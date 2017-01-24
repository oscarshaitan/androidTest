package com.shaitan.boxopen;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shaitan on 3/11/2016.
 */
public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("OpenBox", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean loggedin){
        editor.putBoolean("loggedInmode", loggedin);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }


}
