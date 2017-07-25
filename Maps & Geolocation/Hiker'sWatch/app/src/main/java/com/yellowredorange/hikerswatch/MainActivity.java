package com.yellowredorange.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView latTextView;
    TextView lngTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView addressTextView;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults != null && grantResults.length > 0) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = (TextView) findViewById(R.id.latTextView);
        lngTextView = (TextView) findViewById(R.id.lngTextView);
        accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                showLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        } else {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
                Location current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(current != null) {
                    showLocation(current);
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void showLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double alt = location.getAltitude();
        float accuracy = location.getAccuracy();

        latTextView.setText("Latitude: " + String.format("%.2f", lat));
        lngTextView.setText("Longitude: "+ String.format("%.2f", lng));
        altitudeTextView.setText("Altitude: " + String.format("%.2f", alt));
        accuracyTextView.setText("Accuracy: " + String.format("%.2f", accuracy));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if(addresses != null && addresses.size() > 0) {
                String addr = "Address:\n";
                if(addresses.get(0).getSubThoroughfare() != null) {
                    addr += addresses.get(0).getSubThoroughfare() + "\n";
                }

                if(addresses.get(0).getThoroughfare() != null) {
                    addr += addresses.get(0).getThoroughfare()+ "\n";
                }

                if(addresses.get(0).getPostalCode() != null) {
                    addr += addresses.get(0).getPostalCode()+ "\n";
                }

                if(addresses.get(0).getCountryName() != null) {
                    addr += addresses.get(0).getCountryName();
                }

                addressTextView.setText(addr);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error Info", "Geocoder Not Work!");
        }
    }
}
