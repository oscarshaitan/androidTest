package com.shaitan.boxopen;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.CameraUpdateFactory;

public class DestinatarioMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    TelephonyManager telephonyManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "DestinatariaMapsActivity";
    private ToggleButton chapa;
    private boolean chapaFlag = false;
    private boolean firstload = true;
    private ImageButton btnMenu;
    private Session session;
    private ListView menuList;
    private TextView TVtemp, TVluz, TVhumedad, TVvoltaje, TVbateria, TVtapa, TVkey, TVsistema, Gpsfail;
    private List<Double[]> stopList = new ArrayList<>();
    private  String User, IMEI, IdBox, tipoApertura;
    private  Double latitud, longitud, temperatura, luz, humedad, voltajeBateria, porcentajeBateria,  estadoSistema, estadoTapa, proximidad;
    private boolean firstUpdate = true;
    private boolean showStopsMarkerFlag = false;
    private Location LastLocation;
    private Date lastTimeUpdate = new Date();
    private List<String> menuOptions = new ArrayList<>();
    private final int updateVariables = 30000;
    final Handler handler = new Handler();
    private List<Double[]> stopListJson = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        menuOptions.add(getString(R.string.menuOptions_update));
        menuOptions.add(getString(R.string.Show_hide_stop));
        menuOptions.add(getString(R.string.help));
        menuOptions.add(getString(R.string.back));
        menuOptions.add(getString(R.string.logout));
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        IdBox = bundle.getString("IdBox");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinatario_maps);
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
        chapa = (ToggleButton)findViewById((R.id.chapa));
        activateChapa();
        chapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActivateMenu();
                if (chapaFlag == true) {
                    chapaFlag = false;
                    sendCloseRequest(IdBox);
                    estadoChapa(IdBox);
                }if (chapaFlag == false) {
                    chapaFlag = true;
                    sendOpenRequest(IdBox);
                    estadoChapa(IdBox);
                }
            }
        });
        menuList =(ListView) findViewById(R.id.MenuList);
        TVtemp = (TextView)findViewById(R.id.temp);
        TVluz = (TextView)findViewById(R.id.luz);
        TVhumedad = (TextView)findViewById(R.id.humedad);
        TVvoltaje = (TextView)findViewById(R.id.voltaje);
        TVbateria = (TextView)findViewById(R.id.bateria);
        TVtapa = (TextView)findViewById(R.id.tapa);
        TVkey = (TextView)findViewById(R.id.key);
        TVsistema = (TextView)findViewById(R.id.sistema);
        Gpsfail =  (TextView) findViewById(R.id.gpsfail);
        Gpsfail.setVisibility(View.VISIBLE);
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
        telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void estadoChapa(String BoxID) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String estadoLlave = "";
        try {
            estadoLlave = backgroundWorker.execute("llaveDestinatario", User, BoxID).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "ERROR al intentar solicitar el valor de la llave Destinatario contactar con soporte", Toast.LENGTH_LONG).show();
        }
        if(estadoLlave.equals("ERROR")){
            Toast.makeText(this, "Hay un error de conexión, verificar su acceso a internet ", Toast.LENGTH_SHORT).show();
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

    public void getABox(String IdBox){
        int idBox= Integer.parseInt(IdBox);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String incompleteJson = null;
        try {
            incompleteJson = backgroundWorker.execute("GetBoxInfo",User, ""+idBox).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "ERROR de conexción al tratar de recuperar la información de la caja, contactar con soporte", Toast.LENGTH_LONG).show();
        }
        if(incompleteJson.equals("ERROR")){
            Toast.makeText(this, "Hay un error de conexión, verificar su acceso a internet ", Toast.LENGTH_SHORT).show();
        }
        else {
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
                TVvoltaje.setText("" + voltajeBateria / 1000 + "V");
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

                mMap.addMarker(new MarkerOptions()
                        .title("Caja #" + (idBox + 1))
                        .position(new LatLng(latitud, longitud)));
                inActivateMenu();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "No se recuperó información de la caja, contactar con soporte", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                checkConnection();
                availableGPS();
                refresMap();
                getMyPos();
                estadoChapa(IdBox);
                handler.postDelayed(this, updateVariables);
            }
        }, updateVariables);
    }

    public void activateChapa() {
        chapa.setClickable(true);
        chapa.setEnabled(true);
        estadoChapa(IdBox);
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
            //Show/Hide stop
            case 1:
                showHideStopsMarker();
                break;
            //help
            case 2:
                help();
                break;
            //atras
            case 3:
                onBackPressed();
                break;
             //logout
            case 4:
                logout();
                break;
        }
    }

    public void showHideStopsMarker() {
        if(showStopsMarkerFlag){
            showStopsMarkerFlag = false;
        }
        else{
            showStopsMarkerFlag = true;
        }
        refresMap();
        inActivateMenu();
        inActivateMenu();
    }

    public void refresMap(){
        mMap.clear();

        if (showStopsMarkerFlag) {
            getABox(IdBox);
            getStopsBox(IdBox);
        } else {
            getABox(IdBox);
        }
    }

    private void clearStops(){
        inActivateMenu();
        //db.CLEARSTOPS();
        mMap.clear();
    }

    private void logout(){
        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(DestinatarioMapsActivity.this,Login.class));
    }

    public void inActivateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle","mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    public void activateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_act","mipmap", getPackageName())));
        }
        menuList.setActivated(true);
        menuList.setVisibility(View.VISIBLE);
    }

    public void sendOpenRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendOpenRequest",User,BoxID).get().toString();
            if(BoxStatus.equals("ERROR")){
                Toast.makeText(this, "Hay un error de conexión, verificar su acceso a internet ", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), BoxStatus, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "ERROR al intentar solicitar apertura, contactar con soporte", Toast.LENGTH_LONG).show();
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

    public void sendCloseRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendCloseRequest",User,BoxID).get().toString();
            if(BoxStatus.equals("ERROR")){
                Toast.makeText(this, "Hay un error de conexión, verificar su acceso a internet ", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), BoxStatus, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "ERROR al intentar solicitar cierre, contactar con soporte", Toast.LENGTH_LONG).show();
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
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), R.string.Error_box_list, Toast.LENGTH_LONG).show();
        }
        if(incompleteJson2.equals("ERROR")){
            Toast.makeText(this, R.string.CNX_Error, Toast.LENGTH_SHORT).show();
        }
        else {
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
                            .title(explrObject2.getString("nombre"))
                            .position(new LatLng(stopData[1], stopData[2]))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    circleStops = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(stopData[1], stopData[2]))
                            .radius(15)
                            .strokeColor(Color.RED));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), R.string.Error_stop_list, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        // code here to show dialog
        //super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(DestinatarioMapsActivity.this, DestinatarioBoxList.class);
        intent.putExtra("User",User );
        intent.putExtra("Imei",IMEI);
        startActivity(intent);
        finish();
    }

    public void getMyPos() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        Location temLocation;
        temLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(temLocation == null){
            Gpsfail.setVisibility(View.VISIBLE);
        }
        else {
            Gpsfail.setVisibility(View.INVISIBLE);
            if (firstload) {
                firstload = false;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(temLocation.getLatitude(), temLocation.getLongitude()), 70), 5000, null);
                LastLocation = temLocation;
            }
            if (!tipoApertura.equals("Dos llaves")) {
                double upDistance = 0;
                LatLng updatePos = new LatLng(temLocation.getLatitude(), temLocation.getLongitude());
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
                        checkDistances(temLocation);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastTimeUpdate = TimeUpdate;
                    LastLocation = temLocation;
                }
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
        getMyPos();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    public void help(){
        Intent intent = new Intent(DestinatarioMapsActivity.this, Help.class);
        intent.putExtra("User",User);
        intent.putExtra("Imei",IMEI);
        intent.putExtra("IdBox",""+IdBox);
        intent.putExtra("PrevActivity", "DestinatarioMapsActivity");
        startActivity(intent);
        finish();
    }

    public void checkConnection(){
        String result= "";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        try {
            result = backgroundWorker.execute("Test",User).get().toString();
            if (result != "TRUE" && result == ""){
                Toast.makeText(getApplicationContext(), R.string.CNX_Error, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), R.string.CNX_Error, Toast.LENGTH_LONG).show();
        }
    }

    public boolean availableGPS(){
        //Check GPS available
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Gpsfail.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            Gpsfail.setVisibility(View.INVISIBLE);
            return true;
        }
    }
}