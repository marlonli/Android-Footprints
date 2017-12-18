package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

public class AlbumActivity extends AppCompatActivity {

    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> image_list = new ArrayList<>();
    public String album_name = "";
    LoadAlbumImages loadAlbumTask;
    public String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        username = intent.getStringExtra("username");
        setTitle(album_name);


        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Utilities.dipToPixels(getApplicationContext(), dp);
            galleryGridView.setColumnWidth(Math.round(px));
        }


        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }

    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

//            String path = null;
//            String album = null;
//            String timestamp = null;
//            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//
//            String[] projection = { MediaStore.MediaColumns.DATA,
//                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
//
//            Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
//            Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
//            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
//            while (cursor.moveToNext()) {
//
//                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
//                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
//                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
//
//                imageList.add(Utilities.mappingInbox(album, path, timestamp, Utilities.converToTime(timestamp), null));
//            }
//            cursor.close();
//            Collections.sort(imageList, new MapComparator(Utilities.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending

            read_img_from_data(new LoadDataCallback() {
                @Override
                public void loadFinish() {
                    if (image_list == null) {
                        Toast.makeText(getApplicationContext(), "No photos in this ablum.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SingleAlbumAdapter adapter = new SingleAlbumAdapter(AlbumActivity.this, image_list);
                        galleryGridView.setAdapter(adapter);
                        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    final int position, long id) {

                                Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
                                intent.putExtra("image_name", image_list.get(+position));
                                startActivity(intent);
                            }
                        });
                    }
                }
            });


            return xml;
        }

        public void read_img_from_data(final LoadDataCallback callback){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference Users = database.getReference("New_users");
            DatabaseReference aaa = Users.child(username);
            DatabaseReference bbb = aaa.child("journal_list");
            bbb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap:dataSnapshot.getChildren()){
                        String key = snap.getKey();
                        if(key.equals(album_name)) {
                            ArrayList<String> photo_string = (ArrayList<String>) snap.child("photoString").getValue();
                            if (photo_string != null) {
                                image_list = photo_string;
                            } else {
                                image_list = null;
                            }
                            break;
                        }
                    }
                    callback.loadFinish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        protected void onPostExecute(String xml) {


        }
    }
}

class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String > data;
    public SingleAlbumAdapter(Activity a, ArrayList <String> d) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        String song = "";
        try {
            if (data!=null) {
                song = data.get(position);
            }
            byte[] image_byte = Base64.decode(song,Base64.DEFAULT);
            Glide.with(activity)
                    .asBitmap()
                    .load(image_byte) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception e) {}
        return convertView;
    }
}


class SingleAlbumViewHolder {
    ImageView galleryImage;
}
