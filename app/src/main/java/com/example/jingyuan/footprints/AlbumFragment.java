package com.example.jingyuan.footprints;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    public String username;

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    LoadAlbum loadAlbumTask;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    private OnFragmentInteractionListener mListener;

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 username.
     * @return A new instance of fragment AlbumFragment.
     */

    public static AlbumFragment newInstance(String param1) {
        AlbumFragment fragment = new AlbumFragment();
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
        View v =inflater.inflate(R.layout.fragment_album, container, false);

        // Initialization
        galleryGridView = (GridView) v.findViewById(R.id.galleryGridView);
        Log.v("album fragment", "galleryGridView: " + galleryGridView);

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getActivity().getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Utilities.dipToPixels(getActivity().getApplicationContext(), dp);
            galleryGridView.setColumnWidth(Math.round(px));
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get permission
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else
                {
                    Toast.makeText(getActivity(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }
    }

    class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

//            String path = null;
//            String album = null;
//            String timestamp = null;
//            String countPhoto = null;
//            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//
//            // Get photos from storage
//            String[] projection = { MediaStore.MediaColumns.DATA,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
//            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
//                    null, null);
//            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
//                    null, null);
//            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
//
//            while (cursor.moveToNext()) {
//
//                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
//                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
//                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
//                countPhoto = Utilities.getCount(getActivity().getApplicationContext(), album);
//                Log.v("time stamp: ", timestamp);
//                albumList.add(Utilities.mappingInbox(album, path, timestamp, Utilities.converToTime(timestamp), countPhoto));
//            }
//            cursor.close();
//            Collections.sort(albumList, new MapComparator(Utilities.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending

            read_image_from_database(new LoadDataCallback() {
                @Override
                public void loadFinish() {
                    AlbumAdapter adapter = new AlbumAdapter(getActivity(), albumList);
                    galleryGridView.setAdapter(adapter);
                    galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            Intent intent = new Intent(getActivity(), AlbumActivity.class);
                            intent.putExtra("name", albumList.get(+position).get(Utilities.KEY_ALBUM));
                            intent.putExtra("username",username);
                            startActivity(intent);
                        }
                    });
                }
            });




            return xml;
        }

        public void read_image_from_database(final LoadDataCallback callback){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference Users = database.getReference("New_users");
            DatabaseReference aaa = Users.child(username);
            DatabaseReference bbb = aaa.child("journal_list");
            bbb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap:dataSnapshot.getChildren()){
                        String key = snap.getKey();
                        ArrayList<String> photo_string = (ArrayList<String>) snap.child("photoString").getValue();
                        Utilities u = new Utilities();
                        if (photo_string!=null){
                            String photo_string_one = photo_string.get(0);
                            HashMap<String,String> album_one = u.set_value(key,photo_string_one,""+photo_string.size());
//                            HashMap<String,String> album_one = new HashMap<>();
//                            album_one.put(key,photo_string_one);
                            albumList.add(album_one);
                        }
                        else{
                            HashMap<String,String> album_one = u.set_value(key,null,"0");
                            albumList.add(album_one);
                        }
                    }
                    callback.loadFinish();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public ArrayList<Bitmap> photo_bit_to_string(ArrayList<String> photo_string){
            ArrayList<Bitmap> photo_bit = new ArrayList<Bitmap>();
            for (int i=0;i<photo_string.size();i++){
                String photo_string_tmp = photo_string.get(i);
                byte[] decodeByte = Base64.decode(photo_string_tmp,0);
                Bitmap photo_bit_tmp = BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.length);
                photo_bit.add(photo_bit_tmp);
            }
            return photo_bit;
        }

        @Override
        protected void onPostExecute(String xml) {


        }
    }

}

class AlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public AlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

            convertView.setTag(holder);
        } else {
            holder = (AlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            holder.gallery_title.setText(song.get(Utilities.KEY_ALBUM));
            holder.gallery_count.setText(song.get(Utilities.KEY_COUNT)+" Photos");
            String image_string = song.get(Utilities.KEY_BYTE);
            byte[] image_byte = Base64.decode(image_string,Base64.DEFAULT);
            Glide.with(activity)
                    .load(image_byte)
                    .into(holder.galleryImage);


        } catch (Exception e) {}
        return convertView;
    }
}


class AlbumViewHolder {
    ImageView galleryImage;
    TextView gallery_count, gallery_title;
}
