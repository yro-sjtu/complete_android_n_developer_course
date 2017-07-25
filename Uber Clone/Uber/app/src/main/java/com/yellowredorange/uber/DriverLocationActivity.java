package com.yellowredorange.uber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class DriverLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        intent = getIntent();
        double[] driverLoc = intent.getDoubleArrayExtra("driver");
        double[] riderLoc = intent.getDoubleArrayExtra("rider");

        mMap = googleMap;
        mMap.clear();

        LatLng driverLatLng = new LatLng(driverLoc[0], driverLoc[1]);
        mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        LatLng riderLatLng = new LatLng(riderLoc[0], riderLoc[1]);
        mMap.addMarker(new MarkerOptions().position(riderLatLng).title("Request location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(driverLatLng);
        builder.include(riderLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 15% of screen

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    public void acceptRequest(View view) {
        String ridername = intent.getStringExtra("ridername");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ridername);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0) {
                    for (final ParseObject object : objects) {
                        object.put("drivername", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseGeoPoint gp = object.getParseGeoPoint("location");
                                    double riderLat = gp.getLatitude();
                                    double riderLng = gp.getLongitude();

                                    // reference: https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android

                                    Intent naviIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse("http://maps.google.com/maps?daddr=" + riderLat + "," + riderLng));
                                    naviIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(naviIntent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error occured when accepting request. Please try again later.", Toast.LENGTH_SHORT).show();
                                    Log.i("Error in Saving", e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error occured when accepting request. Please try again later.", Toast.LENGTH_SHORT).show();
                    Log.i("Error in Finding", e.getMessage());
                }
            }
        });
    }
}
