package com.shaitan.boxopen;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Help extends AppCompatActivity {
    private  String prevActivity, User, IMEI, IdBox;
    private List<String> menuOptions = new ArrayList<>();
    private ListView menuList;
    private Session session;
    private ImageButton btnMenu;
    private boolean menuFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        menuOptions.add("Back");
        menuOptions.add("Logout");
        Bundle bundle = getIntent().getExtras();
        prevActivity = bundle.getString("PrevActivity");
        User = bundle.getString("User");
        IMEI = bundle.getString("Imei");
        IdBox = bundle.getString("IdBox");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }

        btnMenu = (ImageButton) findViewById(R.id.menuBtn);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateMenu();
            }
        });

        menuList =(ListView) findViewById(R.id.MenuList);
        final ArrayAdapter<String> adapterM = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menuOptions);
        menuList.setAdapter(adapterM);
        adapterM.notifyDataSetChanged();
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                menuAction(myItemInt);
            }
        });
        inActivateMenu();
        //inActivateMenu();

    }

    public void menuAction(int optionSelected){

        switch (optionSelected){
            //goBack()
            case 0:
                goBack();
                break;
            //clear

            case 1:
                logout();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        goBack();
    }

    public void inActivateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_idle","mipmap", getPackageName())));
        }
        menuList.setActivated(false);
        menuList.setVisibility(View.INVISIBLE);
    }

    public void activateMenu(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnMenu.setImageDrawable(getDrawable(getResources().getIdentifier("menu_act","mipmap", getPackageName())));
        }
        menuList.setActivated(true);
        menuList.setVisibility(View.VISIBLE);
    }

    public void goBack(){
        if(prevActivity.equals("AdminMapsActivity")) {
            Intent intent = new Intent(Help.this, AdminMapsActivity.class);
            intent.putExtra("User",User);
            intent.putExtra("Imei",IMEI);
            intent.putExtra("IdBox",""+IdBox);
            startActivity(intent);
            finish();
        }
        if(prevActivity.equals("TransporterMapsActivity")) {
            Intent intent = new Intent(Help.this, TransporterMapsActivity.class);
            intent.putExtra("User",User);
            intent.putExtra("Imei",IMEI);
            intent.putExtra("IdBox",""+IdBox);
            startActivity(intent);
            finish();
        }
        if(prevActivity.equals("DestinatarioMapsActivity")) {
            Intent intent = new Intent(Help.this, DestinatarioMapsActivity.class);
            intent.putExtra("User",User);
            intent.putExtra("Imei",IMEI);
            intent.putExtra("IdBox",""+IdBox);
            startActivity(intent);
            finish();
        }
    }

    private void logout(){
        inActivateMenu();
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(Help.this,Login.class));
    }
}
