package com.but_app.but.util;


import com.but_app.but.to.Quote;
import com.but_app.but.to.Review;

import java.util.ArrayList;

/**
 * Created by hrss on 10/4/15.
 */
public class ReviewMap {
    public ReviewMap(Review review, ArrayList<Quote> badQuotes, ArrayList<Quote> goodQuotes){
        this.review = review;
        this.badQuotes = badQuotes;
        this.goodQuotes = goodQuotes;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public ArrayList<Quote> getGoodQuotes() {
        return goodQuotes;
    }

    public void setGoodQuotes(ArrayList<Quote> goodQuotes) {
        this.goodQuotes = goodQuotes;
    }

    public ArrayList<Quote> getBadQuotes() {
        return badQuotes;
    }

    public void setBadQuotes(ArrayList<Quote> badQuotes) {
        this.badQuotes = badQuotes;
    }

    private Review review;
    private ArrayList<Quote> goodQuotes;
    private ArrayList<Quote> badQuotes;
}