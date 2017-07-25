package com.yellowredorange.databasedemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            SQLiteDatabase db = openOrCreateDatabase("Events", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS events (name VARCHAR, year INT(4))");
//            db.execSQL("INSERT INTO events (name, year) VALUES ('Independence Day of United States', 1776)");
//            db.execSQL("INSERT INTO events (name, year) VALUES ('Birth of YRO', 1989)");
//
//            Cursor c = db.rawQuery("SELECT * FROM events", null);
//            int nameIdx = c.getColumnIndex("name");
//            int yearIdx = c.getColumnIndex("year");
//
//            c.moveToFirst();
//            while(c != null) {
//                Log.i("name", c.getString(nameIdx));
//                Log.i("year", Integer.toString(c.getInt(yearIdx)));
//
//                c.moveToNext();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {

            SQLiteDatabase db = openOrCreateDatabase("Users", MODE_PRIVATE, null);
//            db.execSQL("CREATE TABLE IF NOT EXISTS users (name VARCHAR, age INT(3))");
//            db.execSQL("INSERT INTO users (name, age) VALUES ('Chocolate', 28)");
//            db.execSQL("INSERT INTO users (name, age) VALUES ('Emma', 25)");
//            db.execSQL("DELETE FROM users WHERE rowid NOT IN (SELECT MIN(rowid) FROM users GROUP BY name)");
            db.execSQL("UPDATE users SET age = 26 WHERE name = 'Emma'");
            Cursor c = db.rawQuery("SELECT * FROM users", null);
            int nameIdx = c.getColumnIndex("name");
            int ageIdx = c.getColumnIndex("age");
            c.moveToFirst();
            while(!c.isAfterLast()) {
                Log.i("name", c.getString(nameIdx));
                Log.i("age", String.valueOf(c.getInt(ageIdx)));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
