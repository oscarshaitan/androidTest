package com.example.ervin.first_iot_login;

/**
 * Created by Ipinnovatech on 27/09/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.ervin.first_iot_login.Interfaces.Productos;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private DBSqliteHelper DB;
    private SQLiteDatabase dbcons;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Mensajes de Datos - FCM
        String notification;
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        DB = new DBSqliteHelper(this, "DBIOT", null, 1);

        // Check if message contains info.
        if (remoteMessage.getData().size() > 0) {
            notification = remoteMessage.getData().get("notification");
            showNotification(notification);
            Log.d(TAG, "Message data payload: " + notification);
            /*try {
                int badgeCount = 4;

                ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
                System.out.println("setbadge");

            } catch (ShortcutBadgeException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }*/
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param notification FCM message body received.
     */
    private void showNotification(String notification) {
        Productos ResService = Productos.retrofit.create(Productos.class);
        Call<ResponseBody> call = ResService.repoContributors("0","1");

        //########################################################################################
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    // textView.setText(response.body().string());
                    String json = response.body().string();
                    try {
                        Log.d("Datos",json);
                        JSONObject Object = new JSONObject(json);
                        Log.d("Datos1", Object.toString());
                        int i=0;
                        //String status = Object.getString("status");
                        JSONArray dataArrayin = Object.getJSONArray("notificacion");
                        int longitud = dataArrayin.length();
                        Log.d("Data Longitud: ", String.valueOf(longitud));
                        if(Object.getString("status").equals("success") ){
                            Log.d("Datos", "success");

                            dbcons = DB.getWritableDatabase();
                            while (i<longitud){
                                JSONObject Pdatos = dataArrayin.getJSONObject(i);
                                String Nombrep = Pdatos.getString("Pr_Nombre");
                                String Fotop = Pdatos.getString("Pr_foto");
                                String Descrip = Pdatos.getString("Pr_Descripcion");
                                String Valorp = Pdatos.getString("Pr_valor");

                                Log.d("Pr_Nombre",Nombrep);
                                Log.d("Pr_foto", Fotop);
                                Log.d("Pr_Descripcion",Descrip);
                                Log.d("Pr_valor", Valorp);


                                try {
                                    String query = "Insert into Productos (Pr_Nombre,Pr_foto,Pr_Descripcion,Pr_valor) values ('"+Nombrep+"','"+Fotop+"','"+Descrip+"','"+Valorp+"')";
                                    Log.d("Consulta", query);
                                    dbcons.execSQL(query);

                                }catch (Exception e ){
                                    Toast t = Toast.makeText(MyFirebaseMessagingService.this, "Error al actualizar DB ",Toast.LENGTH_SHORT );
                                    t.show();
                                }
                                i++;
                                dbcons.close();
                            }
                            Toast t = Toast.makeText(MyFirebaseMessagingService.this, "DB Actualizada adecuadamente",Toast.LENGTH_SHORT );
                            t.show();

                        }else{
                            String msg = "Error en la recepción de datos";
                            Log.d(TAG, msg);
                            Toast.makeText(MyFirebaseMessagingService.this, msg, Toast.LENGTH_SHORT).show();
                        }
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
                Toast.makeText(MyFirebaseMessagingService.this, msg, Toast.LENGTH_SHORT).show();
            }

        });
        //#######################################################################

        String dato ="cadena enviada desde MyFirebaseonmMessage";
        Intent intent = new Intent(this, ListaP.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Dato:",dato);
        //startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);//FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Message")
                .setContentText(notification)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
