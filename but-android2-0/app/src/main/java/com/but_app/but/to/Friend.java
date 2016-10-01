package com.but_app.but.to;

/**
 * Created by Hernando on 08/17/2015.
 */

import android.graphics.drawable.Drawable;

import java.io.Serializable;


public class Friend implements Serializable, Comparable<Friend>{

    private String facebookId;
    private String  name;
    private String imageUrl;
    private Drawable profilePic;

    public Friend(String facebookId, String name, String imageUrl){
        this.facebookId = facebookId;
        this.name = name;
        this.imageUrl= imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Drawable getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Drawable profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int compareTo(Friend friend){
        return this.getName().compareTo(friend.getName());
    }
}
