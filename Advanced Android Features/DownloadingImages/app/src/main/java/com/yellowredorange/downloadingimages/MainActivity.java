package com.yellowredorange.downloadingimages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView downloadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadImg = (ImageView) findViewById(R.id.imageView);
    }

    public void downloadImage(View view) {
        Log.i("Interaction", "Button tapped");

        ImageDownloader task = new ImageDownloader();

        try {

            Bitmap myImage = task.execute("https://upload.wikimedia.org/wikipedia/en/a/aa/Bart_Simpson_200px.png").get();

            downloadImg.setImageBitmap(myImage);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream is = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(is);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }
    }
}
