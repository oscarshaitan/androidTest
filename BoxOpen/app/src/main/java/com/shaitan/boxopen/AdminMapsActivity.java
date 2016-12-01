package com.shaitan.boxopen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener{

    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button btnLogout, btnF5, btnC;
    private ImageButton btnMenu;
    private Session session;
    private ListView boxIdMenu, menuList;
    private List<Double[]> stopList = new ArrayList<>();
    private LatLng newMarkerLatLng;

    private  String User, IMEI;
    private ListView operatorListView;
    private List<String[]> operatorList = new ArrayList<>();
    private List<String> menuOptions = new ArrayList<>();
    private List<String> stopsIdAvalibleA = new ArrayList<>();

    //faltalist de operadores con id

    private  DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuOptions.add("Update Box");
        menuOptions.add("Clear all boxes");
        menuOptions.add("Remote open ");
        menuOptions.add("Logout");
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        db = new DbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }

        btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateMenu();
            }
        });
       /* btnC = (Button)findViewById(R.id.button);
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearStops();
            }
        });
        btnF5 = (Button)findViewById(R.id.F5Stops);
        btnF5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStops();
            }
        });
        btnLogout = (Button)findViewById(R.id.btnLogout2);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });*/
        boxIdMenu =(ListView) findViewById(R.id.listView);
        menuList =(ListView) findViewById(R.id.MenuList);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        /*operatorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =((String) (operatorListView.getItemAtPosition(myItemInt)));
                int token = selectedFromList.indexOf(":");
                token ++;
                String id = selectedFromList.substring(token);
                System.out.println("operador ID");
                System.out.println(id);
                addBoxOperator(id);
            }
        });*/
       // operatorListView.setActivated(false);
       // operatorListView.setVisibility(View.INVISIBLE);
        getAllStops();
        getAllOperators();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stopsIdAvalibleA);
        boxIdMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        boxIdMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =((String) (boxIdMenu.getItemAtPosition(myItemInt))).substring(8);
                sendOpenRequest(selectedFromList);
            }
        });
        inActivateBoxIdMenu();

        final ArrayAdapter<String> adapterM = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menuOptions);
        menuList.setAdapter(adapterM);
        adapterM.notifyDataSetChanged();
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                menuAction(myItemInt);
            }
        });
        inActivateMenu();

    }

    public void inActivateBoxIdMenu(){
        boxIdMenu.setActivated(false);
        boxIdMenu.setVisibility(View.INVISIBLE);
        inActivateMenu();
    }

    public void activateBoxIdMenu(){
        boxIdMenu.setActivated(true);
        boxIdMenu.setVisibility(View.VISIBLE);

    }

    public void activateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_act","mipmap", getPackageName())));
        }
        menuList.setActivated(true);
        menuList.setVisibility(View.VISIBLE);
    }


    public void inActivateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle","mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    public void menuAction(int optionSelected){

        switch (optionSelected){
            //Update
            case 0:
                getAllStops();
                break;
            //clear
            case 1:
                clearStops();
                break;
            case 2:
                activateBoxIdMenu();
                break;
            //logout
            case 3:
                logout();
                break;
        }
    }

    public void addBoxOperator(String operatorId){
        String lat = ""+newMarkerLatLng.latitude;
        String lng = ""+newMarkerLatLng.longitude;
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        operatorListView.setActivated(false);
        operatorListView.setVisibility(View.INVISIBLE);
        try {
            String txt = backgroundWorker.execute("addBoxOperator", operatorId,lat, lng).get().toString();
            Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        getAllStops();
        //bd local
        //long stopId = db.addStop(newMarkerLatLng.latitude,newMarkerLatLng.longitude);
        //addMarker(stopId,newMarkerLatLng.latitude,newMarkerLatLng.longitude);

    }

    public void getAllOperators(){
        List<String> operatorIdAvalible = new ArrayList<>();
        List<String[]> operatorListJson = new ArrayList<>();
        operatorIdAvalible.clear();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String incompleteJson = null;

        try {
            incompleteJson = backgroundWorker.execute("getOperators").get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String completeJson = "{ "+'"'+"Operators"+'"'+": "+incompleteJson+"}";


        try {
            JSONObject jsonobject = new JSONObject(completeJson);
            JSONArray jsonArray = jsonobject.getJSONArray("Operators");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] operatorData= new String[3];
                JSONObject explrObject = jsonArray.getJSONObject(i);
                operatorData[0]= (explrObject.getString("id"));
                operatorData[1]= (explrObject.getString("name"))+(explrObject.getString("surname"));
                operatorListJson.add(operatorData);
                operatorIdAvalible.add(operatorData[0]+":"+operatorData[1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        operatorList.addAll(operatorListJson);


       /* final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,operatorIdAvalible);
        operatorListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/
    }

    private void logout(){
        inActivateBoxIdMenu();
        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(AdminMapsActivity.this,Login.class));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        inActivateMenu();
        /*newMarkerLatLng = point;
        operatorListView.setActivated(true);
        operatorListView.setVisibility(View.VISIBLE);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        //type,operador destino, operador transporte, latitud, longitud
        backgroundWorker.execute("addBoxOperator","1","2",""+newMarkerLatLng.latitude,""+newMarkerLatLng.longitude);*/

    }

    private void getAllStops() {
        inActivateBoxIdMenu();
        inActivateMenu();
        stopList.clear();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        List<Double[]> stopListJson = new ArrayList<>();

        String incompleteJson = null;
        try {
            incompleteJson = backgroundWorker.execute("adminGetBoxes").get().toString();
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
                Double[] stopData= new Double[3];
                JSONObject explrObject = jsonArray.getJSONObject(i);
                stopData[0]= Double.valueOf(explrObject.getString("id_caja"));
                stopData[2]= Double.valueOf(explrObject.getString("latitud"));
                stopData[1]= Double.valueOf(explrObject.getString("longitud"));
                stopListJson.add(stopData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stopList.addAll(stopListJson);

        //DB LOCAL
        //stopList.addAll(db.getAllStops());
        stopsIdAvalibleA.clear();
        for(int i = 0; i<stopList.size(); i++){
            stopsIdAvalibleA.add("Box ID: "+stopList.get(i)[0]);
            mMap.addMarker(new MarkerOptions()
                    .title("Stop #"+stopList.get(i)[0])
                    .position(new LatLng(stopList.get(i)[2],stopList.get(i)[1] )));
        }
    }
    private void addMarker(double id, double lat, double longt){
        LatLng POS = new LatLng(lat,longt);
                mMap.addMarker(new MarkerOptions()
                        .title("Stop #"+id)
                        .position(new LatLng(lat,longt )));
    }
    private void clearStops(){
        inActivateBoxIdMenu();
        inActivateMenu();
        //db.CLEARSTOPS();
        mMap.clear();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        inActivateMenu();
        inActivateBoxIdMenu();
    }

    public void sendOpenRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendAdminOpenRequest",BoxID).get().toString();
            Toast.makeText(getApplicationContext(), BoxStatus, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        /*String IMEI = "";

        telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        String BoxStatus = backgroundWorker.execute(User,crypth.AES_Encrypt(IMEI, BoxID), "open").toString();
        if(BoxStatus.toUpperCase().equals("TRUE")) {
            Toast.makeText(getApplicationContext(), "Box lista para abrir.", Toast.LENGTH_SHORT).show();
                     //eliminar marcador de box
        }
        if(BoxStatus.toUpperCase().equals("FALSE")){
            Toast.makeText(getApplicationContext(), "Falta una llave para abrir.", Toast.LENGTH_SHORT).show();
        }*/
        inActivateBoxIdMenu();
        inActivateMenu();
    }
}
