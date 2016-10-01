package com.but_app.but.to;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by hrss on 12/19/15.
 */
public class ReviewCard {
    private String userName;
    private ParseUser user;
    private ArrayList<Quote> goodQuotes;
    private ArrayList<Quote> badQuotes;
    private String reviewId;
    private int goodPosition = 0;
    private int badPosition = 0;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public ArrayList<Quote> getBadQuotes() {
        return badQuotes;
    }

    public void setBadQuotes(ArrayList<Quote> badQuotes) {
        this.badQuotes = badQuotes;
    }

    public ArrayList<Quote> getGoodQuotes() {
        return goodQuotes;
    }

    public void setGoodQuotes(ArrayList<Quote> goodQuotes) {
        this.goodQuotes = goodQuotes;
    }

    public int getBadPosition() {
        return badPosition;
    }

    public void setBadPosition(int badPosition) {
        this.badPosition = badPosition;
    }

    public int getGoodPosition() {
        return goodPosition;
    }

    public void setGoodPosition(int goodPosition) {
        this.goodPosition = goodPosition;
    }

}
