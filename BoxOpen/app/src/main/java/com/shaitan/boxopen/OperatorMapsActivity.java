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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;

public class OperatorMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private GoogleMap googleMap;
    private GoogleMap mMap = googleMap;
    private Button btnLogoutO, btnF5O;
    private ImageButton BoxBtn;
    private Session session;
    private String provider;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "OperatorMapsActivity";
    LocationManager LM;
    Circle circle;

    private DbHelper db;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DbHelper(this);
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
        BoxBtn = (ImageButton) findViewById(R.id.BoxBtn);
        BoxBtn.setEnabled(false);
        BoxBtn.setClickable(false);
        BoxBtn.setElevation(0);
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close_off","mipmap", getPackageName())));
        BoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBox();
            }
        });

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openBox() {
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_open", "mipmap", getPackageName())));
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
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setBuildingsEnabled(true);

        getAllStops();
        LM = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = LM.getAllProviders();
        Location location = null, temLocation;
        for (String providerT : providers) {
            temLocation = LM.getLastKnownLocation(providerT);

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
    }

    private void logout() {
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(OperatorMapsActivity.this, Login.class));
    }

    private void getAllStops() {
        Circle circleStops;
        List<Double[]> stopList = new ArrayList<>();
        stopList.addAll(db.getAllStops());
        for (int i = 0; i < stopList.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .title("Stop #" + stopList.get(i)[0])
                    .position(new LatLng(stopList.get(i)[2], stopList.get(i)[1])));
            circleStops = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(stopList.get(i)[2], stopList.get(i)[1]))
                    .radius(15)
                    .strokeColor(Color.RED));

        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onLocationChanged(Location location) {

        checkDistances(location);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkDistances(Location location) {
        BoxBtn.setClickable(false);
        BoxBtn.setEnabled(false);
        BoxBtn.setElevation(0);
        BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close_off","mipmap", getPackageName())));
        //da la distancia en Mts
        System.out.println("CALCULATING TEST DISTANCE");
        location.getLatitude();
        location.getAltitude();
        LatLng mPos = new LatLng(location.getLatitude(), location.getLongitude());
        List<Double[]> stopList = new ArrayList<>();
        stopList.addAll(db.getAllStops());
        for (int i = 0; i < stopList.size(); i++) {
            double distance = 0;
            LatLng markerPos = new LatLng(stopList.get(i)[2], stopList.get(i)[1]);
            distance = SphericalUtil.computeDistanceBetween(mPos, markerPos);

            //si una de los stops esta a menos de 15mts se activa la caja
            if (distance <= 15) {
                BoxBtn.setClickable(true);
                BoxBtn.setEnabled(true);
                BoxBtn.setElevation((float) 10.0);
                BoxBtn.setImageDrawable(getDrawable(getResources().getIdentifier("box_close","mipmap", getPackageName())));
            }


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
}
