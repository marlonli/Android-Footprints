package com.example.jingyuan.footprints;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyProfileActivity extends AppCompatActivity {

    private static final String MY_USERNAME = "username";
    private static final String MY_PROFILE = "profile";
    private static final String PERSON_OBJECT = "person";
    private static final String PERSON_POSITION = "position";
    private static final int OPEN_PROFILE_REQ = 2;
    private int position;

    private User user;
    private ImageButton ib;
    private EditText myUsername;
    private TextView journalNum;
    private TextView friendsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialization
        ib = (ImageButton) findViewById(R.id.imageButton_profile);
        myUsername = (EditText) findViewById(R.id.editText_username);
        journalNum = (TextView) findViewById(R.id.textView_num_journals);
        friendsNum = (TextView) findViewById(R.id.textView_num_friends);

        // Set view
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(PERSON_OBJECT);
        if (user.getProfile() != null)
            ib.setImageBitmap(user.getProfile());
        myUsername.setText(user.getUsername());
        if (user.getMyJournals() != null) {
            int size = user.getMyJournals().size();
            journalNum.setText("" + size);
        }

        if (user.getMyFriends() != null) {
            int size = user.getMyJournals().size();
            friendsNum.setText("" + size);
        }

        // If not my profile
        position = intent.getIntExtra(PERSON_POSITION, 0);
        if (position != 0) {
            myUsername.setEnabled(false);
            ib.setClickable(false);
        }

        // imageButton on click
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: open photo lib or camera
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // Set activity result on back.
            String username = myUsername.getText().toString();
            ib.setDrawingCacheEnabled(true);
            Bitmap profile =  ib.getDrawingCache();
            user.setUsername(username);
            user.setProfile(profile);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PERSON_OBJECT, user);
            intent.putExtra(PERSON_POSITION, position);
            setResult(OPEN_PROFILE_REQ, intent);
            finish();
            Log.v("MyProfile status", "Result sent");

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
