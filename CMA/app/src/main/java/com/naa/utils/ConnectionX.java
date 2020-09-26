package com.naa.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.naa.data.Dson;

import java.util.Vector;

public class ConnectionX {
    private static ConnectionX connectionX;
    public static synchronized ConnectionX getInstance() {
        if (connectionX == null){
            connectionX = new ConnectionX();
        }
        return connectionX;
    }

    private SQLiteDatabase mConn ;
    public void openConnection(String file){
        try {
            mConn=SQLiteDatabase.openDatabase(file, null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeConnection(){
        try {
            mConn.close();
        } catch (Exception e) {}
    }
    public boolean isClosed(){
        try {
            return !mConn.isOpen();
        } catch (Exception e) {}
        return true;
    }
    public Dson query(String sql, String...args){
        Dson result = Dson.newArray();
        try {
            Cursor curr = mConn.rawQuery(sql, args!=null?args:new String[0]);
            while (curr.moveToNext()) {
                Dson field = Dson.newObject();
                for (int i = 0; i < curr.getColumnCount(); i++){
                    field.set(curr.getColumnName(i), curr.getString(i));
                }
                result.add( field );
            }
        } catch (Exception e) {}
        return result;
    }
    public void execute(String sql, String...args){
        try {
            if (args!=null){
                mConn.execSQL(sql, args);
            }else{
                mConn.execSQL(sql);
            }
        } catch (Exception e) {}
    }

    private void onCreate() {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + "TABLE_CONTACTS" + "("
                + "KEY_ID" + " INTEGER PRIMARY KEY," + "KEY_NAME" + " TEXT,"
                + "KEY_PH_NO" + " TEXT" + ")";
        mConn.execSQL(CREATE_CONTACTS_TABLE);
    }
}
