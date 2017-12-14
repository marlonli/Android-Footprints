package com.example.jingyuan.footprints;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class SearchResultActivity extends AppCompatActivity {
    private static final int SEARCH_FOR_FRIENDS_REQ = 1;

    private Toolbar myToolbar;
    private String username;
    private String query;
    private ProgressBar spinner;
    private ImageView im;
    private TextView tv;
    private ToggleButton tb;

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
        username = null;

        handleIntent(getIntent());

        searchForFriend();

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // TODO: following
                } else {
                    // TODO: unfollowed
                }
            }
        });

    }

    private void searchForFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO:use the query to search user
                for (long i = 0; i < 100000000; i++) ;


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // hide progress bar, show the result
                        // TODO: show the result
                        im.setImageResource(R.drawable.ic_person_black_24dp);
                        tv.setText("username");
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
