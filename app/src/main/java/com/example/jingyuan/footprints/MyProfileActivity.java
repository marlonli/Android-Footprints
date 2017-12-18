package com.example.jingyuan.footprints;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    private static final String MY_USERNAME = "username";
    private static final String MY_PROFILE = "profile";
    private static final String PERSON_OBJECT = "person";
    private static final String PERSON_POSITION = "position";
    private static final int OPEN_PROFILE_REQ = 2;
    private static final String EDITOR_MODE = "mode";
    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int READ = 11;
    private int position; // if position == 0: my profile
    private String userName;
    private static final String DBName = "New_users";
    private boolean imageTaken = false;
    private static final int IMAGE_CAPTURE_REQUEST = 97;
    private static final int IMAGE_CROP_REQUEST = 98;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = MyProfileActivity.class.getSimpleName();

    private User user;
    private ImageButton ib;
    private TextView myUsername;
    private TextView journalNum;
    private TextView friendsNum;
    private Toolbar myToolbar;
//    private TextView editButton;
//    private ImageButton resetButton;
    private PopupWindow popupWindow;
    private ArrayList<Journal> journal_list;
    private ListView listView;
    private MyJournalViewAdapter mAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;
    private int friend_num;

    protected Uri photoUri = null;
    protected Uri photoOutputUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialization
        ib = (ImageButton) findViewById(R.id.imageButton_profile);
        myUsername = (TextView) findViewById(R.id.textView_username);
        journalNum = (TextView) findViewById(R.id.textView_num_journals);
        friendsNum = (TextView) findViewById(R.id.textView_num_friends);
//        editButton = (TextView) findViewById(R.id.textView_edit);
//        resetButton = (ImageButton) findViewById(R.id.imageButton_reset_password);
        listView = (ListView) findViewById(R.id.listView_journals);
        popupWindow = null;
        journal_list = new ArrayList<>();

        //addTestData();

        // Set toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Set view
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(PERSON_OBJECT);
        userName = user.getUsername();
        myUsername.setText(userName);
        mDatabase = FirebaseDatabase.getInstance().getReference(DBName);
        userRef = mDatabase.child(userName);

        // show profile
        if(user.getProfileByteArray() != null) {
            Bitmap myProfile = BitmapFactory.decodeByteArray(user.getProfileByteArray(), 0, user.getProfileByteArray().length);
            ib.setImageBitmap(myProfile);
        }
        // display username
        myUsername.setText(user.getUsername());

        // load other info from DB
        loadInfo(userName, new LoadDataCallback() {
            @Override
            public void loadFinish() {
                // display number of journals
                journalNum.setText("" + journal_list.size());
                // display number of friends
                friendsNum.setText("" + friend_num);
            }
        });


//        if (user.getProfile() != null)
//            ib.setImageBitmap(user.getProfile());


//        if (user.getMyJournals() != null) {
//            int size = user.getMyJournals().size();
//            journalNum.setText("" + size);
//        }

//        if (user.getMyFriends() != null) {
//            int size = user.getMyJournals().size();
//            friendsNum.setText("" + size);
//        }

        // If not my profile
        position = intent.getIntExtra(PERSON_POSITION, 0);
        if (position != 0) {
//            myUsername.setEnabled(false);
//            resetButton.setClickable(false);
//            resetButton.setEnabled(false);
//            resetButton.setVisibility(View.GONE);
            ib.setClickable(false);
            ib.setEnabled(false);

            // Set listView
            mAdapter = new MyJournalViewAdapter(this, journal_list);
            listView.setAdapter(mAdapter);

        } else {

        }
        Log.v("MyProfile status", "position: " + position);



        // imageButton on click
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: open photo lib (or camera)
                if(!imageTaken) {
                    // open camera
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST);
//                    startCamera();
//                    openOptionsMenu();
                }
            }
        });

        // Edit password
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LayoutInflater factory = LayoutInflater.from(MyProfileActivity.this);
//                view = factory.inflate(R.layout.popup_edit_username, null);
//                final EditText newPassword = (EditText) view.findViewById(R.id.editText_username);
//                new AlertDialog.Builder(view.getContext())
//                        .setTitle("Edit Password")     //title
//                        .setView(view)
//                        .setPositiveButton("Confirm",
//                                new android.content.DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        //myUsername.setText(newName.getText().toString());
//                                        savePassWordtoDB(newPassword.getText().toString());
//                                    }
//                                }).setNegativeButton("Cancel", null).create().show();
//            }
//        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openEditor(i);
            }
        });

    }

