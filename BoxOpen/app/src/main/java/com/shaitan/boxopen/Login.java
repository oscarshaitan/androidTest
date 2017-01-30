package com.shaitan.boxopen;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login;
    private EditText pass,user;
    private crypth crypth = new crypth();
    private Session session;
    TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        session = new Session(this);
        login = (Button)findViewById(R.id.buttonL);
        user = (EditText)findViewById(R.id.userText);
        pass = (EditText)findViewById(R.id.passText);
        login.setOnClickListener(this);
        telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        session.setLoggedin(false);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.buttonL:
                try {
                    login();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    private  void login() throws UnsupportedEncodingException, NoSuchAlgorithmException, ExecutionException, InterruptedException {
        String userL = user.getText().toString();
        String passL = pass.getText().toString();
        String cargo = "";
        String token = "TRUE";
        try {
       /*
            //System.out.println(crypth.AES_Encrypt("USER1IMEITEST00000000000", "TRUE" ));

            //String test = crypth.AES_Encrypt("USER1IMEITEST00000000000", "TRUE" );
            //System.out.println(crypth.AES_Decrypt("USER1IMEITEST00000000000", test));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
            //System.out.println(crypth.AES_E());

            //System.out.println(crypth.AES_D());

            String IMEI = telephonyManager.getDeviceId();
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);

            //rol = backgroundWorker.execute(userL, crypth.AES_Encrypt(IMEI, crypth.SHA1(crypth.MD5(passL))), "login").toString();
            String result = backgroundWorker.execute("login", userL, passL).get().toString();
            cargo = result;


            //Check GPS available
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, R.string.GPS_Error, Toast.LENGTH_SHORT).show();
            }
            if(result.equals("ERROR")){
                Toast.makeText(this, R.string.CNX_Error, Toast.LENGTH_SHORT).show();
            }
            else {
                if (cargo.equals("1")) {//ADMIN

                    session.setLoggedin(true);

                    Intent intent = new Intent(Login.this, AdminBoxList.class);
                    intent.putExtra("User", userL);
                    intent.putExtra("Imei", IMEI);
                    startActivity(intent);
                    finish();
                } else if (cargo.equals("2")) {//TRANSPORTADOR

                    session.setLoggedin(true);
                    Intent intent = new Intent(Login.this, TransporterBoxList.class);
                    intent.putExtra("User", userL);
                    intent.putExtra("Imei", IMEI);
                    startActivity(intent);
                    finish();
                } else if (cargo.equals("3")) {//DESTINATARIO
                    session.setLoggedin(true);
                    Intent intent = new Intent(Login.this, DestinatarioBoxList.class);
                    intent.putExtra("User", userL);
                    intent.putExtra("Imei", IMEI);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.Login_error, Toast.LENGTH_SHORT).show();
            }
        }
        }catch(NullPointerException e){
            Toast.makeText(getApplicationContext(),  R.string.CNX_Error, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            e.printStackTrace();
        }
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

}
