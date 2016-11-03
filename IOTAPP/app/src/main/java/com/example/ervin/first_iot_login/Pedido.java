package com.example.ervin.first_iot_login;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.ervin.first_iot_login.Interfaces.Productos;


public class Pedido extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = "Pedido";
    Spinner spseleccion;
    ArrayAdapter<String> aaseleccion;
    String [] opseleccion = new String[]{"...","Listar todos los Productos ","Listar Productos guardados"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_pedido);
        //final Button Ingresar =  (Button) findViewById(R.id.bp1);
        spseleccion = (Spinner) findViewById(R.id.spinner);

        aaseleccion = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, opseleccion);
        //spseleccion.setOnItemClickListener(this);
        spseleccion.setAdapter(aaseleccion);

        String msg = getString(R.string.fcm_registered);
        Toast.makeText(Pedido.this, msg, Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras!=null){
            String dato = extras.getString("Dato:");
            Toast.makeText(Pedido.this, dato, Toast.LENGTH_LONG).show();
        }

        //Productos ServiceProductos = Productos.retrofit.create(Productos.class);
        //Call<ResponseBody> call = ServiceProductos.repoContributors(Usuario.getText().toString(), Password.getText().toString());

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Ingreso", "Clase 1");
        Toast.makeText(Pedido.this, "Clase 1", Toast.LENGTH_SHORT).show();
        switch(position){
            case R.id.spinner:
                int seleccion = spseleccion.getSelectedItemPosition();
                if (seleccion == 1){
                    String msg = "Traer lista de todos los productos";
                    Toast.makeText(Pedido.this, msg, Toast.LENGTH_SHORT).show();
                }else if(seleccion == 2){
                    String msg = "Traer lista de productos guardados";
                    Toast.makeText(Pedido.this, msg, Toast.LENGTH_SHORT).show();
                }


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    Log.d("Ingreso", "Claase 2");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Ingreso", "Claase 3");
    }
}


