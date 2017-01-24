package com.shaitan.programmingwizards;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.guardianproject.netcipher.NetCipher;


public class Connector {

    public static HttpURLConnection connect(String urlAddress) {
        try
        {
            URL url=new URL(urlAddress);
            HttpURLConnection con= NetCipher.getHttpsURLConnection(url);

            con.setRequestMethod("POST");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setDoInput(true);
            con.setDoOutput(true);

            return con;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}