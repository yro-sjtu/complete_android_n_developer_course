package com.yellowredorange.uber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    static final int REQUEST_CODE = 1;
    static final int MAX_NUMBER_REQUEST = 6;

    LocationManager locationManager;
    LocationListener locationListener;

    double[] driverLocation;
    List<String> distances;
    List<double[]> riders;
    List<String> ridernames;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        setTitle("Nearby Requests");

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateDistInList(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        ListView listView = (ListView) findViewById(R.id.listView);
        distances = new ArrayList<>();
        riders = new ArrayList<>();
        ridernames = new ArrayList<>();
        adapter = new ArrayAdapter<>(ViewRequestActivity.this, android.R.layout.simple_list_item_1, distances);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                locationManager.removeUpdates(locationListener);

                Intent intent = new Intent(ViewRequestActivity.this, DriverLocationActivity.class);
                intent.putExtra("driver", driverLocation);
                intent.putExtra("rider", riders.get(position));
                intent.putExtra("ridername", ridernames.get(position));
                startActivity(intent);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location driverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateDistInList(driverLocation);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location driverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateDistInList(driverLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location driverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateDistInList(driverLocation);
                } else {
                    Toast.makeText(ViewRequestActivity.this, "Location Permission is NOT granted.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.i("request permission", "error: requestCode doesn't match.");
        }
    }

    private void updateDistInList(final Location driver) {
        if(driver != null) {

//            final ProgressDialog progressDialog = new ProgressDialog(ViewRequestActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Loading requests...");
//            progressDialog.show();

            final ParseGeoPoint driverGeoPoint = new ParseGeoPoint(driver.getLatitude(), driver.getLongitude());
            driverLocation = new double[]{driverGeoPoint.getLatitude(), driverGeoPoint.getLongitude()};

            ParseUser user = ParseUser.getCurrentUser();
            user.put("location", driverGeoPoint);
            user.saveInBackground();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
            query.whereNear("location", new ParseGeoPoint(driver.getLatitude(), driver.getLongitude()));
            query.whereDoesNotExist("drivername");
            query.setLimit(MAX_NUMBER_REQUEST);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    Log.i("# of query objects", String.valueOf(objects.size()));

                    if (e == null && objects != null && objects.size() > 0) {

//                        progressDialog.dismiss();

                        distances.clear();
                        riders.clear();
                        ridernames.clear();

                        for (ParseObject object : objects) {
                            ParseGeoPoint riderGeoPoint = (ParseGeoPoint) object.get("location");

                            double dist = driverGeoPoint.distanceInKilometersTo(riderGeoPoint);
                            distances.add(String.format("%.2f kilometers", dist));
                            riders.add(new double[]{riderGeoPoint.getLatitude(), riderGeoPoint.getLongitude()});
                            ridernames.add(object.getString("username"));
                        }
                    } else {

                        Toast.makeText(ViewRequestActivity.this, "No active nearby requests. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            Toast.makeText(ViewRequestActivity.this, "Your current location is not found. Please try again later.", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(ViewRequestActivity.this, MainActivity.class);
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
}

