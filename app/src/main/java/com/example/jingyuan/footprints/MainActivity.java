package com.example.jingyuan.footprints;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements AlbumFragment.OnFragmentInteractionListener, JournalFragment.OnFragmentInteractionListener, MapsFragment.OnFragmentInteractionListener, FriendsFragment.OnFragmentInteractionListener{

    private static final int NEW_JOURNAL = -1;
    Toolbar myToolbar;
    private BottomNavigationView navigation;

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
                    String para1 = "para1";
                    String para2 = "para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, AlbumFragment.newInstance(para1, para2), "Album").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_maps:{
                    String para1 = "para1";
                    String para2 = "para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, MapsFragment.newInstance(para1, para2), "Maps").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_people:
                    String para1 = "para1";
                    String para2 = "para2";
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, PeopleFragment.newInstance(para1, para2), "People").addToBackStack(null).commit();
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
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

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

    public void getPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

        }
    }
}
