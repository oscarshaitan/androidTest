package com.shaitan.boxopen;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class AdminBoxList extends AppCompatActivity implements View.OnClickListener{
    private ListView boxMenu, menuList;
    private Session session;
    private boolean firstUpdate = true;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "AdminBoxList";
    private Location LastLocation;
    private Date lastTimeUpdate = new Date();
    private LocationManager LM;
    private List<Double[]> stopList = new ArrayList<>();
    private LatLng newMarkerLatLng;
    private List<String> menuOptions = new ArrayList<>();
    private List<String> boxsAvalible = new ArrayList<>();
    final Handler handler = new Handler();
    private final int updateVariables = 30000;
    private ImageButton btnMenu;
    private  String User, IMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuOptions.add(getString(R.string.menuOptions_update));
        menuOptions.add(getString(R.string.logout));
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_admin_box_list);
        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }
        btnMenu = (ImageButton) findViewById(R.id.menuBtn);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateMenu();
            }
        });
        menuList =(ListView) findViewById(R.id.MenuList);
        final ArrayAdapter<String> adapterM = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menuOptions);
        menuList.setAdapter(adapterM);
        adapterM.notifyDataSetChanged();
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                menuAction(myItemInt);
            }
        });
        inActivateMenu();
        boxMenu =(ListView) findViewById(R.id.BoxList);
        getAllBoxs();
        scheduleSendLocation();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,boxsAvalible);
        boxMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        boxMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =((String) (boxMenu.getItemAtPosition(myItemInt)));
                int I = selectedFromList.indexOf("{")+1;
                int F = selectedFromList.indexOf("}");
                String idBox = selectedFromList.substring(I,F);
                sendViewBoxDetails(Integer.parseInt(idBox));
            }
        });
    }

    public void menuAction(int optionSelected){
        switch (optionSelected){
            //Update
            case 0:
                getAllBoxs();
                break;
            //clear
            case 1:
                logout();
                break;
        }
    }

    public void activateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_act","mipmap", getPackageName())));
        }
        menuList.setActivated(true);
        menuList.setVisibility(View.VISIBLE);
    }

    private void logout(){
        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(AdminBoxList.this,Login.class));
    }

    public void inActivateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle","mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    private void getAllBoxs() {
        //inActivateBoxIdMenu();
        inActivateMenu();
        stopList.clear();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        List<Double[]> stopListJson = new ArrayList<>();
        String incompleteJson = null;
        try {
            incompleteJson = backgroundWorker.execute("adminGetBoxes",User).get().toString();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),R.string.CNX_Error, Toast.LENGTH_LONG).show();
        }
        if(incompleteJson.equals("ERROR")){
            Toast.makeText(this, R.string.CNX_Error, Toast.LENGTH_SHORT).show();
        }
        else if(!incompleteJson.equals("No hay entregas activas en este momento.")){
            String completeJson = "{ "+'"'+"Boxes"+'"'+": "+incompleteJson+"}";
            try {
                JSONObject jsonobject = new JSONObject(completeJson);
                JSONArray jsonArray = jsonobject.getJSONArray("Boxes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Double[] stopData= new Double[9];
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    stopData[0]= Double.valueOf(explrObject.getString("id_caja"));
                    stopData[2]= Double.valueOf(explrObject.getString("latitud"));
                    stopData[1]= Double.valueOf(explrObject.getString("longitud"));
                    stopData[3]= Double.valueOf(explrObject.getString("Temperatura"));
                    stopData[4]= Double.valueOf(explrObject.getString("Luz"));
                    stopData[5]= Double.valueOf(explrObject.getString("Humedad"));
                    stopData[6]= Double.valueOf(explrObject.getString("voltaje_bateria"));
                    stopData[7]= Double.valueOf(explrObject.getString("porcentaje_bateria"));
                    stopData[8]= Double.valueOf(explrObject.getString("estado_tapa"));
                    stopListJson.add(stopData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                Toast.makeText(getApplicationContext(), R.string.Error_box_list, Toast.LENGTH_LONG).show();
            }
            stopList.addAll(stopListJson);
            boxsAvalible.clear();
            for (int i = 0; i < stopList.size(); i++) {
                String estado_tapa= "";
                if(stopList.get(i)[8] == 0){
                    estado_tapa=getString(R.string.close);
                }
                if(stopList.get(i)[8] == 1){
                    estado_tapa=getString(R.string.open);
                }
                if(stopList.get(i)[8] == 1){
                    estado_tapa = getString(R.string.Error);
                }
                boxsAvalible.add(getString(R.string.ID_box) +stopList.get(i)[0].intValue()+ getString(R.string.Temp) +stopList.get(i)[3]+ getString(R.string.Battery) +stopList.get(i)[7]+ getString(R.string.cover) +estado_tapa);
            }
            prepareBoxList();
        }
        else{
            Toast.makeText(getApplicationContext(), incompleteJson, Toast.LENGTH_LONG).show();
        }
    }

    public void prepareBoxList(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,boxsAvalible);
        boxMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*boxMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =((String) (boxMenu.getItemAtPosition(myItemInt))).substring(8);
                sendViewBoxDetails(selectedFromList);
            }
        });*/
        //inActivateBoxIdMenu();
    }

    public void sendViewBoxDetails(int IdBox){
        Intent intent = new Intent(AdminBoxList.this, AdminMapsActivity.class);
        intent.putExtra("User",User);
        intent.putExtra("Imei",IMEI);
        intent.putExtra("IdBox",""+IdBox);
        startActivity(intent);
        finish();
       // User, IdBox, IMEI;
    }

    @Override
    public void onClick(View v) {
        //inActivateBoxIdMenu();
    }

    @Override
    public void onBackPressed()
    {
        logout();
        finish();
    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getAllBoxs();
                handler.postDelayed(this, updateVariables);
            }
        }, updateVariables);
    }
}