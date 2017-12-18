package com.example.jingyuan.footprints;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalEditorActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String JOURNAL_OBJECT = "journalObj";
    private static final int JOURNAL_EDITOR_REQ = 1;
    private static final int IMAGE = 79;

    private static final int REQ_CODE_TAKE_PICTURE = 33556;
    private static final String EDITOR_MODE = "mode";
    private static final int EDIT = 10;
    private static final int READ = 11;

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
    private TextView tv_address;
    private ListView lv_of_tag;
    private HorizontalScrollView scrollView_buttons;
    public LinearLayout bottomContainer;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    //Latitude and Longitude of current location
    private double currentLatitude;
    private double currentLongitude;

    String date_string;
    boolean editorMode = true;

    private String mAddressOutput;
    private SimpleAdapter simp_adapter;

    private ArrayList<String> tags;  // list_Of_Tags;
    private ArrayList<Bitmap> photos;    // list_Of_Images;
    private List<String> list_Of_Num;
    private List<Map<String, Object>> list_Of_Map;
    private ArrayList<String> tags_from_db;     //tag got from database

    public String username;
    public String journal_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_editor);

        // Get intent
        Intent intent = getIntent();
//        journal = (Journal) intent.getSerializableExtra(JOURNAL_OBJECT);
        username = (String) intent.getStringExtra("username");
        journal_name = (String) intent.getStringExtra("journal_name");
        editorMode =  (intent.getIntExtra(EDITOR_MODE, 10) == EDIT);
        set_journal_from_database(journal_name, new LoadDataCallback() {
            @Override
            public void loadFinish() {
                ini();
            }
        });


    }

    public void set_journal_from_database(final String journalName,final LoadDataCallback callback){
//        Journal new_journal = new Journal(null,null,0,null,null,null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference Users = database.getReference("New_users");
        DatabaseReference aaa = Users.child(username);
        DatabaseReference bbb = aaa.child("journal_list");
        bbb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    String key = snap.getKey();
                    if (key.equals(journalName)){
                        String title = (String) snap.child("title").getValue();
                        String content = (String) snap.child("content").getValue();
                        long dateTimeLong = (long) snap.child("dateTimeLong").getValue();
                        String dateTimeString = (String) snap.child("dateTimeString").getValue();
                        String lat = (String) snap.child("lat").getValue();
                        String lng = (String) snap.child("lng").getValue();
                        ArrayList<String> tags = (ArrayList<String>) snap.child("tags").getValue();
                        journal = new Journal(title, tags,dateTimeLong,lat,lng, content);
                        ArrayList<String> photo_string = (ArrayList<String>) snap.child("photoString").getValue();
                        journal.setPhoto_string(photo_string);
                    }
                }
                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // Convert bitmap to string
    public ArrayList<String> photo_to_string(ArrayList<Bitmap> photo_bit){
        ArrayList<String> photo_string = new ArrayList<String>();
        for (int i=0;i<photo_bit.size();i++){
            Bitmap photo_tmp = photo_bit.get(i);
            if(photo_tmp!=null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo_tmp.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
                photo_string.add(imageEncoded);
            }
        }
        return photo_string;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE
                && resultCode == RESULT_OK && intent != null) {
            Bitmap  photo = intent.getParcelableExtra("data");
            photos.add(photo);
            String size = Integer.toString(photos.size());
            Log.e("photo","after add now exist " + size);
        }

        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && intent != null) {
            try {
                handleImageOnKitKat(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleImageOnKitKat(Intent intent) throws IOException {
        String imagePath = null;
        Uri uri = intent.getData();
        Bitmap photoBmp = null;
        if (uri != null) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            photos.add(bitmap);
            Log.e("phots", "after add(from album) now exist " + Integer.toString(photos.size()));
        } else{
            Log.e("photo","album: uri is null");
        }
    }




    public ArrayList<Bitmap> photo_bit_to_string(ArrayList<String> photo_string){
        ArrayList<Bitmap> photo_bit = new ArrayList<Bitmap>();
        for (int i=0;i<photo_string.size();i++){
            String photo_string_tmp = photo_string.get(i);
            byte[] decodeByte = Base64.decode(photo_string_tmp,0);
            Bitmap photo_bit_tmp = BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
            photo_bit.add(photo_bit_tmp);
        }
        return photo_bit;
    }

    private void initialization() {
        et_title = (EditText) findViewById(R.id.editText_title);
        et_content = (EditText) findViewById(R.id.editText_content);
        ib_save = (ImageButton) findViewById(R.id.imageButton_save);
        ib_location = (ImageButton) findViewById(R.id.imageButton_location);
        ib_tags = (ImageButton) findViewById(R.id.imageButton_tags);
        ib_photos = (ImageButton) findViewById(R.id.imageButton_photos);
        ib_camera = (ImageButton) findViewById(R.id.imageButton_camera);
        tv_address = (TextView) findViewById(R.id.tv_location);
        scrollView_buttons = (HorizontalScrollView) findViewById(R.id.scrollView_tools);
        bottomContainer = (LinearLayout) findViewById(R.id.bottom_container);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAddressOutput = "";

        if(journal != null){
            et_title.setText(journal.getTitle());
            et_content.setText(journal.getContent());
            ArrayList<String> photos_string_tep = journal.getPhotos();

            if(photos_string_tep != null) {
                ArrayList<Bitmap> photos_tep = photo_bit_to_string(photos_string_tep);
                photos = photos_tep;
                Log.v("images", "add image: " + photos.size());
                for (int i = 0; i < photos.size(); i++) {
                    ImageView image = new ImageView(this);
                    image.setImageBitmap(photos.get(i));
                    bottomContainer.addView(image);
                }
        }

            tags_from_db = journal.getTags();
            if(tags_from_db != null)
                tags = tags_from_db;
            else {
                tags = new ArrayList<>();
            }

        }
        else {
            photos = new ArrayList<>();
            tags = new ArrayList<>();
        }

        list_Of_Num = new ArrayList<>();
        list_Of_Map = new ArrayList<>();

        // Set edit mode or read mode
        if (!editorMode) {
            et_title.setEnabled(false);
            et_content.setKeyListener(null);
            ib_camera.setEnabled(false);
            ib_location.setEnabled(false);
            ib_photos.setEnabled(false);
            ib_save.setEnabled(false);
            ib_tags.setEnabled(false);
            tv_address.setVisibility(View.GONE);
            scrollView_buttons.setVisibility(View.GONE);
        }
        else {
            getCurrentLocation();
            ShowAddress();
        }


        // Set the botton click action for location(Map) button
        ib_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getCurrentLocation();
                ShowAddress();
            }
        });

        // click on tags button to save the current location
        ib_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                LayoutInflater factory = LayoutInflater.from(JournalEditorActivity.this);
                view = factory.inflate(R.layout.tag_window, null);
                final EditText edit=(EditText)view.findViewById(R.id.window_tag_et);
                list_Of_Map.clear();
                if(tags != null) {
                    for(int i = 0; i < tags.size(); i++) {
                        String str_size = Integer.toString(i + 1)+".";
                        list_Of_Num.add(str_size);
                    }
                    for (int i = 0; i < tags.size(); i++) {
                        Map<String, Object> listem = new HashMap<String, Object>();
                        listem.put("index", list_Of_Num.get(i));
                        listem.put("tag", tags.get(i));
                        list_Of_Map.add(listem);
                    }
                    simp_adapter = new SimpleAdapter(view.getContext(), list_Of_Map, R.layout.listcontent,
                            new String[]{"index", "tag"}, new int[]{
                            R.id.listcontent_index, R.id.listcontent_content});
                    lv_of_tag = view.findViewById(R.id.lv_tags);
                    lv_of_tag.setAdapter(simp_adapter);
                }
                new AlertDialog.Builder(JournalEditorActivity.this)
                        .setTitle("Tags")     //title
                        .setView(view)
                        .setPositiveButton("Save",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String newtag = edit.getText().toString();
                                        tags.add(newtag);
                                        int size = tags.size();
                                        String str_size = Integer.toString(size)+".";
                                        Log.i("fdsd",str_size);
                                        list_Of_Num.add(str_size);
                                    }
                                }).setNegativeButton("Cancel", null).create().show();
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
                    journal = new Journal(et_title.getText().toString(), tags,current_time_long,
                            currentLatitude+"",currentLongitude+"", et_content.getText().toString());

                    ArrayList<String> photo_string = photo_to_string(photos);
                    journal.setPhoto_string(photo_string);

                    // Save the new journal in the database.
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Users = database.getReference("New_users");
                    Users.child(username).child("journal_list").child(journal.getTitle()).setValue(journal);
                }
                else{
                    // Update the new journal
                    ArrayList<String> photo_string = photo_to_string(photos);
                    journal.setPhoto_string(photo_string);
                    if (tags.size()!=0 && tags!=journal.getTags()){
                        journal.setTags(tags);
                    }
                    String title_new = et_title.getText().toString();
                    String content_new = et_content.getText().toString();
                    journal.setContent(content_new);
                    journal.setTitle(title_new);

                    //save data here:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference Users = database.getReference("New_users");
//                    DatabaseReference AAA = Users.child(username).child("journal_list");
                    Users.child(username).child("journal_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap:dataSnapshot.getChildren()){
                                String key_title = snap.getKey();
                                if (key_title.equals(journal.getTitle())){
                                    Map<String,Object> UP = new HashMap<>();
                                    UP.put("/"+username+"/journal_list/"+key_title,journal);
                                    Users.updateChildren(UP);
                                    finish();
                                    break;
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

//    private void initialization() {
//        et_title = (EditText) findViewById(R.id.editText_title);
//        et_content = (EditText) findViewById(R.id.editText_content);
//        ib_save = (ImageButton) findViewById(R.id.imageButton_save);
//        ib_location = (ImageButton) findViewById(R.id.imageButton_location);
//        ib_tags = (ImageButton) findViewById(R.id.imageButton_tags);
//        ib_photos = (ImageButton) findViewById(R.id.imageButton_photos);
//        ib_camera = (ImageButton) findViewById(R.id.imageButton_camera);
//        tv_address = (TextView) findViewById(R.id.tv_location);
//        scrollView_buttons = (HorizontalScrollView) findViewById(R.id.scrollView_tools);
//        bottomContainer = (LinearLayout) findViewById(R.id.bottom_container);
//        mResultReceiver = new AddressResultReceiver(new Handler());
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mAddressOutput = "";
//
//        if(journal != null){
//            et_title.setText(journal.getTitle());
//            et_content.setText(journal.getContent());
//            ArrayList<String> photos_string_tep = journal.getPhotos();
//
//            if(photos_string_tep != null) {
//                ArrayList<Bitmap> photos_tep = photo_bit_to_string(photos_string_tep);
//                photos = photos_tep;
//                Log.v("images", "add image: " + photos.size());
//                for (int i = 0; i < photos.size(); i++) {
//                    ImageView image = new ImageView(this);
//                    image.setImageBitmap(photos.get(i));
//                    bottomContainer.addView(image);
//                }
//            }
//
//            tags_from_db = journal.getTags();
//            if(tags_from_db != null)
//                tags = tags_from_db;
//            else {
//                tags = new ArrayList<>();
//            }
//
//        }
//        else {
//            photos = new ArrayList<>();
//            tags = new ArrayList<>();
//        }
//
//        list_Of_Num = new ArrayList<>();
//        list_Of_Map = new ArrayList<>();
//
//        // Set edit mode or read mode
//        if (!editorMode) {
//            et_title.setEnabled(false);
//            et_content.setKeyListener(null);
//            ib_camera.setEnabled(false);
//            ib_location.setEnabled(false);
//            ib_photos.setEnabled(false);
//            ib_save.setEnabled(false);
//            ib_tags.setEnabled(false);
//            tv_address.setVisibility(View.GONE);
//            scrollView_buttons.setVisibility(View.GONE);
//        }
//        else {
//            getCurrentLocation();
//            ShowAddress();
//        }
//    }


//    private void ShowMap(){
//        Intent intent = new Intent();
//        intent.setClass(JournalEditorActivity.this, MapsActivity.class);
//
//        intent.putExtra("latitude", currentLatitude);
//        intent.putExtra("longitude", currentLongitude);
//
//        startActivity(intent);
//    }

    public void ini(){
        et_title = (EditText) findViewById(R.id.editText_title);
        et_content = (EditText) findViewById(R.id.editText_content);
        ib_save = (ImageButton) findViewById(R.id.imageButton_save);
        ib_location = (ImageButton) findViewById(R.id.imageButton_location);
        ib_tags = (ImageButton) findViewById(R.id.imageButton_tags);
        ib_photos = (ImageButton) findViewById(R.id.imageButton_photos);
        ib_camera = (ImageButton) findViewById(R.id.imageButton_camera);
        tv_address = (TextView) findViewById(R.id.tv_location);
        bottomContainer = (LinearLayout) findViewById(R.id.bottom_container);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAddressOutput = "";
        photos = new ArrayList<>();
        tags = new ArrayList<>();

        if(journal != null){
            et_title.setText(journal.getTitle());
            et_content.setText(journal.getContent());
            if (editorMode) {
                ArrayList<String> photos_string_tep = journal.getPhotos();
                if(photos_string_tep != null) {
                    ArrayList<Bitmap> photos_tep = photo_bit_to_string(photos_string_tep);
                    photos = photos_tep;
                    Log.v("images", "add image: " + photos.size());
                    for (int i = 0; i < photos.size(); i++) {
                        ImageView image = new ImageView(this);
                        image.setImageBitmap(photos.get(i));
                        bottomContainer.addView(image);
                    }
                }
                Log.e("photo", "initial size is: " + Integer.toString(photos.size()));
            }

        }

        if(journal != null) {
            tags = journal.getTags();
            Log.v("tags",Integer.toString(tags.size()));
        }

        list_Of_Num = new ArrayList<>();
        list_Of_Map = new ArrayList<>();

        // Set edit mode or read mode
        if (!editorMode) {
            et_title.setEnabled(false);
            et_content.setKeyListener(null);
            ib_camera.setEnabled(false);
            ib_location.setEnabled(false);
            ib_photos.setEnabled(false);
            ib_save.setEnabled(false);
            ib_tags.setEnabled(false);
            tv_address.setVisibility(View.GONE);
            scrollView_buttons.setVisibility(View.GONE);
        }
        else {
            getCurrentLocation();
            ShowAddress();
        }

        // TODO: edit journal if j != null

        Log.v("Journal Editor", "journal: " + journal);
//        if (journal != null) { // edit existing journal
//            et_title.setText(journal.getTitle());
//            et_content.setText(journal.getContent());
////            String lat = journal.getLat();
////            String lng = journal.getLng();
////            mLastLocation.setLatitude(Double.valueOf(lat));
////            mLastLocation.setLongitude(Double.valueOf(lng));
////            // set Address
////            startIntentService();
//        }

        // Set the botton click action for location(Map) button
        ib_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getCurrentLocation();
                ShowAddress();
            }
        });

        // click on tags button to save the current location
        ib_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                LayoutInflater factory = LayoutInflater.from(JournalEditorActivity.this);
                view = factory.inflate(R.layout.tag_window, null);
                final EditText edit=(EditText)view.findViewById(R.id.window_tag_et);
                list_Of_Map.clear();
                if(tags != null) {
                    for(int i = 0; i < tags.size(); i++) {
                        String str_size = Integer.toString(i + 1)+".";
                        list_Of_Num.add(str_size);
                    }
                    for (int i = 0; i < tags.size(); i++) {
                        Map<String, Object> listem = new HashMap<String, Object>();
                        listem.put("index", list_Of_Num.get(i));
                        listem.put("tag", tags.get(i));
                        list_Of_Map.add(listem);
                    }
                    simp_adapter = new SimpleAdapter(view.getContext(), list_Of_Map, R.layout.listcontent,
                            new String[]{"index", "tag"}, new int[]{
                            R.id.listcontent_index, R.id.listcontent_content});
                    lv_of_tag = view.findViewById(R.id.lv_tags);
                    lv_of_tag.setAdapter(simp_adapter);
                }
                new AlertDialog.Builder(JournalEditorActivity.this)
                        .setTitle("Tags")     //title
                        .setView(view)
                        .setPositiveButton("Save",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String newtag = edit.getText().toString();
                                        tags.add(newtag);
                                        int size = tags.size();
                                        String str_size = Integer.toString(size)+".";
                                        Log.i("fdsd",str_size);
                                        list_Of_Num.add(str_size);
                                    }
                                }).setNegativeButton("Cancel", null).create().show();
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
                    journal = new Journal(et_title.getText().toString(), tags,current_time_long,
                            currentLatitude+"",currentLongitude+"", et_content.getText().toString());

                    ArrayList<String> photo_string = photo_to_string(photos);
                    journal.setPhoto_string(photo_string);

                    // Save the new journal in the database.
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Users = database.getReference("New_users");
                    Users.child(username).child("journal_list").child(journal.getTitle()).setValue(journal);
                }
                else{
                    // Update the new journal
                    ArrayList<String> photo_string = photo_to_string(photos);
                    journal.setPhoto_string(photo_string);
                    if (tags.size()!=0 && tags!=journal.getTags()){
                        journal.setTags(tags);
                    }
                    String title_new = et_title.getText().toString();
                    String content_new = et_content.getText().toString();
                    journal.setContent(content_new);
                    journal.setTitle(title_new);

                    //save data here:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference Users = database.getReference("New_users");
//                    DatabaseReference AAA = Users.child(username).child("journal_list");
                    Users.child(username).child("journal_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap:dataSnapshot.getChildren()){
                                String key_title = snap.getKey();
                                if (key_title.equals(journal.getTitle())){
                                    Map<String,Object> UP = new HashMap<>();
                                    UP.put("/"+username+"/journal_list/"+key_title,journal);
                                    Users.updateChildren(UP);
                                    finish();
                                    break;
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

    private void getCurrentLocation() {
        if (!checkPermissionsforcoarse()) {
            requestPermissionsforcoarse();
        } else {
            getLastLocation();
        }
    }

    private void ShowAddress() {
        if (!checkPermissionsforfine()) {
            requestPermissionsforfine();
        } else {
            getAddress();
        }
    }

    @SuppressLint("MissingPermission")
    private void getAddress() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }
                        mLastLocation = location;
                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            return;
                        }
                        startIntentService();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    private void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        //Log.e("sss", "before startservice");
        startService(intent);
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
                            Log.v("JournalEditor status", "lat: " + currentLatitude + ", lng: " + currentLongitude);

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.e("address", "get address update");
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();
        }
    }

    private void displayAddressOutput() {
        tv_address.setText(mAddressOutput);
    }

    private boolean checkPermissionsforcoarse() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsforcoarse() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            Log.e(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequestforcoarse();
                        }
                    });

        } else {
            Log.e(TAG, "Requesting permission");
            startLocationPermissionRequestforcoarse();
        }
    }

    private void startLocationPermissionRequestforcoarse() {
        ActivityCompat.requestPermissions(JournalEditorActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private boolean checkPermissionsforfine() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionsforfine() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(JournalEditorActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(JournalEditorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
