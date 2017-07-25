package com.yellowredorange.photoimportdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.net.Uri;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if(grantResults != null && grantResults.length > 0) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoto();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            getPhoto();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    Uri uri = data.getData();
//                    // method 1:
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//
//                        imageView.setImageBitmap(bitmap);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                     //method 2:
                     imageView.setImageURI(uri);
                }
            }
        }
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
