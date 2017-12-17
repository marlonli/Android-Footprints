package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jingyuan on 12/6/17.
 */

public class Utilities {

    static final String KEY_ALBUM = "album_name";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "date";
    static final String KEY_COUNT = "date";
    static final String KEY_BYTE = "image_byte";

    public static HashMap<String, String> set_value(String album, String image_byte,String count){
        HashMap<String, String>map = new HashMap<String, String>();
        map.put(KEY_ALBUM,album);
        map.put(KEY_BYTE,image_byte);
        map.put(KEY_COUNT,count);
        return map;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static HashMap<String, String> mappingInbox(String album, String path, String timestamp, String time, String count)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);
        map.put(KEY_COUNT, count);
        return map;
    }

    public static String converToTime(String timestamp)
    {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        return formatter.format(date);
    }

    public static String getCount(Context c, String album_name)
    {
        Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});


        return cursor.getCount()+" Photos";
    }

    public static String latLngToLoc(Activity activity, String lat, String lng) {
        String loc = null;
        if (lat == null || lng == null)
            return loc;

        double myLat = Double.valueOf(lat);
        double myLng = Double.valueOf(lng);
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addresses = new ArrayList<>();
        // Get and show address
        try {

            addresses.addAll(geocoder.getFromLocation(myLat, myLng, 1));

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (addresses!= null && addresses.size() != 0) {
            Address address = addresses.get(0);
            StringBuilder sb = new StringBuilder();
            if (address.getSubThoroughfare() != null) sb.append(address.getSubThoroughfare()).append(", ");
            if (address.getThoroughfare() != null) sb.append(address.getThoroughfare()).append(", ");
            if (address.getLocality() != null) sb.append(address.getLocality()).append(", ");
            if (address.getAdminArea() != null) sb.append(address.getAdminArea()).append(" ");
            if (address.getPostalCode() != null) sb.append(address.getPostalCode());
            loc = sb.toString();
        }
        return loc;
    }

}
