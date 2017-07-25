package com.yellowredorange.memerableplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView placeList;
    static ArrayList<String> places;
    static ArrayList<LatLng> locations;
    static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeList = (ListView) findViewById(R.id.placeList);

        places = new ArrayList<>();
        locations = new ArrayList<>();

        SharedPreferences userData = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
        try {
            String serializedPlaces = userData.getString("places", ObjectSerializer.serialize(new ArrayList<String>()));
            String serializedLocations = userData.getString("locations", ObjectSerializer.serialize(new ArrayList<String>()));

            places = (ArrayList<String>) ObjectSerializer.deserialize(serializedPlaces);
            locations = (ArrayList<LatLng>) ObjectSerializer.deserialize(serializedLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        placeList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences userData = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);

        try {
            userData.edit().putString("places", ObjectSerializer.serialize(places)).apply();
            userData.edit().putString("locations", ObjectSerializer.serialize(locations)).apply();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlace(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
}
