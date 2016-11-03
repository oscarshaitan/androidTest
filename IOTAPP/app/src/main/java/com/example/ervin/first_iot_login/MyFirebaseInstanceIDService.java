package com.example.ervin.first_iot_login;

/**
 * Created by Ipinnovatech on 26/09/16.
 */

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

//        sendRegistrationToServer(refreshedToken);
    }

    protected void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

    }
}
