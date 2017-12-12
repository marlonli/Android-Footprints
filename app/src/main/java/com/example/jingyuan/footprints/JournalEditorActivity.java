package com.example.jingyuan.footprints;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JournalEditorActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int JOURNAL_EDITOR_REQ = 1;
    private static final int IMAGE = 79;
    private static final int REQ_CODE_TAKE_PICTURE = 90210;
    private Journal journal;
    private EditText et_title;
    private EditText et_content;
    private EditText et_date;
    private ImageButton ib_save;
    private ImageButton ib_location;
    private ImageButton ib_tags;
    private ImageButton ib_photos;
    private ImageButton ib_camera;
    private Bitmap bmp;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    //Latitude and Longitude of current location
    private double currentLatitude;
    private double currentLongitude;

    String date_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_editor);

        // Initialization
        initialization();

        // Get intent
        Intent intent = getIntent();
        journal = (Journal) intent.getSerializableExtra(JOURNAL_OBJECT);
        // TODO: edit journal if j != null
        Log.v("Journal Editor", "journal: " + journal);
        if (journal != null) {
            et_title.setText(journal.getTitle());
            et_content.setText(journal.getContent());
        }

        // Set the botton click action for location(Map) button
        ib_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getCurrentLocation();
                ShowMap();
            }
        });

        // click on tags button to save the current location
        ib_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

            }
        });

        ib_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
            }
        });

        ib_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
            }
        });

        // Set date
        // Format: Sat Dec 02 19:19:45 EST 2017
        String[] date = Calendar.getInstance().getTime().toString().split(" ");
        et_date = (EditText) findViewById(R.id.editText_date);
        date_string = date[0] + ", " + date[1] + " " + date[2] + ", " + date[5];
        et_date.setText(date_string);

        final long current_time_long =  System.currentTimeMillis();

        // Set save button and image button
        // TODO: set image button add photo
        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: set return value (save to database)

                // If it is a null journal
                if (journal==null){
                    // Set journal class
                    String debug1 = et_title.getText().toString();
//                    journal.setTitle(debug1);
//                    journal.setContent(et_content.getText().toString());
//                    journal.setDateTime(current_time_long);
//                    journal.setLat(currentLatitude+"");
//                    journal.setLng(currentLongitude+"");
//
                    journal = new Journal(debug1, null,current_time_long,
                    currentLatitude+"",currentLongitude+"", et_content.getText().toString());

                    // Save the new journal in the database.
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Users = database.getReference("Users");
                    String key = Users.child("journal").push().getKey();
                    Users.child("journal").child(key).setValue(journal);
                }
                else{
                    //save data here:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference Users = database.getReference("Users");
                    Users.child("journal").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap:dataSnapshot.getChildren()){
                                String key = snap.getKey();
                                String title_tmp = (String) snap.child("title").getValue();
                                if (title_tmp.equals(journal.getTitle())){
                                    Map<String,Object> UP = new HashMap<>();
                                    UP.put("/Users/journal/"+key,journal);
                                    Users.updateChildren(UP);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

//                Users.child("journal").push().setValue(journal.getContent());


//                Intent intent = new Intent(JournalEditorActivity.this, MainActivity.class);
//
//                intent.putExtra(JOURNAL_OBJECT, journal);
////                intent.putExtra("addPerson", add);
////                intent.putExtra("size", relation.size());
//
//                setResult(JOURNAL_EDITOR_REQ, intent);
                finish();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE
                && resultCode == RESULT_OK) {
            bmp = (Bitmap) intent.getExtras().get("data");
            //not finished
            //bmp should be stored in db
        }

        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && intent != null) {
            Uri selectedImage = intent.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            saveImage(imagePath);
            c.close();
        }
    }

    private void saveImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        //save the bm in db
    }

    private void initialization() {
        et_title = (EditText) findViewById(R.id.editText_title);
        et_content = (EditText) findViewById(R.id.editText_content);
        ib_save = (ImageButton) findViewById(R.id.imageButton_save);
        ib_location = (ImageButton) findViewById(R.id.imageButton_location);
        ib_tags = (ImageButton) findViewById(R.id.imageButton_tags);
        ib_photos = (ImageButton) findViewById(R.id.imageButton_photos);
        ib_camera = (ImageButton) findViewById(R.id.imageButton_camera);
    }

    private void ShowMap(){
        Intent intent = new Intent();
        intent.setClass(JournalEditorActivity.this, MapsActivity.class);

        intent.putExtra("latitude", currentLatitude);
        intent.putExtra("longitude", currentLongitude);

        startActivity(intent);
    }

    private void getCurrentLocation() {
        if (!checkPermissionsforcoarse()) {
            requestPermissionsforcoarse();
        } else {
            getLastLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            currentLatitude = mLastLocation.getLatitude();
                            currentLongitude = mLastLocation.getLongitude();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    private boolean checkPermissionsforcoarse() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsforcoarse() {
        ActivityCompat.requestPermissions(JournalEditorActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
