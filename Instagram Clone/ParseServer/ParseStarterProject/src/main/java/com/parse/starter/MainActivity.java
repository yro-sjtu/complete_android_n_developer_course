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
import android.view.View;
import android.widget.Switch;

import com.parse.FindCallback;
import com.parse.GetCallback;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
        query.whereGreaterThan("score", 200);

//        query.whereEqualTo("username", "chocolate");
//        query.setLimit(1);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0) {
                    for(ParseObject object : objects) {
                        object.put("score", 50 + object.getInt("score"));
                        object.saveInBackground();

                        Log.i("Result", "username: " + object.getString("username") +
                                       ", " + "score: " + object.getInt("score"));
                    }
                }
            }
        });

    /*
    ParseObject score = new ParseObject("Score");
    score.put("username", "frank");
    score.put("score", 86);
    score.saveInBackground(new SaveCallback(){

      @Override
      public void done(ParseException e) {
        if(e == null) {
          Log.i("SaveInBackground", "Successful");
        } else {
          Log.i("SaveInBackground", "Failed, Error: ");
          e.printStackTrace();
        }
      }
    });
    */

    /*
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
    query.getInBackground("o8WorQNsUJ", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if(e == null && object != null) {
          object.put("score", 200);
          object.saveInBackground();

          Log.i("ObjectValue", object.getString("username"));
          Log.i("ObjectValue", String.valueOf(object.getInt("score")));
        }
      }
    });
    */
        /*
        ParseObject object = new ParseObject("Tweet");
        object.put("username", "Frank");
        object.put("content", "I like computer science");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i("Save Result", "Successful");
                } else {
                    Log.i("Save Result", "Failed");
                }
            }
        });
        */

        /*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
        query.getInBackground("VZ5mWPx4qT", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null) {
                    Log.i("Get Result", "Successful");

                    Log.i("Tweet Info", object.getString("username"));
                    Log.i("Tweet Info", object.getString("content"));

                } else {
                    Log.i("Get Result", "Failed");
                }
            }
        });
        */

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}