package com.shaitan.boxopen;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TransporterMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "TransporterMapsActivity";
    private LocationManager LM;

    private Button terminarEntrega;
    private ToggleButton chapa;
    private boolean chapaFlag = false;
    private boolean showHideBoxMarkerFlag = false;
    private ImageButton btnMenu;
    private Session session;
    private ListView menuList;
    private TextView TVtemp, TVluz, TVhumedad, TVvoltaje, TVbateria, TVtapa, TVkey, TVsistema;
    private List<Double[]> stopList = new ArrayList<>();
    private List<Double[]> stopListJson = new ArrayList<>();

    private boolean firstUpdate = true;
    private Location LastLocation;
    private Date lastTimeUpdate = new Date();

    private String User, IMEI, IdBox, tipoApertura;
    private Double latitud, longitud, temperatura, luz, humedad, voltajeBateria, porcentajeBateria, estadoTapa, estadoSistema, proximidad;
    private List<String> menuOptions = new ArrayList<>();
    private final int updateVariables = 30000;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        menuOptions.add("Update Box");
        menuOptions.add("Show/Hide Box");
        menuOptions.add("Help");
        menuOptions.add("Logout");

        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        IdBox = bundle.getString("IdBox");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporter_maps);

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

        terminarEntrega = (Button)findViewById(R.id.terminarEntrega);
        terminarEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTerminarEntrega(IdBox);
            }
        });

        chapa = (ToggleButton) findViewById((R.id.chapa));

        activateChapa();

        chapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActivateMenu();
                if (chapaFlag == true) {
                    chapaFlag = false;
                    sendCloseRequest(IdBox);
                    estadoChapa(IdBox);
                } if (chapaFlag == false) {
                    chapaFlag = true;
                    sendOpenRequest(IdBox);
                    estadoChapa(IdBox);
                }
            }
        });

        menuList = (ListView) findViewById(R.id.MenuList);
        TVtemp = (TextView) findViewById(R.id.temp);
        TVluz = (TextView) findViewById(R.id.luz);
        TVhumedad = (TextView) findViewById(R.id.humedad);
        TVvoltaje = (TextView) findViewById(R.id.voltaje);
        TVbateria = (TextView) findViewById(R.id.bateria);
        TVtapa = (TextView) findViewById(R.id.tapa);
        TVkey = (TextView) findViewById(R.id.key);
        TVsistema = (TextView) findViewById(R.id.sistema);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        inActivateMenu();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        inActivateMenu();
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
        mMap.clear();
        getABox(IdBox);
        if (tipoApertura.equals("Dos llaves")){
            chapa.setClickable(true);
            chapa.setEnabled(true);
            if (estadoSistema == 0) {
                chapa.setChecked(false);
            }
            if (estadoSistema == 1) {
                chapa.setChecked(true);
            }
        } else {
            chapa.setClickable(false);
            chapa.setEnabled(false);
            chapa.setChecked(false);
        }
        estadoChapa(IdBox);

        scheduleSendLocation();
        getStopsBox(IdBox);

        final ArrayAdapter<String> adapterM = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuOptions);
        menuList.setAdapter(adapterM);
        adapterM.notifyDataSetChanged();
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                menuAction(myItemInt);
            }
        });
        inActivateMenu();

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
    }

    public void menuAction(int optionSelected) {

        switch (optionSelected) {
            /*
            menuOptions.add("Update Box");
            menuOptions.add("Show/Hide Box");
            menuOptions.add("Help");
            menuOptions.add("Logout");
            * */
            //Update
            case 0:
                getABox(IdBox);
                break;
            //Show/Hide Box
            case 1:
                showHideBoxMarker();
                break;
            //help
            case 2:
            help();
            //  System.out.println("NOT IMPLEMENTED YET");
            break;

            //logout
            case 3:
                logout();
                break;
        }
    }

    private void clearStops() {

        inActivateMenu();
        //db.CLEARSTOPS();
        mMap.clear();
    }

    private void logout() {

        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(TransporterMapsActivity.this, Login.class));
    }

    public void inActivateMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle", "mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    public void activateMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_act", "mipmap", getPackageName())));
        }
        menuList.setActivated(true);
        menuList.setVisibility(View.VISIBLE);
    }

    public void sendOpenRequest(String BoxID) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus = backgroundWorker.execute("sendOpenRequest", User, BoxID).get().toString();
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
        inActivateMenu();
    }

    public void sendCloseRequest(String BoxID) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus = backgroundWorker.execute("sendCloseRequest", User, BoxID).get().toString();
            Toast.makeText(getApplicationContext(), BoxStatus, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        inActivateMenu();
    }

    public void sendTerminarEntrega(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendTerminarEntrega",User,BoxID).get().toString();
            Toast.makeText(getApplicationContext(), BoxStatus, Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        inActivateMenu();
    }

    public void getStopsBox(String IdBox) {
        Circle circleStops;
        int idBox = Integer.parseInt(IdBox);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String incompleteJson2 = null;
        try {
            incompleteJson2 = backgroundWorker.execute("get_puntos", User, "" + idBox).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String completeJson2 = "{ " + '"' + "Puntos" + '"' + ": " + incompleteJson2 + "}";


        try {
            JSONObject jsonobject2 = new JSONObject(completeJson2);
            JSONArray jsonArray2 = jsonobject2.getJSONArray("Puntos");

            for (int i = 0; i < jsonArray2.length(); i++) {
                Double[] stopData = new Double[3];
                JSONObject explrObject2 = jsonArray2.getJSONObject(i);
                stopData[0] = Double.valueOf(explrObject2.getString("id"));

                stopData[1] = Double.valueOf(explrObject2.getString("latitud"));
                stopData[2] = Double.valueOf(explrObject2.getString("longitud"));
                stopListJson.add(stopData);
                mMap.addMarker(new MarkerOptions()
                        .title("Stop #" + stopData[0] + " " + explrObject2.getString("nombre"))
                        .position(new LatLng(stopData[1], stopData[2]))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                circleStops = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(stopData[1], stopData[2]))
                        .radius(15)
                        .strokeColor(Color.RED));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getABox(String IdBox) {
        int idBox = Integer.parseInt(IdBox);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String incompleteJson = null;

        try {
            incompleteJson = backgroundWorker.execute("GetBoxInfo", User, "" + idBox).get().toString();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String completeJson = "{ " + '"' + "Box" + '"' + ": " + incompleteJson + "}";

        try {
            JSONObject jsonobject = new JSONObject(completeJson);
            JSONArray jsonArray = jsonobject.getJSONArray("Box");


            JSONObject explrObject = jsonArray.getJSONObject(0);
            latitud = Double.valueOf(explrObject.getString("latitud"));
            longitud = Double.valueOf(explrObject.getString("longitud"));
            temperatura = Double.valueOf(explrObject.getString("Temperatura"));
            luz = Double.valueOf(explrObject.getString("Luz"));
            humedad = Double.valueOf(explrObject.getString("Humedad"));
            voltajeBateria = Double.valueOf(explrObject.getString("voltaje_bateria"));
            porcentajeBateria = Double.valueOf(explrObject.getString("porcentaje_bateria"));
            estadoSistema = Double.valueOf(explrObject.getString("lock_status"));
            estadoTapa = Double.valueOf(explrObject.getString("estado_tapa"));
            tipoApertura = explrObject.getString("tipo_apertura");
            proximidad = Double.valueOf(explrObject.getString("proximidad"));
            TVtemp.setText(" " + temperatura + "º");
            TVluz.setText(" " + luz);
            TVhumedad.setText("" + humedad + "%");
            DecimalFormat df2 = new DecimalFormat(".##");
            TVvoltaje.setText("" + df2.format(voltajeBateria / 1000) + "V");
            TVbateria.setText("" + porcentajeBateria + "%");

            String estadoTapaS = "...";
            if (estadoTapa == 0) {
                estadoTapaS = "Cerrada";
            }
            if (estadoTapa == 1) {
                estadoTapaS = "Abierta";
            }
            if (estadoTapa == -1) {
                estadoTapaS = "Error";
            }

            TVtapa.setText(estadoTapaS);

            if (tipoApertura == "null") {
                tipoApertura = "No  asignado";
            }
            TVkey.setText("" + tipoApertura);

            String estadoSistemaS = "...";

            if (estadoSistema == 0) {
                estadoSistemaS = "Bloqueada";
            }
            if (estadoSistema == 1) {
                estadoSistemaS = "Liberada";
            }
            if (estadoSistema == -1) {
                estadoSistemaS = "Error";
            }
            TVsistema.setText(estadoSistemaS);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        inActivateMenu();
    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getABox(IdBox);
                estadoChapa(IdBox);
                handler.postDelayed(this, updateVariables);
            }
        }, updateVariables);
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        //super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(TransporterMapsActivity.this, TransporterBoxList.class);
        intent.putExtra("User", User);
        intent.putExtra("Imei", IMEI);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        if (!tipoApertura.equals("Dos llaves")) {
            double upDistance = 0;
            LatLng updatePos = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng lastPos = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
            upDistance = SphericalUtil.computeDistanceBetween(lastPos, updatePos);

            Date TimeUpdate = new Date();
            long mills = TimeUpdate.getTime() - lastTimeUpdate.getTime();

            long diffSec = mills / 1000;
            long min = diffSec / 60;
            long sec = diffSec % 60;

            if ((upDistance > 5.0 && sec >= 30) || (min >= 1 || firstUpdate)) {
                firstUpdate = false;
                try {
                    LastLocation.setLatitude(location.getLatitude());
                    LastLocation.setLongitude(location.getLongitude());
                    checkDistances(location);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lastTimeUpdate = TimeUpdate;
                backgroundWorker.execute("sendTransporterLocation", User, "" + location.getLatitude(), "" + location.getLongitude());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkDistances(Location location) throws ExecutionException, InterruptedException {
        LatLng mPos = new LatLng(location.getLatitude(), location.getLongitude());

        double boxDistance = 0;
        LatLng boxPos = new LatLng(latitud, longitud);
        boxDistance = SphericalUtil.computeDistanceBetween(mPos, boxPos);//usuario caja
        boolean boxDistanceF = false;
        if (boxDistance <= 15) {
            boxDistanceF = true;
        }

        boolean stopDistance = false;
        for (int i = 0; i < stopListJson.size(); i++) {
            double distance = 0;
            LatLng markerPos = new LatLng(stopListJson.get(i)[1], stopListJson.get(i)[2]);
            distance = SphericalUtil.computeDistanceBetween(boxPos, markerPos); //caja parada
            if (distance <= proximidad) {
                stopDistance = true;
            }
        }
        // si el transportador esta a 15mts de la caja y la parada segura esta dentro del radio de apertura de la caja
        if (boxDistanceF && stopDistance) {
            chapa.setEnabled(true);
            chapa.setClickable(true);
        }
    }

    public void activateChapa() {
        chapa.setClickable(true);
        chapa.setEnabled(true);
        estadoChapa(IdBox);
    }

    public void estadoChapa(String BoxID) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String estadoLlave = "";
        try {
            estadoLlave = backgroundWorker.execute("llaveTransportador", User, BoxID).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (estadoLlave.equals("1")) {
            chapa.setChecked(true);
            chapaFlag =true;
        }
        if (estadoLlave.equals("0")) {
            chapa.setChecked(false);
            chapaFlag = false;
        }
    }

    public void showHideBoxMarker() {
        if (!showHideBoxMarkerFlag) {
            mMap.clear();
            getStopsBox(IdBox);
            mMap.addMarker(new MarkerOptions()
                    .title("Box #" + (IdBox))
                    .position(new LatLng(latitud, longitud)));
            showHideBoxMarkerFlag = true;
        } else {
            mMap.clear();
            getStopsBox(IdBox);
            showHideBoxMarkerFlag = false;
        }
        inActivateMenu();
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

    public void help(){
        Intent intent = new Intent(TransporterMapsActivity.this, Help.class);
        intent.putExtra("User",User);
        intent.putExtra("Imei",IMEI);
        intent.putExtra("IdBox",""+IdBox);
        intent.putExtra("PrevActivity", "TransporterMapsActivity");
        startActivity(intent);
        finish();
    }

}
