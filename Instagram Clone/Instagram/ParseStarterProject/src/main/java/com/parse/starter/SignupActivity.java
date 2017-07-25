package com.parse.starter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static android.R.attr.keycode;

public class SignupActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText pwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        TextView loginLink = (TextView) findViewById(R.id.loginLink);
        RelativeLayout signupRelativeLayout = (RelativeLayout) findViewById(R.id.signupRelativeLayout);
        ImageView logImageView = (ImageView) findViewById(R.id.logImageView);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        pwdEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keycode == KeyEvent.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN) {
                    Log.i("KeyEvent", "Enter is pressed down");
                    signUp(v);
                    return true;
                }

                return false;
            }
        });

        signupRelativeLayout.setOnClickListener(new View.OnClickListener() {
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
    }

    public void signUp(View view) {
        final String username = nameEditText.getText().toString();
        final String password = pwdEditText.getText().toString();

        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "A username and password are required.", Toast.LENGTH_SHORT).show();
        } else {
            // progress dialog when authenticating user info
            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Log.i("Signup", "Successful");
                        progressDialog.dismiss();

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", username);
                        returnIntent.putExtra("password", password);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
