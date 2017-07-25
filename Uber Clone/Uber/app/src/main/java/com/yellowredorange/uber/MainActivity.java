package com.yellowredorange.uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    Switch userTypeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);

        if(ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.i("Info", "Anomymous Login Successful");
                    } else {
                        Log.i("Info", "Anomymous Login Failed");
                    }
                }
            });
        } else {
            if(ParseUser.getCurrentUser().get("userType") != null) {
                Log.i("Info", "Redirecting as " + ParseUser.getCurrentUser().get("userType"));
            }
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void getStarted(View view) {

        Log.i("Switch value", String.valueOf(userTypeSwitch.isChecked()));

        final String userType = userTypeSwitch.isChecked()? "driver" : "rider";

        ParseUser.getCurrentUser().put("userType", userType);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i("Info", "Redirecting as " + userType);
                redirect(userType);
            }
        });
    }

    private void redirect(String userType) {
        if(userType.equals("rider")) {
            Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
            startActivity(intent);
        } else {
             Intent intent = new Intent(getApplicationContext(), ViewRequestActivity.class);
             startActivity(intent);
        }
    }
}