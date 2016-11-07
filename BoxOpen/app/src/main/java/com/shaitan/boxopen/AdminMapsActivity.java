package com.shaitan.boxopen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener  {

    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button btnLogout, btnF5, btnC;
    private Session session;

    private  DbHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        btnC = (Button)findViewById(R.id.button);
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
        });

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
        getAllStops();
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(AdminMapsActivity.this,Login.class));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        System.out.println(point.latitude);
        System.out.println(point.longitude);
        long stopId = db.addStop(point.latitude,point.longitude);
        addMarker(stopId,point.latitude,point.longitude);
    }

    private void getAllStops() {
        List<Double[]> stopList = new ArrayList<>();
        stopList.addAll(db.getAllStops());
        for(int i = 0; i<stopList.size(); i++){
            mMap.addMarker(new MarkerOptions()
                    .title("Stop #"+stopList.get(i)[0])
                    .position(new LatLng(stopList.get(i)[2],stopList.get(i)[1] )));
            //addMarker(stopList.get(i)[0],stopList.get(i)[1],stopList.get(i)[2]);
        }
        //db.CLEARSTOPS();
    }
    private void addMarker(double id, double lat, double longt){
        System.out.println("addMarker");
        System.out.println(id);
        System.out.println(lat);
        System.out.println(longt);
        LatLng POS = new LatLng(lat,longt);
                mMap.addMarker(new MarkerOptions()
                        .title("Stop #"+id)
                        .position(new LatLng(lat,longt )));
    }
    private void clearStops(){
        db.CLEARSTOPS();
    }

}
