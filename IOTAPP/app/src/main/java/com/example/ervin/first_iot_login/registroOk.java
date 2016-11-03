package com.example.ervin.first_iot_login;

import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class registroOk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_registro_ok);

        final Button Ok = (Button) findViewById(R.id.bIngresar);
        final ImageView image = (ImageView) findViewById(R.id.imageok);
        image.setImageResource(R.drawable.bien);


        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(registroOk.this, Login.class);
                registroOk.this.startActivity(registerIntent);
            }
        });


    }
}
