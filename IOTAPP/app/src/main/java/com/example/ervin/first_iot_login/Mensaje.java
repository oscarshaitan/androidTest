package com.example.ervin.first_iot_login;

/**
 * Created by Ipinnovatech on 22/09/16.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.multidex.MultiDex;

public class Mensaje extends Activity{

    Bundle bundle = null;
    String mensaje = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.mensaje);

        bundle = getIntent().getExtras();
        mensaje = bundle.getString("message");


        AlertDialog.Builder builder = new AlertDialog.Builder(Mensaje.this);
        builder.setMessage(mensaje)
                .setTitle("Mensaje recibido")
                .setIcon(R.drawable.bien)
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
        builder.create();
        builder.show();
    }

}