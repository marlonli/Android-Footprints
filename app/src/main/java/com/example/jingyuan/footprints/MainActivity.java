package com.example.jingyuan.footprints;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
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
    private FloatingActionButton fab;
    private String username;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    fab.show();
                    getSupportActionBar().show();
                    showJournalList(username);
                    return true;
                }
                case R.id.navigation_album: {
                    fab.hide();
                    getSupportActionBar().hide();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, AlbumFragment.newInstance(username), "Album").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_maps:{
                    fab.hide();
                    getSupportActionBar().hide();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, MapsFragment.newInstance(username), "Maps").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_people:
                    fab.hide();
                    getSupportActionBar().show();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, FriendsFragment.newInstance(username), "People").addToBackStack(null).commit();
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

        fab = (FloatingActionButton) findViewById(R.id.fab_newjournal);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        // Set journal list fragment
        showJournalList(username);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void showJournalList(String username) {
        String para1 = username;
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, JournalFragment.newInstance(para1), "Journal").addToBackStack(null).commit();
    }

    public void getPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

        }
    }
}
