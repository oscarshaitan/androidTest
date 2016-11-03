package com.example.ervin.first_iot_login;

import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Main_Screen extends AppCompatActivity {
    private static final String TAG = "Main_Screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       MultiDex.install(this);

        setContentView(R.layout.activity_main__screen);
        final Button salir = (Button) findViewById(R.id.aSalir);

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Dispositivo deslogueado adecuadamente";
                Log.d(TAG, msg);
                Toast.makeText(Main_Screen.this, msg, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }

}