//<<<<<<< HEAD
    private void loadInfo(final String myName, final LoadDataCallback callback) { // TODO: change code to use userRef
//=======
//    private void startCamera() {
//        // Use name as photo id
//        File file = new File(getExternalCacheDir(), userName + ".jpg");
//        Log.v("capture", "startCamera imagename: " + userName);
//        try {
//            if(file.exists()) {
//                Log.v("capture", "startCameraimagename exists! ");
//                file.delete();
//            }
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        /**
//         * Use FileProvider to share data
//         */
//        if(Build.VERSION.SDK_INT >= 24) {
//            photoUri = FileProvider.getUriForFile(this, "com.example.jingyuan.footprints.fileprovider", file);
//            Log.v("capture", "startCamera set photoUri" + photoUri);
//        } else {
//            photoUri = Uri.fromFile(file);
//        }
//        // start image capture
//        Intent takePhotoIntent = new Intent();
//        takePhotoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Set output dir
//        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//        startActivityForResult(takePhotoIntent, IMAGE_CAPTURE_REQUEST);
//
//    }
//
//    private void cropPhoto(Uri inputUri) {
//        // crop action
//        Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
//        // Set uri and type
//        cropPhotoIntent.setDataAndType(inputUri, "image/*");
//        // authorize reading uri
//        cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        // set output file dir
//        cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                photoOutputUri = Uri.parse("file:////sdcard/" + userName + ".jpg"));
//        startActivityForResult(cropPhotoIntent, IMAGE_CROP_REQUEST);
//    }
//
//    private void loadInfo(final String myName) { // TODO: change code to use userRef
//>>>>>>> origin/master
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(DBName);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String curName = "";
                for(DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    curName = (String)userSnap.child("username").getValue();
                    if(curName.equals(myName)) {
                        // load and decode profile
                        if(userSnap.child("profile").getValue() != null) {
                            String encodedProfile = (String) userSnap.child("profile").getValue();
                            byte[] decodedByteArray = Base64.decode(encodedProfile, Base64.DEFAULT);
                            Bitmap myProfile = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
                            ib.setImageBitmap(myProfile);
                        }
                        else
                            ib.setImageResource(R.drawable.ic_person_black_24dp);
                        loadFriends(userSnap);
                        loadJournals(userSnap);
                    }
                }

                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFriends(DataSnapshot userSnap) {
        friend_num = (int)userSnap.child("friends").getChildrenCount();
    }

    private void loadJournals(DataSnapshot userSnap) {
        DataSnapshot journals = userSnap.child("journal_list");
        for(DataSnapshot journal : journals.getChildren()) {
            String title = (String)journal.child("title").getValue();
            ArrayList<String> tags = new ArrayList<>();
            if(journal.child("tags").getValue() != null)
                tags = (ArrayList<String>)journal.child("tags").getValue();
            ArrayList<String> photos = new ArrayList<>();
            if(journal.child("photos").getValue() != null)
                photos = (ArrayList<String>)journal.child("photos").getValue();
            long datetime = (long)journal.child("dateTimeLong").getValue();
            String lat = (String)journal.child("lat").getValue();
            String lng = (String)journal.child("lng").getValue();
            String address = "";
            if(journal.child("address").getValue() != null)
                address = (String)journal.child("address").getValue();
            String content = (String)journal.child("content").getValue();
            Journal j = new Journal(title, tags, datetime, lat, lng, content);
            j.setAddress(Utilities.latLngToLoc(this, lat, lng));
            journal_list.add(j);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_CAPTURE_REQUEST && resultCode == Activity.RESULT_OK) {
//            cropPhoto(photoUri);
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap)extras.get("data");
            ib.setImageBitmap(photo);
            imageTaken = true;
            saveProfiletoDB(photo);
        }
//        else if (requestCode == IMAGE_CROP_REQUEST && resultCode == Activity.RESULT_OK) {
//            File file = new File(photoOutputUri.getPath());
//            if(file.exists()) {
//                Log.v("capture", "onActivityResult" + photoOutputUri.getPath());
//                Bitmap photo = BitmapFactory.decodeFile(photoOutputUri.getPath());
//                ib.setImageBitmap(photo);
//                imageTaken = true;
//                saveProfiletoDB(photo);
//            } else {
//                Toast.makeText(this, "Image not found!", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void saveProfiletoDB(Bitmap photo) {
        //userRef.child()
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        userRef.child("profile").setValue(imageEncoded);
    }

    private void savePassWordtoDB(String pw) {
        userRef.child("password").setValue(pw);
    }

    public void openEditor(int journalIndex) {
        Intent intent = new Intent(this, JournalEditorActivity.class);
        intent.putExtra(EDITOR_MODE, READ);
        intent.putExtra("journal_name", journal_list.get(journalIndex).getTitle());
        intent.putExtra("username",myUsername.getText().toString());
        startActivity(intent);
    }

    private void addTestData() {
        long startDate1 = 0;
        long startDate2 = 0;
        try {
            String dateString1 = "11/09/2017 14:09:03";
            String dateString2 = "11/16/2017 14:09:03";
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = sdf.parse(dateString1);
            Date date2 = sdf.parse(dateString2);

            startDate1 = date.getTime();
            startDate2 = date2.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // test data
        long currentTime = System.currentTimeMillis();
        ArrayList<String> testTags = new ArrayList<>();
        testTags.add("tag1");
        testTags.add("tag2");
        testTags.add("tag3");
        journal_list.add(new Journal("Journal1", testTags, currentTime, "30", "120", getString(R.string.large_text)));
        journal_list.add(new Journal("Journal title", testTags, currentTime - 86400000, "30", "-120", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journal_list.add(new Journal("OMG OMG", testTags, startDate1, "40", "-74", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journal_list.add(new Journal("Journal1", testTags, startDate2, "40", "-110", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));
        journal_list.add(new Journal("Journal1", testTags, currentTime, "30", "-70", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));

    }

    // Add menu to action bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        if (position != 0)
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset_password:
                // Reset Password
                LayoutInflater factory = LayoutInflater.from(MyProfileActivity.this);
                View view = factory.inflate(R.layout.popup_edit_username, null);
                final EditText newPassword = (EditText) view.findViewById(R.id.editText_username);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Reset Password")     //title
                        .setView(view)
                        .setPositiveButton("Confirm",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //myUsername.setText(newName.getText().toString());
                                        savePassWordtoDB(newPassword.getText().toString());
                                    }
                                }).setNegativeButton("Cancel", null).create().show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PERSON_POSITION, position);
            // TODO: send profile bitmap
            if (position == 0) {
                // Set activity result on back.
                String username = myUsername.getText().toString();
//                ib.setDrawingCacheEnabled(true);
//                Bitmap profile =  ib.getDrawingCache();
                user.setUsername(username);
//                user.setProfile(profile);

                intent.putExtra(PERSON_OBJECT, user);
            }
            Log.v("MyProfile status", "Result sent");
            setResult(OPEN_PROFILE_REQ, intent);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



}
