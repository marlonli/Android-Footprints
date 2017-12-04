package com.example.jingyuan.footprints;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements AlbumFragment.OnFragmentInteractionListener, JournalFragment.OnFragmentInteractionListener{

    private static final int NEW_JOURNAL = -1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    showJournalList();
                    return true;
                }
                case R.id.navigation_album: {
                    String para1 = "album_para1";
                    String para2 = "album_para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, AlbumFragment.newInstance(para1, para2), "Album").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_maps:
//                    mTextMessage.setText(R.string.title_maps);
                    return true;
                case R.id.navigation_people:
//                    mTextMessage.setText(R.string.title_people);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar and navigation bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Set journal list fragment
        showJournalList();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void showJournalList() {
        String para1 = "journal_para1";
        String para2 = "journal_para2";
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, JournalFragment.newInstance(para1, para2), "Journal").addToBackStack(null).commit();
    }

//    public static int dp2px(int dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getContext().getResources().getDisplayMetrics());
//    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
