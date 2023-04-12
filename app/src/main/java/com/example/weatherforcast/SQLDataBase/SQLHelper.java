package com.example.weatherforcast.SQLDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.weatherforcast.search_City.CityName;
import com.example.weatherforcast.search_City.Coord;

import java.util.ArrayList;

public class SQLHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dataweatherforcast.db";
    private static final int DATABSE_VERSION = 1;
    public static final String TABLE_NAME = "ListCity";
    public static final String ID_COLUMN = "id";
    public static final String CITYNAME_COLUMN = "cityname";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGTITUDE_COLUMN = "longtitude";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            " " + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " " + CITYNAME_COLUMN + " TEXT NOT NULL," +
            " " + LATITUDE_COLUMN + " TEXT NOT NULL," +
            " " + LONGTITUDE_COLUMN + " TEXT NOT NULL" +
            ");";

    public SQLHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addCity(CityName cityName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CITYNAME_COLUMN, cityName.getName());
        contentValues.put(LATITUDE_COLUMN, cityName.getCoord().getLat());
        contentValues.put(LONGTITUDE_COLUMN, cityName.getCoord().getLon());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }

    public ArrayList<CityName> getListCity() {
        ArrayList<CityName> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CityName city = new CityName();
                int idIndex = cursor.getColumnIndex(ID_COLUMN);
                if (idIndex != -1) {
                    city.setId(cursor.getInt(idIndex));
                }
                int cityNameIndex = cursor.getColumnIndex(CITYNAME_COLUMN);
                if (cityNameIndex != -1) {
                    city.setName(cursor.getString(cityNameIndex));
                }
                Coord coord = new Coord();
                int latitudeIndex = cursor.getColumnIndex(LATITUDE_COLUMN);
                if (latitudeIndex != -1) {
                    coord.setLat(Float.valueOf(cursor.getString(latitudeIndex)));
                }
                int longtitudeIndex = cursor.getColumnIndex(LONGTITUDE_COLUMN);
                if (longtitudeIndex != -1) {
                    coord.setLon(Float.valueOf(cursor.getString(longtitudeIndex)));
                }
                city.setCoord(coord);
                list.add(city);
            }
            sqLiteDatabase.close();
            return list;
        }
        sqLiteDatabase.close();
        return null;
    }
    public void deleteCity(CityName cityName){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String whereClause = ID_COLUMN + "=?";
        String whereArray[] = {cityName.getId()+""};
        sqLiteDatabase.delete(TABLE_NAME,whereClause, whereArray);
        sqLiteDatabase.close();
    }
}
