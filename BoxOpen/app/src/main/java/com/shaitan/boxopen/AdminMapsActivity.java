package com.shaitan.boxopen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener{

    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button terminarEntrega;
    private ImageButton btnMenu;
    private ToggleButton chapa;
    private boolean chapaFlag = false;
    private boolean menuFlag = true;
    private Session session;
    private ListView menuList;
    private TextView TVtemp, TVluz, TVhumedad, TVvoltaje, TVbateria, TVtapa, TVkey, TVsistema;


    private  String User, IMEI, IdBox, tipoApertura;
    private  Double latitud, longitud, temperatura, luz, humedad, voltajeBateria, porcentajeBateria, estadoSistema, estadoTapa;
    private List<String> menuOptions = new ArrayList<>();
    private final int updateVariables = 30000;
    final Handler handler =new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuOptions.add("Update Box");
        menuOptions.add("Help");
        menuOptions.add("Logout");
        Bundle bundle = getIntent().getExtras();
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        IdBox = bundle.getString("IdBox");
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
                if(menuFlag) {
                    activateMenu();
                }
                else{
                    inActivateMenu();
                }
            }
        });

        terminarEntrega = (Button)findViewById(R.id.terminarEntrega);
        terminarEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTerminarEntrega(IdBox);
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
        TVsistema = (TextView)findViewById(R.id.sistema);
        TVtapa = (TextView)findViewById(R.id.tapa);
        TVkey = (TextView)findViewById(R.id.key);


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
        activateChapa();
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
                getABox(IdBox);
                break;
            //help
            case 1:
                help();
                //  System.out.println("NOT IMPLEMENTED YET");
                break;
            //logout
            case 2:
                logout();
                break;
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
        }
        String completeJson = "{ "+'"'+"Box"+'"'+": "+incompleteJson+"}";

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
            TVtemp.setText(" "+temperatura+"º");
            TVluz.setText(" "+luz);
            TVhumedad.setText(""+humedad+"%");
            DecimalFormat df2 = new DecimalFormat(".##");
            TVvoltaje.setText(""+df2.format(voltajeBateria/1000)+"V");
            TVbateria.setText(""+porcentajeBateria+"%");

            String estadoTapaS = "...";
            if(estadoTapa == 0){
                estadoTapaS="Cerrada";
            }
            if(estadoTapa == 1){
                estadoTapaS="Abierta";
            }
            if(estadoTapa == -1){
                estadoTapaS = "Error";
            }

            TVtapa.setText(estadoTapaS);

            if(tipoApertura == "null"){
                tipoApertura = "No  asignado";
            }
            TVkey.setText(""+tipoApertura);

            String estadoSistemaS = "...";

            if(estadoSistema == 0){
                estadoSistemaS="Bloqueada";
            }
            if(estadoSistema == 1){
                estadoSistemaS="Liberada";
            }
            if(estadoSistema == -1){
                estadoSistemaS="Error";
            }
            TVsistema.setText(estadoSistemaS);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .title("Box #"+ (idBox+1))
                .position(new LatLng(latitud, longitud)));

        inActivateMenu();
    }

    public void getStopsBox(String IdBox){

        Circle circleStops;
        List<Double[]> stopListJson = new ArrayList<>();
        int idBox= Integer.parseInt(IdBox);
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String incompleteJson2 = null;



        try {
            incompleteJson2 = backgroundWorker.execute("get_puntos",User, ""+idBox).get().toString();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String completeJson2 = "{ "+'"'+"Puntos"+'"'+": "+incompleteJson2+"}";


        try {
            JSONObject jsonobject2 = new JSONObject(completeJson2);
            JSONArray jsonArray2 = jsonobject2.getJSONArray("Puntos");

            for (int i = 0; i < jsonArray2.length(); i++) {
                Double[] stopData= new Double[3];
                JSONObject explrObject2 = jsonArray2.getJSONObject(i);
                stopData[0]= Double.valueOf(explrObject2.getString("id"));

                stopData[1]= Double.valueOf(explrObject2.getString("latitud"));
                stopData[2]= Double.valueOf(explrObject2.getString("longitud"));
                stopListJson.add(stopData);
                mMap.addMarker(new MarkerOptions()
                        .title("Stop #"+ stopData[0] + " "+ explrObject2.getString("nombre"))
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

    private void logout(){
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

    private void clearStops(){
        inActivateMenu();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        inActivateMenu();
    }

    public void sendOpenRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendAdminOpenRequest",User,BoxID).get().toString();
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

    public void sendCloseRequest(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        try {
            String BoxStatus  = backgroundWorker.execute("sendCloseRequest",User,BoxID).get().toString();
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

    @Override
    public void onBackPressed(){
        // code here to show dialog
        //super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(AdminMapsActivity.this, AdminBoxList.class);
        intent.putExtra("User",User );
        intent.putExtra("Imei",IMEI);
        startActivity(intent);
        finish();
    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getABox(IdBox);
                getStopsBox(IdBox);
                estadoChapa(IdBox);// this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, updateVariables);
            }
        }, updateVariables);
    }

    public void activateChapa(){
        chapa.setClickable(true);
        chapa.setEnabled(true);
        estadoChapa(IdBox);
    }

    public void estadoChapa(String BoxID){
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String estadoLlave = "";
        try {
            estadoLlave = backgroundWorker.execute("llaveDestinatario", User, BoxID).get().toString();
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

    public void inactivateChapa() {
        chapa.setClickable(false);
        chapa.setEnabled(false);
        chapa.setChecked(false);
    }

    public void help(){
        Intent intent = new Intent(AdminMapsActivity.this, Help.class);
        intent.putExtra("User",User);
        intent.putExtra("Imei",IMEI);
        intent.putExtra("IdBox",""+IdBox);
        intent.putExtra("PrevActivity", "AdminMapsActivity");
        startActivity(intent);
        finish();
    }
}
