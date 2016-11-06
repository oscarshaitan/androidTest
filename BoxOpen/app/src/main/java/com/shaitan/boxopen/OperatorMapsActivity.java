package com.shaitan.boxopen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class OperatorMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button btnLogoutO, btnF5O;
    private Session session;
    private Camera camera;
    CameraPosition cameraPosition;

    private DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }



        btnF5O = (Button) findViewById(R.id.F5StopsO);
        btnF5O.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllStops();
            }
        });
        btnLogoutO = (Button) findViewById(R.id.btnLogoutO);
        btnLogoutO.setOnClickListener(new View.OnClickListener() {
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
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setBuildingsEnabled(true);
        /*CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(0,0))
                        .bearing(0)
                        .tilt(23)
                        .zoom(googleMap.getCameraPosition().zoom)
                        .build();
        camera = new Camera();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        getAllStops();

    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(OperatorMapsActivity.this,Login.class));
    }

    private void getAllStops() {
        List<Double[]> stopList = new ArrayList<>();
        stopList.addAll(db.getAllStops());
        for(int i = 0; i<stopList.size(); i++){
            mMap.addMarker(new MarkerOptions()
                    .title("Stop #"+stopList.get(i)[0])
                    .position(new LatLng(stopList.get(i)[2],stopList.get(i)[1] )));

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
