package com.yellowredorange.sounddemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.util.Log;
import android.media.AudioManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mplayer;
    AudioManager audioManager;

    public void play(View view) {
        mplayer.start();
    }

    public void pause(View view) {
        mplayer.pause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mplayer = MediaPlayer.create(this, R.raw.kid_laugh);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar volumeControl = (SeekBar) findViewById(R.id.seekBar);

        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curVolume);

        volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.i("SeekBar value", Integer.toString(progress));

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar scrubber = (SeekBar) findViewById(R.id.seekBar2);
        scrubber.setMax(mplayer.getDuration());

//        new Timer().scheduleAtFixedRate(new TimerTask(){
//            public void run() {
//                scrubber.setProgress(mplayer.getCurrentPosition());
//            }
//        }, 0, 100);

        scrubber.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mplayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
