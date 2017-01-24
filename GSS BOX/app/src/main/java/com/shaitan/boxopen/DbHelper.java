package com.shaitan.boxopen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shaitan on 3/11/2016.
 */
public class DbHelper extends SQLiteOpenHelper{

    public static  final String TAG = DbHelper.class.getSimpleName();
    public static  final String DB_NAME = "OpenBox.db";
    public static  final int DB_VERSION = 1;

    public static  final String USER_TABLE = "user";
    public static  final String COLUMN_USER_ID = "_id";
    public static  final String COLUMN_USER = "user";
    public static  final String COLUMN_PASS = "pass";
    public static  final String COLUMN_ROL = "rol";

    public static  final String STOP_TABLE = "stops";
    public static  final String COLUMN_STOP_ID = "_id";
    public static  final String COLUMN_LONGITUD = "longitud";
    public static  final String COLUMN_LAT = "lat";
    private crypth crypthTool = new crypth();


    public static  final String CREATE_TABLE_USERS = "CREATE TABLE " +USER_TABLE+ "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER + " TEXT,"
            + COLUMN_PASS + " TEXT,"
            + COLUMN_ROL + " INT);";
    public static  final String CREATE_TABLE_STOPS = "CREATE TABLE " +STOP_TABLE+ "("
            + COLUMN_STOP_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_LONGITUD + " DOUBLE,"
            + COLUMN_LAT +   " DOUBLE);";


    public DbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_STOPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXIST " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + STOP_TABLE);
        onCreate(db);
    }

    public void addUser(String user, String pass, int rol)throws NoSuchAlgorithmException, UnsupportedEncodingException{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, user);
        values.put(COLUMN_PASS,crypthTool.SHA1(crypthTool.MD5(pass)));
        values.put(COLUMN_ROL, rol);

        long id = db.insert(USER_TABLE, null, values);
        db.close();

        Log.d(TAG, "user inserted "+id);
    }

    public long addStop(double lat, double longit ){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LONGITUD, longit);
        values.put(COLUMN_LAT, lat);

        long id = db.insert(STOP_TABLE, null, values);
        db.close();

        return id;

    }

    public int getUser(String user, String pass) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String selectQuery = "select * from " +USER_TABLE+ " where "+
                COLUMN_USER + " = " + "'"+user+"'"+ " and " + COLUMN_PASS + " = " +"'"+crypthTool.SHA1(crypthTool.MD5(pass))+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        if(cursor.getCount()>0){

           return cursor.getInt(3);
        }
        else
        return 0;
    }

    public List<Double[]> getAllStops() {
        String selectQuery = "select * from " +STOP_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Double[]> stopList = new ArrayList<>();

        while(cursor.moveToNext()){
            Double[] stopData= new Double[3];
            stopData[0]= Double.valueOf(cursor.getString(0));
            stopData[1]= Double.valueOf(cursor.getString(1));
            stopData[2]= Double.valueOf(cursor.getString(2));
            stopList.add(stopData);
        }
        return stopList;
    }

    public void CLEARSTOPS(){
        String Query = "delete from "+STOP_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(Query);
    }
}
