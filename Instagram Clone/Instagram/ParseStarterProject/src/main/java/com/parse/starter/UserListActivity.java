package com.parse.starter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("User Feed");

        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> users = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(users.size() > position) {
                    String username = users.get(position);

                    Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null) {
                    for (ParseUser parseUser : parseUsers) {
                        String username = parseUser.getString("username");
                        users.add(username);
                    }
                    adapter.notifyDataSetChanged();

                    Log.i("Users", users.toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // Build.VERSION_CODES.M: M stands for Marshmallow
                getPhoto();
            } else {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    getPhoto();
                }
            }
            return true;
        } else if (item.getItemId() == R.id.logout) {
            ParseUser user = ParseUser.getCurrentUser();

            final ProgressDialog progressDialog = new ProgressDialog(UserListActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            user.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    finish();
                }
            });
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    Uri imageUri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        Log.i("Photo", "Received");

                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        byte[] byteArray = outStream.toByteArray();
                        ParseFile file = new ParseFile(byteArray);
                        ParseObject object = new ParseObject("Image");
                        object.put("image", file);
                        object.put("username", ParseUser.getCurrentUser().getUsername());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    Toast.makeText(getApplicationContext(), "Image shared!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Image sharing failed, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}