package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        Intent intent = getIntent();
        String activeUsername = intent.getStringExtra("username");
        setTitle(activeUsername + "'s Feed");

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ParseQuery query = ParseQuery.getQuery("Image");
        query.whereEqualTo("username", activeUsername);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>(){

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                try {
                    if (e == null) {
                        for (ParseObject object : objects) {
                            ParseFile file = object.getParseFile("image");
                            byte[] byteArray = file.getData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));

                            // getDrawable(int resid) is deprecated in api 22
                            // reference: https://stackoverflow.com/questions/29041027/android-getresources-getdrawable-deprecated-api-22
                            imageView.setImageBitmap(bitmap);
                            linearLayout.addView(imageView);
                        }
                    }
                } catch(Exception err) {
                    err.printStackTrace();
                }
            }
        });
    }
}
