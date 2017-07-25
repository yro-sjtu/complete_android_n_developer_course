package com.yellowredorange.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.yellowredorange.sharedpreferences", Context.MODE_PRIVATE);

        ArrayList<String> friends = new ArrayList<String>();
        friends.add("Monica");
        friends.add("Rachel");

        try {
//            sharedPreferences.edit().putString("friends", ObjectSerializer.serialize(friends)).apply();

            ArrayList<String> newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("friends", newFriends.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


//        sharedPreferences.edit().putString("username", "frank").apply();
//        String username = sharedPreferences.getString("username", "");
//        Log.i("username", username);
    }
}
