package com.yellowredorange.eggtimerapp;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timerTextView;
    SeekBar timerSeekBar;
    Button button;
    CountDownTimer counter;
    int secondInTotal;
    boolean counterIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secondInTotal = 30;
        counterIsActive = false;
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        button = (Button) findViewById(R.id.controllerButton);
        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        timerSeekBar.setMax(600);
        timerSeekBar.setProgress(30);
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                secondInTotal = progress;
                updateTimer(secondInTotal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void controlTimer(View view) {
        if(!counterIsActive) {
            counterIsActive = true;
            timerSeekBar.setEnabled(false);
            button.setText("Stop");
            counter = new CountDownTimer(1000 * secondInTotal, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondUntilFinished = (int) (millisUntilFinished / 1000);
                    updateTimer(secondUntilFinished);
                }
                @Override
                public void onFinish() {
                    updateTimer(0);
                    MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                    mplayer.start();
                }
            };
            counter.start();
        } else {
            resetTimer();
        }
    }

    private void resetTimer() {
        timerTextView.setText("00:30");
        button.setText("Go");
        timerSeekBar.setEnabled(true);
        timerSeekBar.setProgress(30);
        counter.cancel();
        counterIsActive = false;
    }

    private void updateTimer(int secondInTotal) {
        String minute = String.format("%02d", secondInTotal / 60);
        String second = String.format("%02d", secondInTotal % 60);
        timerTextView.setText(minute + ":" + second);
    }
}
