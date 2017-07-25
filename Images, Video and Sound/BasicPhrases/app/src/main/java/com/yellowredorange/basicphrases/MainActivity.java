package com.yellowredorange.basicphrases;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonTapped(View view) {
//        boolean res = view instanceof Button;
//        Log.i("type info", String.valueOf(res));

        int id = view.getId();
        String name = view.getResources().getResourceEntryName(id);
//        String name = view.getResources().getResourceName(id); // package:type is added, "package:type/entryName"

        int resourceId = view.getResources().getIdentifier(name, "raw", getPackageName());

        MediaPlayer mplayer = MediaPlayer.create(this, resourceId);
        mplayer.start();

        Log.i("button tapped", name);
    }
}
