package com.yellowredorange.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button btn0;
    Button btn1;
    Button btn2;
    Button btn3;

    ArrayList<String> celebNames = new ArrayList<>();
    ArrayList<String> celebUrls = new ArrayList<>();
    int chosenCeleb = 0;
    int locOfCorrectAnswer = 0;
    int[] answers = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageView);
        btn0 = (Button) findViewById(R.id.button0);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();

        try {
            String result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splited = result.split("<div class=\"siderBarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splited[0]);
            while(m.find()) {
                celebUrls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splited[0]);
            while(m.find()) {
                celebNames.add(m.group(1));
            }

            generateQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateQuestion() {
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebUrls.size());
            ImageLoader loader = new ImageLoader();
            Bitmap imgBitmap = loader.execute(celebUrls.get(chosenCeleb)).get();
            image.setImageBitmap(imgBitmap);

            locOfCorrectAnswer = rand.nextInt(4);
            for (int i = 0; i < answers.length; i++) {
                if (i != locOfCorrectAnswer) {
                    int idx = rand.nextInt(celebUrls.size());
                    while (idx == chosenCeleb) {
                        idx = rand.nextInt(celebUrls.size());
                    }
                    answers[i] = idx;
                } else {
                    answers[i] = chosenCeleb;
                }
            }

            btn0.setText(celebNames.get(answers[0]));
            btn1.setText(celebNames.get(answers[1]));
            btn2.setText(celebNames.get(answers[2]));
            btn3.setText(celebNames.get(answers[3]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void celebChosen(View view) {

        String msg = null;

        if(Integer.parseInt(view.getTag().toString()) == locOfCorrectAnswer) {

            msg = "Correct!";

        } else {

            msg = "Wrong! It was " + celebNames.get(chosenCeleb);
        }
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

        generateQuestion();
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection connection = null;
            String result = "";

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while(data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection connection = null;

            try{

                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                Bitmap imgBitmap = BitmapFactory.decodeStream(is);
                return imgBitmap;

            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
