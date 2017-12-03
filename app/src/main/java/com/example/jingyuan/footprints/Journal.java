package com.example.jingyuan.footprints;

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
    private long _id;
    // Sat Dec 02 19:19:45 EST 2017
    Date dateTime;
    private String location;
    private String content;

    public Journal(String title, ArrayList<String> tags, Date currentTime, String location, String content) {
        this.title = title;
        this.tags = tags;
        this.location = location;
        this.content = content;
        this.dateTime = currentTime;

    }

    public long get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
