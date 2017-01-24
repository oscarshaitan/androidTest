package com.shaitan.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
public class MainActivity extends Activity {

    TextView content;
    EditText fname, user, login, pass;
    String Name, User, Login, Pass;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        user      =    (EditText)findViewById(R.id.user);
        pass       =   (EditText)findViewById(R.id.password);


        Button saveme=(Button)findViewById(R.id.save);

        saveme.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v)
            {
                try{

                    // CALL GetText method to make post method call
                    GetText();
                }
                catch(Exception ex)
                {
                    content.setText(" url exeption! " );
                }
            }
        });
    }

    // Create GetText Metod
    public  void  GetText()  throws UnsupportedEncodingException
    {
        // Get user defined values

        User   = user.getText().toString();

        Pass   = pass.getText().toString();
        String data="";

    }



}