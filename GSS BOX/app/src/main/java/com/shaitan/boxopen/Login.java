package com.shaitan.boxopen;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login, register;
    private EditText pass,user,cargo;
    private DbHelper db;
    private crypth crypth = new crypth();
    private Session session;
    TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DbHelper(this);
        session = new Session(this);
        login = (Button)findViewById(R.id.buttonL);
        register = (Button)findViewById(R.id.buttonR);
        user = (EditText)findViewById(R.id.userText);
        pass = (EditText)findViewById(R.id.passText);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
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
            case R.id.buttonR:
                startActivity(new Intent(Login.this, Register.class));
                break;
            default:
        }
    }

    private  void login() throws UnsupportedEncodingException, NoSuchAlgorithmException, ExecutionException, InterruptedException {
        String userL = user.getText().toString();
        String passL = pass.getText().toString();
        String rolTemp = "";
        String cargo = "";
        String token = "TRUE";
       /* try {
            //System.out.println(crypth.AES_Encrypt("USER1IMEITEST00000000000", "TRUE" ));
            //System.out.println(crypth.AES_Decrypt("USER1IMEITEST00000000000", "rNJP9bixc2OR3KDNlCnDEw=="));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/

        String IMEI = telephonyManager.getDeviceId();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        //rol = backgroundWorker.execute(userL, crypth.AES_Encrypt(IMEI, crypth.SHA1(crypth.MD5(passL))), "login").toString();
        cargo = backgroundWorker.execute("login",userL,passL).get().toString();

        System.out.println(cargo);

        //para bd local
       //rol =""+ db.getUser(userL,passL);
        /*
        Operador = 1
        Admin = 2
         */
        if(cargo.equals("1")){
            session.setLoggedin(true);
            Intent intent = new Intent(Login.this, AdminMapsActivity.class);
            intent.putExtra("User",userL );
            intent.putExtra("Imei",IMEI);
            startActivity(intent);
            finish();
        }
        else if(cargo.equals("2")){
            session.setLoggedin(true);
            Intent intent = new Intent(Login.this, OperatorMapsActivity.class);
            intent.putExtra("User",userL);
            intent.putExtra("Imei",IMEI);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong Combination", Toast.LENGTH_SHORT).show();
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
