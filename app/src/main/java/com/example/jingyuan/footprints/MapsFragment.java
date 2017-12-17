package com.example.jingyuan.footprints;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wyh.slideAdapter.SlideAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int LOC_PERMISSION_REQUEST_CODE = 10;

    private String username;
    private ArrayList<String> markers;

    private OnFragmentInteractionListener mListener;

    MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;


    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 username.
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance(String param1) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        markers = new ArrayList<>();

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read data
        read_data_from_database(new LoadDataCallback() {
            @Override
            public void loadFinish() {
                Log.v("MapsFragment status", "loadFinish!");
            }
        });


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale( getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(getActivity(), "Permission denied! Please check settings.", Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions( getActivity(), new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                    // Center on my location
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        String lp = LocationManager.GPS_PROVIDER;
                        Location loc = null;
                        loc = locationManager.getLastKnownLocation(lp);
                        if (loc == null)
                            lp = LocationManager.NETWORK_PROVIDER;
                        loc = locationManager.getLastKnownLocation(lp);
                        if (loc != null) {
                            LatLng myLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }

                    setMarkers();
                }

                // For dropping a marker at a point on the Map
//                LatLng sydney = new LatLng(-34, 151);
//                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

        return v;
    }

    private void setMarkers() {
        if (markers.size() != 0)
            for (String latlng : markers) {
                String[] coordination = latlng.split(",");
                LatLng pnt = new LatLng(Double.valueOf(coordination[0]), Double.valueOf(coordination[1]));
                Marker mPoint = googleMap.addMarker(new MarkerOptions()
                        .position(pnt)
//                        .title(j.getTitle())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker)));
//                mPoint.setTag(0);
            }
    }

    private void read_data_from_database(final LoadDataCallback callback){
        Log.v("Username: ", "" + username);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference Users = database.getReference("New_users");
        DatabaseReference aaa = Users.child(username);
        DatabaseReference bbb = aaa.child("journal_list");
        bbb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                markers.clear();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    String key = snap.getKey();
                    String lat = (String) snap.child("lat").getValue();
                    String lng = (String) snap.child("lng").getValue();
                    markers.add(lat + "," + lng);
                }
                callback.loadFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            // remove listener if needed
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
