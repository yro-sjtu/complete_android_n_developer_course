package com.yellowredorange.uber;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RiderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
    private boolean onRequest = false;

    private Handler handler = new Handler();

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Button callUberButton;
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTitle("Rider");
        callUberButton = (Button) findViewById(R.id.callUberButton);
        infoTextView = (TextView) findViewById(R.id.infoTextView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> requests, ParseException e) {
                if(e == null && requests.size() > 0) {
                    onRequest = true;
                    callUberButton.setText("Cancel Uber");
                    checkForUpdates();
                }
            }
        });
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    showUserLocation(lastKnownLocation);
                } else {
                    Log.i("Location Info", "Location Not Found.");
                }
            } else {
                ActivityCompat.requestPermissions(RiderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                showUserLocation(lastKnownLocation);
            } else {
                Log.i("Location Info", "Location Not Found.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    showUserLocation(lastKnownLocation);
                }
            }
        }
    }

    private void showUserLocation(Location userLocation) {
        LatLng user = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(user).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
    }

    public void callUber(View view) {
        if (!onRequest) {
            Log.i("Call Uber", "OK");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    ParseObject request = new ParseObject("Request");
                    ParseGeoPoint geoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    request.put("username", ParseUser.getCurrentUser().getUsername());
                    request.put("location", geoPoint);
                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                callUberButton.setText("Cancel Uber");
                                onRequest = true;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkForUpdates();
                                    }
                                }, 2000);

                            } else {
                                Toast.makeText(RiderActivity.this, "Request not saved properly. please try again later.", Toast.LENGTH_SHORT).show();
                                Log.i("Call Uber", e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(RiderActivity.this, "Could not find your location. please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.i("Cancel Uber", "OK");

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> requests, ParseException e) {
                    Log.i("requests - cancel uber", String.valueOf(requests.size()));
                    if(e == null) {
                        for(ParseObject request : requests) {
                            Log.i("request - username: ", request.getString("username"));

                            ParseGeoPoint geoPoint = request.getParseGeoPoint("location");

                            Log.i("request - latitude: ", String.valueOf(geoPoint.getLatitude()));
                            Log.i("request - longitude: ", String.valueOf(geoPoint.getLongitude()));

                            request.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Log.i("Deletion Info", "Successful");
                                    } else {
                                        Log.i("Deletion Info", e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            });

            onRequest = false;
            callUberButton.setText("Call Uber");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(RiderActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.i("Logout", e.getMessage());
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void checkForUpdates() {
        Log.i("Info", "Enter checkForUpdates()");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereExists("drivername");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0) {
                    final ParseObject activeRequest = objects.get(0);

                    String drivername = activeRequest.getString("drivername");
                    final ParseGeoPoint riderLocation = activeRequest.getParseGeoPoint("location");

                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    userQuery.whereEqualTo("userType", "driver");
                    userQuery.whereEqualTo("username", drivername);
                    userQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if(e == null && objects.size() > 0) {
                                callUberButton.setVisibility(View.INVISIBLE);

                                ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("location");

                                double distance = driverLocation.distanceInKilometersTo(riderLocation);

                                if(distance > 0.05) {

                                    LatLng riderLatLng = new LatLng(riderLocation.getLatitude(), riderLocation.getLongitude());
                                    LatLng driverLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(riderLatLng);
                                    builder.include(driverLatLng);
                                    LatLngBounds bounds = builder.build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.30); // offset from edges of the map 30% of screen

                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    mMap.addMarker(new MarkerOptions().position(riderLatLng).title("Your location"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

                                    infoTextView.setText("Your driver is " + String.format("%.2f",distance) + " kilometers away. Please wait.");

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            checkForUpdates();
                                        }
                                    }, 2000);

                                } else {

                                    infoTextView.setText("Your driver is here!");

                                    activeRequest.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.i("Deletion Info", "Successful");
                                        }
                                    });

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            callUberButton.setVisibility(View.VISIBLE);
                                            callUberButton.setText("Call Uber");
                                            infoTextView.setText("");
                                            onRequest = false;
                                        }
                                    }, 5000);
                                }
                            }
                        }
                    });
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastKnownLocation != null) {
                            showUserLocation(lastKnownLocation);
                        }
                    }
                }
            }
        });
    }
}
