package com.ex.saulantonio.enruta3;

/**
 * Created by SaulAntonio on 5/4/2015.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by SaulAntonio on 5/4/2015.
 */
public class ControladorSQL {
    SQLHelper sqlHelper;
    public ControladorSQL(Context context){
        sqlHelper = new SQLHelper(context);
    }

    public long insertarValores(String latLng, String nombre, String descripcion) {
        SQLiteDatabase  database = sqlHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        if(nombre==null) {
            nombre = "Marcador";
            descripcion = "personalizado";
        }
        valores.put(sqlHelper.NOMBRE,nombre);
        valores.put(sqlHelper.DESCRIPCION,descripcion);
        valores.put(sqlHelper.latlng,latLng);
        return database.insert(sqlHelper.TABLE_NAME,null,valores);
    }

    public ArrayList<ModeloFavoritos> obtenerDatos(){
        SQLiteDatabase database =null;
        ArrayList<ModeloFavoritos> lista = new ArrayList<>();
        Cursor cursor=null;
        try {
            database = sqlHelper.getWritableDatabase();
            String[] columnas ={sqlHelper.UID,sqlHelper.NOMBRE,sqlHelper.DESCRIPCION,sqlHelper.latlng};
            cursor = database.query(sqlHelper.TABLE_NAME, columnas, null, null, null, null, null);
        }catch (Exception e){Log.d("AQI ESTA EL ERROR",e.toString());};

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()){
            int indexID = cursor.getColumnIndex(sqlHelper.UID);
            int indexNombre = cursor.getColumnIndex(sqlHelper.NOMBRE);
            int indexDescription = cursor.getColumnIndex(sqlHelper.DESCRIPCION);
            int indexCoordenadas = cursor.getColumnIndex(sqlHelper.latlng);
            Log.d("indice",indexDescription+"");
            String nombre= cursor.getString(indexNombre);
            String descripcion = cursor.getString(indexDescription);
            String coordenadas = cursor.getString(indexCoordenadas);
            String[] aux = coordenadas.split(",");
            LatLng ll = new LatLng(Double.parseDouble(aux[0]),Double.parseDouble(aux[1]));
            int id = cursor.getInt(indexID);
            ModeloFavoritos elemento = new ModeloFavoritos(id,nombre,R.drawable.ic_favoritos,descripcion,ll);
            lista.add(elemento);
        }

        database.close();
        return lista;
    }

    public void eliminarValor(int id) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.execSQL("delete from " + SQLHelper.TABLE_NAME + " where "+ SQLHelper.UID + "='" + id + "'");
    }

    static class SQLHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME ="bdatos";
        private static final String TABLE_NAME = "FAVORITOS";
        private static final String UID ="_id";
        private static final String NOMBRE = "Nombre";
        private static final String latlng = "Coordenadas";
        private static final String DESCRIPCION = "Descripcion";
        private static final int DATABASE_VERSION=6;
        private static final String CREATE_TABLE  ="CREATE TABLE "+TABLE_NAME+"("+UID+ " INTEGER PRIMARY KEY AUTOINCREMENT," + NOMBRE + " VARCHAR(255),"+DESCRIPCION+" VARCHAR(255),"+latlng+" VARCHAR(255));";
        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {db.execSQL(CREATE_TABLE);}catch (SQLException sql){Log.w("SQL EXCEPTION",sql);}
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);}catch (SQLException sql){Log.w("SQL EXCEPTION",sql);}
            onCreate(db);
        }

    }
}

