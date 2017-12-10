package com.example.jingyuan.footprints;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jingyuan on 12/9/17.
 */

public class User implements Serializable{
    long _id;
    String username;
    String password;
    List<User> myFriends;
    List<Journal> myJournals;
    Bitmap profile;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<User> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(List<User> myFriends) {
        this.myFriends = myFriends;
    }

    public List<Journal> getMyJournals() {
        return myJournals;
    }

    public void setMyJournals(List<Journal> myJournals) {
        this.myJournals = myJournals;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }
}
