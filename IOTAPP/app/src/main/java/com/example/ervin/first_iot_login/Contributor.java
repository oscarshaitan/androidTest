package com.example.ervin.first_iot_login;

/**
 * Created by Ipinnovatech on 19/09/16.
 */
public class Contributor {

    String login;
    String html_url;
    String contributions;

    @Override
    public String toString() {
        return login + " (" + contributions + ")";
    }
}
