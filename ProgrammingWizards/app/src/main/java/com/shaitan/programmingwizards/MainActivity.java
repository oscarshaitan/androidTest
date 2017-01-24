package com.shaitan.programmingwizards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    String urlAddress="https://192.168.0.15:8080/gss/poster.php/";
    EditText nameTxt,posTxt,teamTxt;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nameTxt= (EditText) findViewById(R.id.nameEditTxt);
        posTxt= (EditText) findViewById(R.id.posEditTxt);
        teamTxt= (EditText) findViewById(R.id.teamEditTxt);
        saveBtn= (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sender s=new Sender(MainActivity.this,urlAddress,nameTxt,posTxt,teamTxt);
                s.execute();
            }
        });
    }
}
