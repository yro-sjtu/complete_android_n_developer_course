package com.yellowredorange.timerdemo;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Countdown is counting down (every second)
                Log.i("Second Left", String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Countdown is Finished (10 seconds)
                Log.i("Done", "Countdown Timer is Finished");
            }
        }.start();

        /*
        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                // insert code to be run every second
                Log.i("Runnable has run!", "A second has passed...");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(run);
        */


    }
}
