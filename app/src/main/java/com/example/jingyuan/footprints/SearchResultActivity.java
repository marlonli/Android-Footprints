package com.example.jingyuan.footprints;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {
    private static final int SEARCH_FOR_FRIENDS_REQ = 1;
    private static final String DBName = "New_users";

    private Toolbar myToolbar;
    private String username;
    private String query;
    private ArrayList<String> friendsName;
    private ProgressBar spinner;
    private ImageView im;
    private TextView tv;
    private ToggleButton tb;
    private User user;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //Initialization
        im = (ImageView) findViewById(R.id.friends_profile);
        tv = (TextView) findViewById(R.id.friends_username);
        tb = (ToggleButton) findViewById(R.id.toggleButton_follow);

        // Set toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        // set invisible before searching
        tb.setVisibility(View.GONE);
        //username = null;
        Intent intent = getIntent();
        username = null;
        friendsName = new ArrayList<>();

        handleIntent(getIntent());

        user = new User("");
        search(new LoadDataCallback() {
            @Override
            public void loadFinish(){
                if(user.getUsername().equals("")){
                    im.setImageResource(R.drawable.ic_person_black_24dp);
                    tv.setText("No such user");
                    tb.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                }
                else{
                    if(user.getProfileByteArray() != null) {
                        Bitmap myProfile = BitmapFactory.decodeByteArray(user.getProfileByteArray(), 0, user.getProfileByteArray().length);
                        im.setImageBitmap(myProfile);
                    }
                    else
                        im.setImageResource(R.drawable.ic_person_black_24dp);
                    tv.setText(user.getUsername());
                    tb.setVisibility(View.VISIBLE);
                    if(friendsName.contains(user.getUsername())){
                        tb.setChecked(true);
                    }
                    spinner.setVisibility(View.GONE);
                }
            }
        });
        //searchForFriend();

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //get reference of current user
            DatabaseReference curUserRef = FirebaseDatabase.getInstance().getReference(DBName).child(username);
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // TODO: following
                    if(!friendsName.contains(user.getUsername())) {
                        DatabaseReference newFriend = curUserRef.child("friends").push();
                        //newFriend.setValue(query);      // add friend's name to friends list
                        String imageEncoded = Base64.encodeToString(user.getProfileByteArray(), Base64.DEFAULT);    // add profile to friends list
                        //newFriend.setValue(imageEncoded);
                        ArrayList<String> nf = new ArrayList<>();
                        nf.add(query);
                        nf.add(imageEncoded);
                        newFriend.setValue(nf);
                        friendsName.add(user.getUsername());
                    }
                } else {
                    // TODO: unfollowed
                    if(friendsName.contains(user.getUsername())) {
                        final DatabaseReference myFriends = curUserRef.child("friends");
                        myFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(user.getUsername().equals((String)snapshot.getValue())){
                                        myFriends.child(snapshot.getKey()).removeValue();
                                        friendsName.remove(user.getUsername());
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });

    }

    private void search(final LoadDataCallback callback) {
        dbRef = FirebaseDatabase.getInstance().getReference(DBName);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    String curName = (String)userSnap.child("username").getValue();
                    if(curName.equals(query)) {
                        user.setUsername(curName);
                        if(userSnap.child("profile").getValue() != null) {
                            String encodedProfile = (String) userSnap.child("profile").getValue();
                            byte[] decodedByteArray = Base64.decode(encodedProfile, Base64.DEFAULT);
                            user.setProfileByteArray(decodedByteArray);
                        }
                        break;
                    }
                }

                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchForFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO:search for friend
//                for (long i = 0; i < 100000000; i++) ;


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // hide progress bar, show the result
                        // TODO: show the result
                        if(user.getUsername().equals("")) {
                            // TODO: show corresponding info if there is no such user
                            tv.setText("No such user");
                            tb.setVisibility(View.GONE);
                        }
                        Bitmap myProfile = BitmapFactory.decodeByteArray(user.getProfileByteArray(), 0, user.getProfileByteArray().length);
                        im.setImageBitmap(myProfile);
                        tv.setText(user.getUsername());
//                        im.setImageResource(R.drawable.ic_person_black_24dp);
//                        tv.setText("username");
                        tb.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                        Log.v("search status", "spinner gone");
                    }
                });

            }
        }).start();

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        handleIntent(intent);
//    }

    private void handleIntent(Intent intent) {

        query = intent.getStringExtra(SearchManager.QUERY);
        username = intent.getStringExtra("username");
        friendsName = intent.getStringArrayListExtra("friends");
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (keyCode == KeyEvent.KEYCODE_BACK ) {
//
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra(PERSON_POSITION, position);
//            if (position == 0) {
//                // Set activity result on back.
//                String username = myUsername.getText().toString();
////                ib.setDrawingCacheEnabled(true);
////                Bitmap profile =  ib.getDrawingCache();
//                user.setUsername(username);
////                user.setProfile(profile);
//
//                intent.putExtra(PERSON_OBJECT, user);
//            }
//            Log.v("search result status", "Result sent");
//            setResult(SEARCH_FOR_FRIENDS_REQ, intent);
//            finish();
//
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

}
