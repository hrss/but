package com.but_app.but.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.but_app.but.to.Friend;
import com.but_app.but.to.Quote;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by hrss on 8/23/15.
 */
public class ConfigHelper {
    private ParseConfig config;
    private long configLastFetchedTime;
    private long quotesLastFetchedTime;
    private HashMap<String, Quote> goodQuotes;
    private HashMap<String, Quote> badQuotes;
    private BitmapDrawable background;
    private ArrayList<Friend> friends;
    private HashMap<String, ReviewMap> reviews;
    private Friend currentUser;
    private ImageLoader imgLoader;

    public void fetchQuotesIfNeeded(){
        final long configRefreshInterval = 60 * 60 * 1000;
        if (goodQuotes == null || goodQuotes.isEmpty() || badQuotes == null || badQuotes.isEmpty() ||
                System.currentTimeMillis() - quotesLastFetchedTime > configRefreshInterval) {
            doQuotesQuery(true);
            doQuotesQuery(false);
            quotesLastFetchedTime = System.currentTimeMillis();
        }
    }

    private void doQuotesQuery(final Boolean goodOrBad) {
        Locale ptBr = new Locale("pt","BR");
        ParseQuery<Quote> quotesQuery = Quote.getQuery();
        quotesQuery.whereEqualTo("goodOrBad", goodOrBad);
        quotesQuery.whereEqualTo("live", true);
        if (Locale.getDefault().equals(ptBr)) {
            quotesQuery.whereEqualTo("locale", "pt-BR");
        } else {
            quotesQuery.whereEqualTo("locale", "en-US");
        }
        quotesQuery.findInBackground(new FindCallback<Quote>() {
            public void done(List<Quote> l, ParseException e) {
                if (e == null){
                   if(goodOrBad){
                       goodQuotes = new HashMap<String, Quote>();
                       for (Quote quote : l){
                           goodQuotes.put(quote.getId(), quote);
                       }
                   } else {
                       badQuotes = new HashMap<String, Quote>();
                       for (Quote quote : l){
                           badQuotes.put(quote.getId(), quote);
                       }
                   }
                }
            }
        });

    }

    /**
     * Get current user info.
     * @return Friend containing the current user's info.
     */
    public Friend getUserInfo(final ImageView v, final Context ctx){
        if(currentUser == null) {
            Bundle params = new Bundle();
            params.putString("fields", "id, name, picture");

            GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                    FacebookRequestError error = response.getError();
                    if (error == null) {
                        try {
                            String name = object.getString("name");
                            JSONObject picObject = object.getJSONObject("picture");
                            String fbId = object.getString("id");
                            String picUrl = picObject.getJSONObject("data").getString("url");
                            Friend pictureByName = new Friend(fbId, name, picUrl);
                            currentUser = pictureByName;
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    }
                    });
            request.setParameters(params);
            request.executeAsync();
        }
        return currentUser;
    }

    public HashMap<String, Quote> getGoodQuotes(){
        return new HashMap<>(goodQuotes);
    }

    public HashMap<String, Quote> getBadQuotes(){
        return new HashMap<>(badQuotes);
    }

    public HashMap<String, ReviewMap> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, ReviewMap> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public void fetchConfigIfNeeded() {
        // 1
        final long configRefreshInterval = 60 * 60;
        if (config == null ||
                System.currentTimeMillis() - configLastFetchedTime > configRefreshInterval) {
            // 2
            config = ParseConfig.getCurrentConfig();
            // 3
            ParseConfig.getInBackground(new ConfigCallback() {
                @Override
                public void done(ParseConfig parseConfig, ParseException e) {
                    if (e == null) {
                        // 4
                        config = parseConfig;
                        configLastFetchedTime = System.currentTimeMillis();
                    } else {
                        // 5
                        configLastFetchedTime = 0;
                    }
                }
            });
        }
    }

    public BitmapDrawable getBackground() {
        return background;
    }

    public void setBackground(BitmapDrawable background) {
        this.background = background;
    }


}
