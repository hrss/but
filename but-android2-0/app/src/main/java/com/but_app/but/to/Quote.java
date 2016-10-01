package com.but_app.but.to;

/**
 * Created by Hernando on 05/01/2015.
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Quote")
public class Quote extends ParseObject{
    private Boolean checked;
    private String inQuote;

    public Quote(String inQuote){
        this.inQuote = inQuote;
    }

    public Quote(){
        inQuote = null;
    }

    public String getInQuote(){
        return inQuote;
    }

    public void setInQuote(String inQuote){
        this.inQuote = inQuote;
    }

    public String getId() {
        return getObjectId();
    }

    public void setPosition(Integer position) {
        put("position", position);
    }

    public Integer getPosition() {
        return (Integer) getNumber("position");
    }

    public void setQuote(String quote) {
        put("quote", quote);
    }

    public String getQuote(){
        return getString("quote");
    }

    public static ParseQuery<Quote> getQuery(){
        return ParseQuery.getQuery("Quote");
    }

    public Boolean getGoodOrBad(){ return getBoolean("goodOrBad");}

    public Boolean isChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
