package com.techobbyist.signuplogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Admin extends AppCompatActivity {
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(Admin.this,LoginActivity.class));
    }
}
