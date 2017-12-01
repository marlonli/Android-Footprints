package com.example.jingyuan.footprints;

import java.util.ArrayList;

/**
 * Created by jingyuan on 11/30/17.
 */

class Journal {
    private String title;
    private ArrayList<String> tags;
    private String month;
    private String date;
    private String location;
    private String content;

    public Journal(String title, ArrayList<String> tags, int month, int date, String location, String content) {
        this.title = title;
        this.tags = tags;
        this.location = location;
        this.content = content;

        // Set month and date
        switch (month) {
            case 1:
                this.month = "Jan.";
                break;
            case 2:
                this.month = "Feb.";
                break;
            case 3:
                this.month = "Mar.";
                break;
            case 4:
                this.month = "Apr.";
                break;
            case 5:
                this.month = "May";
                break;
            case 6:
                this.month = "June";
                break;
            case 7:
                this.month = "July";
                break;
            case 8:
                this.month = "Aug.";
                break;
            case 9:
                this.month = "Sep.";
                break;
            case 10:
                this.month = "Oct.";
                break;
            case 11:
                this.month = "Nov.";
                break;
            case 12:
                this.month = "Dec.";
                break;
            default:
                break;
        }

        if (date < 10) {
            this.date = "0" + date;
        }
        else {
            this.date = String.valueOf(date);
        }
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

    public String getMonth() {
        return month;
    }

//    public void setMonth(int month) {
//        this.month = month;
//    }

    public String getDate() {
        return date;
    }

//    public void setDate(int date) {
//        this.date = date;
//    }

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
