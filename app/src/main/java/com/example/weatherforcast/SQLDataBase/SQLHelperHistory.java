package com.example.weatherforcast.SQLDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.weatherforcast.search_City.CityName;

import java.util.ArrayList;

public class SQLHelperHistory extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "dataHistorySearch.db";
    private static final int DATABSE_VERSION = 1;
    public static final String TABLE_NAME = "ListHistorySearch";
    public static final String ID_COLUMN = "id";
    public static final String CITYNAME_COLUMN = "cityName";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            " " + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " " + CITYNAME_COLUMN + " TEXT NOT NULL " +
            ");";

    public SQLHelperHistory(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSearchHistory(CityName cityName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CITYNAME_COLUMN, cityName.getName());
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
                CityName cityName = new CityName();
                int idIndex = cursor.getColumnIndex(ID_COLUMN);
                if (idIndex != -1) {
                    cityName.setId(cursor.getInt(idIndex));
                }
                int cityNameIndex = cursor.getColumnIndex(CITYNAME_COLUMN);
                if (cityNameIndex != -1) {
                    cityName.setName(cursor.getString(cityNameIndex));
                }
                list.add(cityName);
            }
            sqLiteDatabase.close();
            return list;
        }
        sqLiteDatabase.close();
        return null;
    }
}
