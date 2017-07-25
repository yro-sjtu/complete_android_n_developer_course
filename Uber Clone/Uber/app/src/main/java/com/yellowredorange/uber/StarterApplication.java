package com.yellowredorange.uber;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("58d0fd7f74f7c028c51212612433732dffbdcf22")
                .clientKey("a229c5ef0df778455c97f66682bfb2aaceeaccc5")
                .server("http://ec2-52-15-143-79.us-east-2.compute.amazonaws.com:80/parse/")
                .build()
        );

        // ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}