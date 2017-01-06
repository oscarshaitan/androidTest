package com.shaitan.boxopen;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Shaitan on 18/11/2016.
 */
public class BackgroundWorker extends AsyncTask <String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    BackgroundWorker(Context context){
        this.context = context ;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String user = params[1];
        final String TOKEN = "TRUE";

        //login
        String login_url = "http://190.131.205.166/gssbox/index.php/Login_android/login_usuarios_android";// FUNCIONANDO (21/12/16)
        //MAIN URL
        String Boxs_url = "http://190.131.205.166/gssbox/index.php/main_android/get_usuario_cajas";
        String openBoxs_url = "http://190.131.205.166/gssbox/index.php/main_android/send_open_request";// FUNCIONANDO (21/12/16)
        String closeBoxs_url = "http://190.131.205.166/gssbox/index.php/main_android/send_close_request";
        String GetBoxInfo_url = "http://190.131.205.166/gssbox/index.php/main_android/get_caja";// FUNCIONANDO (21/12/16)
        String GetBoxInfoEmpresa_url = "http://190.131.205.166/gssbox/index.php/main_android/get_cajas_empresa";
        String get_puntos_url =  "http://190.131.205.166/gssbox/index.php/main_android/get_puntos";// FUNCIONANDO (21/12/16)
        String getLlaveDestinatarioBox_url = "http://190.131.205.166/gssbox/index.php/main_android/get_llave_destino";// FUNCIONANDO (21/12/16)
        String getLlaveTransportadorBox_url = "http://190.131.205.166/gssbox/index.php/main_android/get_llave_transporte";//POR IMPLEMENTAR
        String sendTransportadorPos_url = "http://190.131.205.166/gssbox/index.php/main_android/update_transportador_pos";
        String sendTerminarEntrega_url = "http://190.131.205.166/gssbox/index.php/main_android/terminar_entrega";


        //ADMIN URL
        String AdminBoxs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/get_usuario_cajas";// FUNCIONANDO (21/12/16)
        String adminOpenBoxs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/send_open_request";// FUNCIONANDO (21/12/16) falta verificar el tipo de apertura en php, // PARA CUANDO SE VUELVE A UNDIR RETORNA UN "ok"
        //String getOperatorrs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/get_usuarios_operador";
        try {
            if (type.equals("login")) {

                String pass = params[2];
                URL url = new URL(login_url);
                //final HttpsURLConnection connection = prepareConnection(url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data= URLEncoder.encode("user","UTF-8")+"="+ URLEncoder.encode(user,"UTF-8")
                        +"&"+URLEncoder.encode("pass","UTF-8")+"="+ URLEncoder.encode(pass,"UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result= "";
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("sendOpenRequest")) {
                String idBox = params[2];
                    URL url = new URL(openBoxs_url);
                    final HttpURLConnection connection = prepareConnection(url);
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                    String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                            + "&" + URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                            +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    connection.disconnect();
                    return result;
            }
            if (type.equals("sendCloseRequest")) {
                String idBox = params[2];
                URL url = new URL(closeBoxs_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                        + "&" + URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("sendAdminOpenRequest")) {
                String idBox = params[2];
                URL url = new URL(adminOpenBoxs_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("getBoxes")) {
                URL url = new URL(Boxs_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("getBoxesEmpresa")) {
                URL url = new URL(GetBoxInfoEmpresa_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("adminGetBoxes")) {

                URL url = new URL(AdminBoxs_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("GetBoxInfo")) {
                String idBox = params[2];
                URL url = new URL(GetBoxInfo_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8")
                        +"&"+URLEncoder.encode("IdBox","UTF-8")+"="+ URLEncoder.encode(idBox,"UTF-8")
                        +"&"+URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("get_puntos")) {
                String idBox = params[2];
                URL url = new URL(get_puntos_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8")
                        +"&"+URLEncoder.encode("IdBox","UTF-8")+"="+ URLEncoder.encode(idBox,"UTF-8")
                        +"&"+URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("llaveDestinatario")) {
                String idBox = params[2];
                URL url = new URL(getLlaveDestinatarioBox_url);
                //final HttpsURLConnection connection = prepareConnection(url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result= "";
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("llaveTransportador")) {
                String idBox = params[2];
                URL url = new URL(getLlaveTransportadorBox_url);
                //final HttpsURLConnection connection = prepareConnection(url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result= "";
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            }
            if (type.equals("sendTransporterLocation")) {
                String latitud = params[2];
                String longitud = params[3];
                URL url = new URL(sendTransportadorPos_url);
                //final HttpsURLConnection connection = prepareConnection(url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("latitud", "UTF-8") + "=" + URLEncoder.encode(latitud, "UTF-8")
                        +"&"+URLEncoder.encode("longitud","UTF-8")+"="+ URLEncoder.encode(longitud,"UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8")
                        +"&"+URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
            if (type.equals("sendTerminarEntrega")) {
               /* String idBox = params[2];
                URL url = new URL(sendTerminarEntrega_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                        + "&" + URLEncoder.encode("IdBox", "UTF-8") + "=" + URLEncoder.encode(idBox, "UTF-8")
                        +"&"+URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();*/
                String result = "POR IMPLEMENTAR";
                return result;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("testing");
            //Toast.makeText(getApplicationContext(), "Información incorrecta", Toast.LENGTH_SHORT).show();
           // e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        //alertDialog.setMessage(result);
        //alertDialog.show();
    }

    @Override
    protected void onPreExecute() {
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Loging Status");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private HttpURLConnection prepareConnection(final URL verifierURL)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
       /* TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };


        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


       /*SSLContext sslcontext = SSLContext.getInstance("TLSv1");

        try {
            sslcontext.init(null,
                    null,
                    null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
        HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory); */

        final HttpURLConnection connection = (HttpURLConnection) verifierURL
                .openConnection();
        //final HttpsURLConnection connection = (HttpsURLConnection) verifierURL
        // .openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        return connection;
    }
}
