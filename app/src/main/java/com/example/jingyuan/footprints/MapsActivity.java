package com.example.jingyuan.footprints;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        setCurrentLocationCenter(mMap);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        showAllTags();
    }

    private void setCurrentLocationCenter(GoogleMap googleMap) {
        Intent intent = getIntent();
        Double latitude = intent.getDoubleExtra("latitude", 0.0);
        Double longitude = intent.getDoubleExtra("longitude", 0.0);
        LatLng current_location = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
    }

    private void showAllTags() {

        //All Tags should be searched in db

//        Cursor cursor = dbread.rawQuery("SELECT location.id, location.name, location.latitude, location.longitude " +
//                "FROM location ", null);

//        while(cursor.moveToNext()) {
//            double recordedlatitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
//            double recordedlongitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
//
//            LatLng marker_Location = new LatLng(recordedlatitude, recordedlongitude);
//            Marker marker = mMap.addMarker(new MarkerOptions().position(marker_Location);
//        }
    }
}
