package com.example.ervin.first_iot_login;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ervin.first_iot_login.Interfaces.conexionService;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity{
    private static final String TAG = "Login";

    public static String id_movil;
    private DBSqliteHelper DB;
    private SQLiteDatabase dbcons;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MultiDex.install(this);

        final EditText Usuario = (EditText) findViewById(R.id.aUser);
        final EditText Password = (EditText) findViewById(R.id.aPass);
        final TextView Registrarse =  (TextView) findViewById(R.id.aRegistrarse);
        final Button Ingresar =  (Button) findViewById(R.id.aIngresar);
        DB = new DBSqliteHelper(this, "DBIOT", null, 1);
        //DB.getWritableDatabase();

        Registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Login.this, Register.class);
                Login.this.startActivity(registerIntent);
            }
        });

        Ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Ingresar", "boton login presionado");

                String Token = FirebaseInstanceId.getInstance().getToken();
                conexionService ResService = conexionService.retrofit.create(conexionService.class);
                Log.d("Datos enviados1",Usuario.getText().toString());
                Log.d("Datos enviados1",Password.getText().toString());
                Log.d("Datos enviados1",Token);
                Call<ResponseBody> call = ResService.repoContributors(Usuario.getText().toString(), Password.getText().toString(), Token);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //final TextView textView = (TextView) findViewById(R.id.atexto_return);//#

                       /* try{
                            textView.setText(response.body().string());//#
                            Log.d("respuesta", response.body().string());
                        }catch(NullPointerException e){
                            Log.d("respuesta", "error: " +e);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        try{
                           // textView.setText(response.body().string());
                            String json = response.body().string();
                            try {
                                Log.d("Datos",json);
                                JSONObject Object = new JSONObject(json);

                                Log.d("Datos1", Object.toString());
                               // String obj = "data";

                                //JSONArray jarray =  Object.getJSONArray("data");
                                //System.out.println(jarray.toString());

                               // for(int i=0; i < jarray.length(); i++) {
                               //      JSONObject jsonObject = jarray.getJSONObject(0);
                               //     Log.d("Datos2", jsonObject.getString("status"));
                               // }
                                String NameR = Object.getString("Nombre");
                                String EdadR = Object.getString("Edad");
                                if(Object.getString("status").equals("success") ){
                                    Log.d("Datos", "success");
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    dbcons = DB.getWritableDatabase();
                                    try {
                                        String query = "Insert into Usuarios (Us_Nombre,Us_Edad) values ('"+NameR+"',"+EdadR+")";
                                        dbcons.execSQL(query);
                                        Log.d("Consulta", query);

                                        Toast t = Toast.makeText(Login.this, "DB Actualizada adecuadamente"+NameR+" "+EdadR,Toast.LENGTH_SHORT );
                                        t.show();

                                    }catch (Exception e ){
                                        Toast t = Toast.makeText(Login.this, "Error al actualizar DB ",Toast.LENGTH_SHORT );
                                        t.show();
                                    }
                                    dbcons.close();

                                    if(token !=""){// Log and toast
                                            Log.d("No. Registro",token);
                                            //save server Token
                                            String msg = getString(R.string.msg_token_fmt);
                                            Log.d(TAG, msg);
                                            Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();


                                            Intent accesIntent = new Intent(Login.this, Main_Screen.class);
                                            Login.this.startActivity(accesIntent);
                                        }else{
                                                String msg = getString(R.string.error_token_fmt);
                                                Log.d(TAG, msg);
                                                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                }else{
                                   // error.setText("");
                                    String msg = getString(R.string.error_login);
                                    Log.d(TAG, msg);
                                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                                }

                                //}
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }


                        }catch(NullPointerException e){
                            Log.d("respuesta", "error: " +e);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        String msg = "Something went wrong: " + t.getMessage();
                        Log.d(TAG, msg);
                        Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }

}
