package com.but_app.but.to;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by iGor Montella on 07/10/2014.
 */

@ParseClassName("User")

public class User extends ParseUser {

    public Long id;
    public Long creationDate;
    public Long modificationDate;
    public Boolean active;
    public Boolean removed;
    public String name;
    public String faceId;
    public String email;
    public String password;
    public Long likes;
    public Long dislikes;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Boolean isActive() {
        return active && !isRemoved();
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }




}
