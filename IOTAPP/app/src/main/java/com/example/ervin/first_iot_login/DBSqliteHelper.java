package com.example.ervin.first_iot_login;

/**
 * Created by Ipinnovatech on 30/09/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBSqliteHelper extends SQLiteOpenHelper {

    private String sqlCreate;//sqlCreate1,sqlCreate2,sqlCreate3,sqlCreate4,sqlCreate5;
    Context contexto;
    public DBSqliteHelper(Context contexto, String nombre, CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //sqlCreate = "CREATE TABLE Productos (Pr_ID TEXT PRIMARY KEY AUTOINCREMENT, Pr_Nombre TEXT, Pr_foto TEXT, Pr_Descripcion TEXT, Pr_Cantidad TEXT DEFAULT '0', Pr_valor TEXT DEFAULT '0', Pr_ListaSave TEXT DEFAULT '0')";
        //db.execSQL(sqlCreate);
        db.execSQL("CREATE TABLE Productos (Pr_ID INTEGER PRIMARY KEY AUTOINCREMENT, Pr_Nombre TEXT, Pr_foto TEXT, Pr_Descripcion TEXT, Pr_Cantidad TEXT DEFAULT '0', Pr_valor TEXT DEFAULT '0', Pr_ListaSave TEXT DEFAULT '0')");
        db.execSQL("CREATE TABLE Usuarios (Us_ID INTEGER PRIMARY KEY AUTOINCREMENT, Us_Nombre TEXT,Us_Edad INTEGER, Us_Date DATETIME DEFAULT CURRENT_TIMESTAMP)");

        /*sqlCreate1 = "CREATE TABLE datosGPS (lat TEXT, lon TEXT, vel TEXT, fechasys TEXT)";
        db.execSQL(sqlCreate1);*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        db.execSQL("DROP TABLE IF EXISTS Productos");
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(db);

        /*db.execSQL("DROP TABLE IF EXISTS datosGPS");
        db.execSQL(sqlCreate1);*/
    }

    DBSqliteHelper DB;
    SQLiteDatabase dbcons;

    public void abrir(){
        dbcons = DB.getWritableDatabase();
    }

    public void cerrar(){
        dbcons.close();
    }

    public long registrar (String pDato) throws Exception{
        ContentValues Info = new ContentValues();
        Info.put("Pr_Nombre",pDato);
        return dbcons.insert("Productos", null, Info);
    }

}