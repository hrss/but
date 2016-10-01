package com.but_app.but.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.but_app.but.R;
import com.but_app.but.Fragment.ReviewListFragment;
import com.but_app.but.to.Review;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private Boolean isConnected;
    private ConnectivityManager connectivityManager;
    private ReviewListFragment reviewListFragment;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String sharedReview;
    private String fbId;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setLogo(R.drawable.ic_launcher);

            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSuccess(Sharer.Result result) {

                    ParseQuery<Review> reviewsQuery = Review.getQuery();
                    reviewsQuery.orderByDescending("createdAt");
                    reviewsQuery.whereEqualTo("objectId", sharedReview);

                    reviewsQuery.findInBackground(new FindCallback<Review>() {
                        @Override
                        public void done(final List<Review> objects, ParseException e) {
                            if(e == null) {
                                for (final Review review : objects) {
                                    ParseACL acl = new ParseACL();
                                    acl.setPublicWriteAccess(false);
                                    acl.setPublicReadAccess(true);
                                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);

                                    review.setACL(acl);
                                    review.saveInBackground();

                                    final HashMap<String, Object> params = new HashMap<String, Object>();
                                    params.put("review", (String)review.getId());

                                    try {
                                        ParseCloud.callFunction("publishReview", params);
                                    } catch (ParseException e1){
                                        e1.printStackTrace();
                                    }
                                }

                            } else {
                                //NO INTERNET CONNECTION
                            }
                        }
                    });
                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            fbId = (String) ParseUser.getCurrentUser().get("facebookId");
            id =  ParseUser.getCurrentUser().getObjectId();
            name = (String) ParseUser.getCurrentUser().get("realName");

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            reviewListFragment = new ReviewListFragment();


            fragmentTransaction.add(R.id.main_fragment, reviewListFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            setContentView(R.layout.fragment_no_internet_connection);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setLogo(R.drawable.ic_launcher);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_about){
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_app_invites){
            String appLinkUrl, previewImageUrl;

            appLinkUrl = "https://fb.me/1739417306318521";
            previewImageUrl = "https://but-app.parseapp.com/images/featurette.png";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(this, content);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected) {
            setContentView(R.layout.fragment_no_internet_connection);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(this);

        if(!isConnected) {
            finish();
        }
    }

    public void friendsList(View v) {
        Intent intent = new Intent(MainActivity.this, FriendsListActivity.class);
        startActivity(intent);
    }

    public void shareOnFacebook(View v) {
        final String reviewId = (String) v.getTag();

        sharedReview = reviewId;

        // Create the content


        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://but-app.parseapp.com/view-review.html?id=" + id +"&fbId=" + fbId))
                .setContentTitle(name + "'s reviews")
                .setImageUrl(Uri.parse("http://but-app.parseapp.com/images/featurette.png"))
                .build();

        shareDialog.show(MainActivity.this, content);

    }

    public void showPreviousItem (View v) {
        reviewListFragment.showPreviousItem(v);
    }

    public void showNextItem (View v) {
        reviewListFragment.showNextItem(v);
    }

}