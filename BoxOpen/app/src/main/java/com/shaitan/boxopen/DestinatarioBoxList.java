package com.shaitan.boxopen;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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

public class DestinatarioBoxList extends AppCompatActivity {
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

    private ImageButton btnMenu;

    private  String User, IMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuOptions.add("Update Box");
        menuOptions.add("Logout");
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinatario_box_list);

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
        getAllStops();
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
                getAllStops();
                break;
            //logout
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
        startActivity(new Intent(DestinatarioBoxList.this,Login.class));
    }

    public void inActivateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle","mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    private void getAllStops() {

        inActivateMenu();
        stopList.clear();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        List<Double[]> stopListJson = new ArrayList<>();

        String incompleteJson = null;
        try {
            incompleteJson = backgroundWorker.execute("getBoxesEmpresa",User).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String completeJson = "{ "+'"'+"Boxes"+'"'+": "+incompleteJson+"}";


        try {
            JSONObject jsonobject = new JSONObject(completeJson);
            JSONArray jsonArray = jsonobject.getJSONArray("Boxes");
            for (int i = 0; i < jsonArray.length(); i++) {
                Double[] stopData= new Double[10];
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
                stopData[9]= Double.valueOf(explrObject.getString("lock_status"));

                stopListJson.add(stopData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stopList.addAll(stopListJson);
        boxsAvalible.clear();
        for (int i = 0; i < stopList.size(); i++) {
            String estado_tapa= "";
            if(stopList.get(i)[8] == 0){
                estado_tapa="Cerrada";
            }
            if(stopList.get(i)[8] == 1){
                estado_tapa="Abierta";
            }
            boxsAvalible.add("Box ID: {" +stopList.get(i)[0].intValue()+ "} Temp: " +stopList.get(i)[3]+ " %Bateria: " +stopList.get(i)[7]+ " Tapa:  " +estado_tapa);
        }

        prepareBoxList();

    }

    public void prepareBoxList(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,boxsAvalible);
        boxMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void sendViewBoxDetails(int IdBox){
        Intent intent = new Intent(DestinatarioBoxList.this, DestinatarioMapsActivity.class);
        intent.putExtra("User",User);
        intent.putExtra("Imei",IMEI);
        intent.putExtra("IdBox",""+IdBox);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed()
    {
        logout();
        finish();
    }

}