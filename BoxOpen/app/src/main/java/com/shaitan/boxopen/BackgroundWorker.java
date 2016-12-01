package com.shaitan.boxopen;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

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
        final String TOKEN = "TRUE";


        String login_url = "http://190.131.205.166/gssbox/index.php/Login_android/login_usuarios_android";
        String Boxs_url = "http://190.131.205.166/gssbox/index.php/main_android/get_usuario_cajas";
        String AdminBoxs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/get_usuario_cajas";
        String getOperatorrs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/get_usuarios_operador";
        String openBoxs_url = "http://190.131.205.166/gssbox/index.php/main_android/send_open_request";
        String adminOpenBoxs_url = "http://190.131.205.166/gssbox/index.php/Admin_android/send_open_request";
        try {
            if (type.equals("login")) {
                String user = params[1];
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
                String user = params[1];
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
            if (type.equals("sendAdminOpenRequest")) {
                String idBox = params[1];
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
                String user = params[1];

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
            if (type.equals("getOperators")) {

                URL url = new URL(getOperatorrs_url);
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
            if (type.equals("addBoxOperator")) {
                //type,operador destino, operador transporte, latitud, longitud
                String operatorDestinoId = params[1];
                String operatorTransporteId = params[2];
                String latitud = params[3];
                String longitud = params[4];

                URL url = new URL(getOperatorrs_url);
                final HttpURLConnection connection = prepareConnection(url);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("token","UTF-8")+"="+ URLEncoder.encode(TOKEN,"UTF-8")
                        +"&"+URLEncoder.encode("operatorDestinoId","UTF-8")+"="+ URLEncoder.encode(operatorDestinoId,"UTF-8")
                        +"&"+URLEncoder.encode("operatorTransporteId","UTF-8")+"="+ URLEncoder.encode(operatorTransporteId,"UTF-8")
                        +"&"+URLEncoder.encode("latitud","UTF-8")+"="+ URLEncoder.encode(latitud,"UTF-8")
                        +"&"+URLEncoder.encode("longitud","UTF-8")+"="+ URLEncoder.encode(longitud,"UTF-8");

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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
