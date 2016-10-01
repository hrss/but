package com.but_app.but.to;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hrss on 8/23/15.
 */
@ParseClassName("Review")
public class Review extends ParseObject implements Serializable{
    public String getId() {
        return getObjectId();
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        ParseRelation<ParseObject> relation = getRelation("quotes");
        for(Quote quote : quotes)
            relation.add(quote);
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public ParseUser getUser(){
        return (ParseUser) get("user");
    }

    public ParseUser getReviewOwner(){
        return (ParseUser) get("reviewOwner");
    }

    public Integer getIndex(){
      return (Integer) getNumber("index");
    }

    public void setIndex(Long index){
        put("index", index);
    }

    public static ParseQuery<Review> getQuery(){
        return ParseQuery.getQuery(Review.class);
    }
}
