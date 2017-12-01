package com.example.jingyuan.footprints;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.jingyuan.footprints.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements AlbumFragment.OnFragmentInteractionListener, JournalFragment.OnFragmentInteractionListener{

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    String para1 = "journal_para1";
                    String para2 = "journal_para2";
                    getFragmentManager().beginTransaction().replace(R.id.content, JournalFragment.newInstance(para1, para2), "Album").addToBackStack(null).commit();
                    return true;
                }
                case R.id.navigation_album: {
                    String para1 = "album_para1";
                    String para2 = "album_para2";
                    getFragmentManager().beginTransaction().replace(R.id.content, AlbumFragment.newInstance(para1, para2), "Album").addToBackStack(null).commit();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
