package com.yellowredorange.memerableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults != null && grantResults.length > 0) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocation(lastKnownLocation, "Your location");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        if(MainActivity.locations.size() == 0) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location, "Your location");
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

            if (Build.VERSION.SDK_INT < 23) { // version before Marshmallow
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        updateLocation(lastKnownLocation, "Your location");
                    }
                }
            }
        } else {
            int size = MainActivity.places.size();
            String address = MainActivity.places.get(size - 1);
            LatLng latLng = MainActivity.locations.get(size - 1);
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);

            updateLocation(location, address);
        }
    }


    private void updateLocation(Location location, String title) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng user = new LatLng(lat, lng);

        if(!title.equals("Your location")) {
            mMap.addMarker(new MarkerOptions().position(user).title(title));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 12));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";

        try {
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(listAddress != null && listAddress.size() > 0) {
                if(listAddress.get(0).getSubThoroughfare() != null) {
                    address += listAddress.get(0).getSubThoroughfare() + ", ";
                }
                if(listAddress.get(0).getThoroughfare() != null) {
                    address += listAddress.get(0).getThoroughfare();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        if(!address.equals("")) {
            MainActivity.places.add(address);
            MainActivity.locations.add(latLng);
            MainActivity.adapter.notifyDataSetChanged();

            Toast.makeText(this, "Location saved", Toast.LENGTH_LONG).show();
        }
    }
}
