package com.example.jingyuan.footprints;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jingyuan on 11/30/17.
 */

class Journal implements Serializable {
    private String title;
    private ArrayList<String> tags;
    private ArrayList<Bitmap> photos;
    private long _id;
    private long dateTime;
    private String lat;
    private String lng;
    private String address;
    private String content;
    private ArrayList<String> photo_string;

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }

    public void addPhoto(Bitmap photo) {
        this.photos.add(photo);
    }

    public Journal(String title, ArrayList<String> tags, long currentTime, String lat, String lng, String content) {
        this.title = title;
        this.tags = tags;
        this.lat = lat;
        this.lng = lng;
        this.content = content;
        this.dateTime = currentTime;
        this.photos = new ArrayList<>();

    }

    public Journal(String title, long currentTime, String lat, String lng) {
        this.title = title;
        this.tags = new ArrayList<>();
        this.lat = lat;
        this.lng = lng;
        this.content = "";
        this.dateTime = currentTime;
        this.photos = new ArrayList<>();

    }

    public long get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getDateTimeLong() {
        return dateTime;
    }

    // Sat Dec 02 19:19:45 EST 2017
    public String getDateTimeString() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(dateTime);
        Date dt = date.getTime();
        return dt.toString();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getPhotoString(){
        return this.photo_string;
    }

    public void setPhoto_string(ArrayList<String> photo_string){
        this.photo_string = photo_string;
    }
}
