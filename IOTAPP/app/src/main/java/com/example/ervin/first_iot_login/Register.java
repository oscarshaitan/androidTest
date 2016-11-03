package com.example.ervin.first_iot_login;

import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ervin.first_iot_login.Interfaces.registerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_register);

        final EditText Edad = (EditText) findViewById(R.id.aEdadr);
        final EditText Nombre = (EditText) findViewById(R.id.aNombrer);
        final EditText Usuario = (EditText) findViewById(R.id.aUsuarior);
        final EditText Password = (EditText) findViewById(R.id.aPasswd);
        final Button Registrarse =  (Button) findViewById(R.id.aRegistrar);

        Registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Edad.getText().toString().isEmpty() || Nombre.getText().toString().isEmpty() || Usuario.getText().toString().equals("") || Password.getText().toString().equals("") ) {
                    final TextView textViewx = (TextView) findViewById(R.id.aIncompleto);
                    textViewx.setText("Parametros Incompletos");
                }else{
                    Log.i("Ingresar", "boton presionado");
                    Intent registerIntent = new Intent(Register.this, registroOk.class);
                    Register.this.startActivity(registerIntent);
                    registerService registro_val = registerService.retrofit.create(registerService.class);
                    Call<ResponseBody> call = registro_val.repoContributors(Usuario.getText().toString(), Password.getText().toString(),Nombre.getText().toString(), Edad.getText().toString());

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try{
                            //textView.setText(response.body().string());
                            String json = response.body().string();
                            try {
                                JSONObject jObject = new JSONObject(json);
                                //JSONArray jsonArray =  jsonRootObject.optJSONArray("data");

                                // for(int i=0; i < jsonArray.length(); i++){
                               // JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Log.d("Datos_Ingreso", jObject.toString());

                                /*if(jObject.getString("status").equals("success") ){
                                    Intent accesIntent = new Intent(Register.this, Main_Screen.class);
                                    Register.this.startActivity(accesIntent);
                                }else{
                                    error.setText("Datos Ingresados Errados");
                                }*/
                                //}
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("respuesta", response.body().string());
                        }catch(NullPointerException e){
                            Log.d("respuesta", "error: " +e);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            String msg = "Registro errrado";
                            Log.d(TAG, msg);
                            Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            }
        });


    }
}
