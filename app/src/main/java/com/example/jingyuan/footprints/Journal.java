package com.example.jingyuan.footprints;

import java.util.ArrayList;

/**
 * Created by jingyuan on 11/30/17.
 */

class Journal {
    private String title;
    private ArrayList<String> tags;
    private int month;
    private int date;
    private String location;
    private String content;

    public Journal(String title, ArrayList<String> tags, int month, int date, String location, String content) {
        this.title = title;
        this.tags = tags;
        this.month = month;
        this.date = date;
        this.location = location;
        this.content = content;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
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
