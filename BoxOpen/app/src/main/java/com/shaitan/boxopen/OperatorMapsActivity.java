package com.shaitan.boxopen;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.location.LocationServices;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OperatorMapsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMapClickListener{

    private  String User, IMEI;
    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button btnLogoutO, btnF5O;
    private ListView boxIdMenu, menuList;
    private ImageButton BoxBtn, btnMenu;
    private Session session;
    private boolean firstUpdate = true;
    private boolean boxIDFlag = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "OperatorMapsActivity";
    private Location LastLocation;
    private Date lastTimeUpdate = new Date();
    private LocationManager LM;
    private Circle circle;
    private List<Double[]> stopList = new ArrayList<>();
    private List<String> stopsIdAvalible = new ArrayList<>();
    private List<String> menuOptions = new ArrayList<>();
    private HashMap<Integer, Marker> MarkersH = new HashMap();


    private crypth crypth;


   // private DbHelper db;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuOptions.add("Update Box");
        menuOptions.add("Logout");
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");

       // db = new DbHelper(this);
        mMap = googleMap;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        session = new Session(this);
        if (!session.loggedin()) {
            logout();
        }


        btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateMenu();
            }
        });
        BoxBtn = (ImageButton) findViewById(R.id.BoxBtn);
        BoxBtn.setEnabled(false);
        BoxBtn.setClickable(false);
        BoxBtn.setElevation(0);
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close_off","mipmap", getPackageName())));
        BoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openBox();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });

/*
        btnF5O = (Button) findViewById(R.id.F5StopsO);
        btnF5O.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getAllStops();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnLogoutO = (Button) findViewById(R.id.btnLogoutO);
        btnLogoutO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        boxIdMenu =(ListView) findViewById(R.id.listView);
        menuList =(ListView) findViewById(R.id.MenuList);
    }

    @Override
    public void onClick(View v) {
    }
    public void inActivateBoxIdMenu(){

        boxIdMenu.setActivated(false);
        boxIdMenu.setVisibility(View.INVISIBLE);
        boxIDFlag = false;
    }
    public void activateBoxIdMenu(){
        boxIdMenu.setActivated(true);
        boxIdMenu.setVisibility(View.VISIBLE);
        boxIDFlag = true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openBox() throws GeneralSecurityException {
            if(boxIDFlag){
                BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close", "mipmap", getPackageName())));
                inActivateBoxIdMenu();
            }
            else{
                BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_open", "mipmap", getPackageName())));
                activateBoxIdMenu();
            }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void sendOpenRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendOpenRequest",User,BoxID).get().toString();
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
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close", "mipmap", getPackageName())));
        inActivateBoxIdMenu();
        int KEY = Integer.parseInt(BoxID);

        Marker m = MarkersH.get(KEY);
        m.remove();
        m.setVisible(false);
        m = null;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        circle = null;
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setBuildingsEnabled(true);


        try {
            getAllStops();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LM = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = LM.getAllProviders();
        Location location = null, temLocation;
        for (String providerT : providers) {
            temLocation = LM.getLastKnownLocation(providerT);
            LastLocation = temLocation;


            if (location == null
                    || (temLocation != null && location.getTime() < temLocation
                    .getTime()))
                location = temLocation;
        }

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .bearing(0)
                        .tilt(90)
                        .zoom(20)
                        .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stopsIdAvalible);
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
                try {
                    menuAction(myItemInt);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        inActivateMenu();
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

    public void menuAction(int optionSelected) throws ExecutionException, InterruptedException {

        switch (optionSelected){
            //Update
            case 0:
                getAllStops();
                break;
            //clear
           /* case 1:
                clearStops();
                break;*/
            //logout
            case 1:
                logout();
                break;
        }
    }

    private void logout() {
        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(OperatorMapsActivity.this, Login.class));
    }

    private void getAllStops() throws ExecutionException, InterruptedException {
        inActivateMenu();
        stopList.clear();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        List<Double[]> stopListJson = new ArrayList<>();

        String incompleteJson = backgroundWorker.execute("getBoxes", User).get().toString();
        String completeJson = "{ "+'"'+"Boxes"+'"'+": "+incompleteJson+"}";

        try {
            JSONObject jsnobject = new JSONObject(completeJson);
            JSONArray jsonArray = jsnobject.getJSONArray("Boxes");
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
        Circle circleStops;

        //for local bd
        //stopList.addAll(db.getAllStops());
        stopsIdAvalible.clear();
        for (int i = 0; i < stopList.size(); i++) {

            stopsIdAvalible.add("Box ID: "+stopList.get(i)[0]);
            MarkerOptions m = new MarkerOptions()
                    .title("Stop #" + stopList.get(i)[0].intValue())
                    .position(new LatLng(stopList.get(i)[2], stopList.get(i)[1]));
            Marker marker = mMap.addMarker(m);
            MarkersH.put(stopList.get(i)[0].intValue(), marker);
            circleStops = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(stopList.get(i)[2], stopList.get(i)[1]))
                    .radius(15)
                    .strokeColor(Color.RED));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        inActivateMenu();
    }

    @Override
    public void onLocationChanged(Location location) {
        double upDistance = 0;
        LatLng updatePos = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng lastPos = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
        upDistance = SphericalUtil.computeDistanceBetween(lastPos, updatePos);

        Date TimeUpdate = new Date();
        long mills = TimeUpdate.getTime() -lastTimeUpdate.getTime();

        long diffSec = mills / 1000;
        long min = diffSec / 60;
        long sec = diffSec % 60;

        if((upDistance>5.0 && sec >=30) || (min >=1 || firstUpdate)) {
            firstUpdate = false;
            try {
                checkDistances(location);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lastTimeUpdate=TimeUpdate;
        }
        if(min>= 1){
            try {
                refreshStops();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkDistances(Location location) throws ExecutionException, InterruptedException {
        List<String> stopsIdAvalibleUp = new ArrayList<>();
        BoxBtn.setClickable(false);
        BoxBtn.setEnabled(false);
        BoxBtn.setElevation(0);
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close_off","mipmap", getPackageName())));
        //da la distancia en Mts

        location.getLatitude();
        location.getAltitude();
        LatLng mPos = new LatLng(location.getLatitude(), location.getLongitude());
        stopList.clear();
        getAllStops();
        for (int i = 0; i < stopList.size(); i++) {
            double distance = 0;
            LatLng markerPos = new LatLng(stopList.get(i)[2], stopList.get(i)[1]);
            distance = SphericalUtil.computeDistanceBetween(mPos, markerPos);

            //si una de los stops esta a menos de 15mts se activa la caja
            if (distance <= 15) {
                stopsIdAvalibleUp.add("Box ID: "+stopList.get(i)[0].intValue());
                BoxBtn.setClickable(true);
                BoxBtn.setEnabled(true);
                BoxBtn.setElevation((float) 10.0);
                BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close","mipmap", getPackageName())));
            }
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stopsIdAvalibleUp);
        boxIdMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(700);
        mLocationRequest.setFastestInterval(350);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    private void clearStops(){
        mMap.clear();
    }
    private void refreshStops() throws ExecutionException, InterruptedException {
        clearStops();
        getAllStops();
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if(i == 1){
//TODO CUANDO HACE ZOOM OUT AL MAXIMO SE PIERDE EL ANGULO
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
        inActivateMenu();
        inActivateBoxIdMenu();
    }
}
