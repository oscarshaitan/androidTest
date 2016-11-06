package com.shaitan.boxopen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login, register;
    private EditText pass,user,rol;
    private DbHelper db;
    private crypth crypth;
    private Session session;


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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        if(session.loggedin()){
            session.setLoggedin(true);
            startActivity(new Intent(Login.this,AdminMapsActivity.class ));
            finish();
        }
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
                }
                break;
            case R.id.buttonR:
                startActivity(new Intent(Login.this, Register.class));
                break;
            default:
        }
    }

    private  void login() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String userL = user.getText().toString();
        String passL = pass.getText().toString();
        String rol = ""+db.getUser(userL,passL);
        if(rol.equals("1")){
            session.setLoggedin(true);
            startActivity(new Intent(Login.this, AdminMapsActivity.class));
            finish();
        }
        else if(rol.equals("2")){
            session.setLoggedin(true);
            startActivity(new Intent(Login.this, OperatorMapsActivity.class));
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong Combination", Toast.LENGTH_SHORT).show();
        }
    }

}
