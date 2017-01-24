package com.shaitan.posttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Login";

    TextView content;
    EditText  user, pass;
    String User, Pass;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content    =   (TextView)findViewById( R.id.content );
        user      =   (EditText)findViewById(R.id.user);
        pass       =   (EditText)findViewById(R.id.password);
    }

    // Create GetText Metod
    public  void  Login(View view) throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        User   = user.getText().toString();
        Pass   = pass.getText().toString();
        String  type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        String rol = "";
        String login_url = "https://190.131.205.166/gss/login.php";

        rol = backgroundWorker.execute(User, Pass, type).get().toString();
        System.out.println("ROL");
        System.out.println(rol);
    }

}
