/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
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
            Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
            startActivity(intent);
        }
    }
}