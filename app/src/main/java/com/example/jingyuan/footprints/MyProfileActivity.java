package com.example.jingyuan.footprints;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    private User user;
    private ImageButton ib;
    private TextView myUsername;
    private TextView journalNum;
    private TextView friendsNum;
    private Toolbar myToolbar;
    private TextView editButton;
    private PopupWindow popupWindow;
    private ArrayList<Journal> journals;
    private ListView listView;
    private MyJournalViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialization
        ib = (ImageButton) findViewById(R.id.imageButton_profile);
        myUsername = (TextView) findViewById(R.id.textView_username);
        journalNum = (TextView) findViewById(R.id.textView_num_journals);
        friendsNum = (TextView) findViewById(R.id.textView_num_friends);
        editButton = (TextView) findViewById(R.id.textView_edit);
        listView = (ListView) findViewById(R.id.listView_journals);
        popupWindow = null;
        journals = new ArrayList<>();

        addTestData();

        // Set toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
//            myUsername.setEnabled(false);
            editButton.setText("");
            ib.setClickable(false);

            // Set listView
            mAdapter = new MyJournalViewAdapter(this, journals);
            listView.setAdapter(mAdapter);

        } else {
            editButton.setText("Edit");
        }
        Log.v("MyProfile status", "position: " + position);



        // imageButton on click
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: open photo lib or camera
            }
        });

        // Edit username
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(MyProfileActivity.this);
                view = factory.inflate(R.layout.popup_edit_username, null);
                final EditText newName = (EditText) view.findViewById(R.id.editText_username);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Edit Username")     //title
                        .setView(view)
                        .setPositiveButton("Confirm",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        myUsername.setText(newName.getText().toString());
                                    }
                                }).setNegativeButton("Cancel", null).create().show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: view journal
                openEditor(i);
            }
        });

    }

    public void openEditor(int journalIndex) {
        Intent intent = new Intent(this, JournalEditorActivity.class);
        intent.putExtra(EDITOR_MODE, READ);
        intent.putExtra(JOURNAL_OBJECT, journals.get(journalIndex));
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
        journals.add(new Journal("Journal1", testTags, currentTime, "30", "120", getString(R.string.large_text)));
        journals.add(new Journal("Journal title", testTags, currentTime - 86400000, "30", "-120", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journals.add(new Journal("OMG OMG", testTags, startDate1, "40", "-74", "Often you will want one Fragment to communicate with another, for example to change the content based on a user event. All Fragment-to-Fragment communication is done through the associated Activity. Two Fragments should never communicate directly."));
        journals.add(new Journal("Journal1", testTags, startDate2, "40", "-110", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));
        journals.add(new Journal("Journal1", testTags, currentTime, "30", "-70", "In order to reuse the Fragment UI components, you should build each as a completely self-contained, modular component that defines its own layout and behavior. Once you have defined these reusable Fragments, you can associate them with an Activity and connect them with the application logic to realize the overall composite UI."));

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
