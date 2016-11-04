package com.shaitan.boxopen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private Button add, back;
    private TextView tvLogin;
    private EditText pass, user, rol;
    private  DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DbHelper(this);

        add = (Button)findViewById(R.id.buttonA);
        back = (Button)findViewById(R.id.buttonB);
        user = (EditText)findViewById(R.id.nameText);
        pass = (EditText)findViewById(R.id.passRText);
        rol = (EditText)findViewById(R.id.rolText);
        back.setOnClickListener(this);
        add.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonB:
                startActivity(new Intent(Register.this, Login.class));
                break;
            case R.id.buttonA:
                try {
                    register();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    private void register() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String userR = user.getText().toString();
        String passR = pass.getText().toString();
        String rolR = rol.getText().toString();
        if(userR.isEmpty()||passR.isEmpty()||rolR.isEmpty()){
            displayToast ("User/pass/rol are important");
        }
        else{
            db.addUser(userR, passR, Integer.valueOf(rolR));
            displayToast ("User registered");
            finish();
        }
    }
    private void displayToast(String txt){
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
    }

}
