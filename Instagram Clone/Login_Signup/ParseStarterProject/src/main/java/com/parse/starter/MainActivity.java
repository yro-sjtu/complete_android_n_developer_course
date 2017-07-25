/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.SignUpCallback;

import java.util.List;

import static android.R.attr.keycode;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 1;

    EditText nameEditText;
    EditText pwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        TextView signupLink = (TextView) findViewById(R.id.signupLink);
        RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        ImageView logImageView = (ImageView) findViewById(R.id.logImageView);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        pwdEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keycode == KeyEvent.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN) {
                    Log.i("KeyEvent", "Enter is pressed down");
                    logIn(v);
                    return true;
                }

                return false;
            }
        });

        mainRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        logImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        if(ParseUser.getCurrentUser() != null) {
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void logIn(View view) {
        String username = nameEditText.getText().toString();
        String password = pwdEditText.getText().toString();
        logInHelper(username, password);
    }

    private void logInHelper(String username, String password) {
        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "A username and password are required.", Toast.LENGTH_SHORT).show();
        } else {
            // progress dialog when authenticating user info
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user == null || e != null) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("Login", "Successful");
                        progressDialog.dismiss();

                        showUserList();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SIGNUP) {
            if(resultCode == Activity.RESULT_OK) {
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                logInHelper(username, password);
            }
        }
    }
}