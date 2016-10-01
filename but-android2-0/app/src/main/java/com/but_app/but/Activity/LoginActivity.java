package com.but_app.but.Activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.but_app.but.R;
import com.but_app.but.But;
import com.but_app.but.Fragment.LoginFragment;
import com.but_app.but.util.ButUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {
    private LoginFragment loginFragment;
    private LoginActivity ctx;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
//        setSupportActionBar(toolbar);
        ctx = this;



        //final View v = findViewById(R.id.authButton);

        //Set Background
//        if(v != null)
//        v.post(new Runnable() {
//            @Override
//            public void run() {
//                if (But.getConfigHelper().getBackground() == null)
//                    But.getConfigHelper().setBackground(new BitmapDrawable(
//                            getResources(), ButUtils.decodeSampledBitmapFromResource(
//                            getResources(), R.drawable.background, ((View) v.getParent().getParent()).getMeasuredWidth(), ((View) v.getParent().getParent()).getMeasuredHeight())));
//
//                ((View)v.getParent()).setBackground(But.getConfigHelper().getBackground());
//
//            }
//        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login_activity, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onButtonPressed(View v) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("user_friends"), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
            if (user == null) {

            } else if (user.isNew()) {

                final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user", user);
                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(false);
                acl.setPublicWriteAccess(false);
                acl.setReadAccess(user, true);
                acl.setWriteAccess(user, true);
                installation.setACL(acl);
                installation.saveInBackground();

                GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                        FacebookRequestError error = response.getError();
                        if (error == null) {
                            try {
                                ParseUser usr = ParseUser.getCurrentUser();
                                usr.put("facebookId", object.getString("id"));
                                usr.put("realName", object.getString("name"));
                                usr.saveInBackground();

                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        }
                    });


                ParseQuery<ParseObject> query = ParseQuery.getQuery("ATAuth");

                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.whereEqualTo("installation", installation);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> tokens, ParseException e) {
                        if (e == null) {
                            if(tokens.size() != 0){
                                tokens.get(0).put("token", AccessToken.getCurrentAccessToken().getToken());
                                tokens.get(0).saveInBackground();
                            } else {
                                ParseObject token= new ParseObject("ATAuth");
                                token.put("token", AccessToken.getCurrentAccessToken().getToken());
                                token.put("installation", installation);
                                token.put("user", ParseUser.getCurrentUser());
                                ParseACL tokenAcl = new ParseACL();
                                tokenAcl.setPublicReadAccess(false);
                                tokenAcl.setPublicWriteAccess(false);
                                tokenAcl.setReadAccess(ParseUser.getCurrentUser(), true);
                                tokenAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
                                token.setACL(tokenAcl);
                                token.saveInBackground();

                            }

                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });


                Bundle params = new Bundle();
                params.putString("fields", "id, name, picture");
                request.setParameters(params);
                request.executeAsync();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(i);

            } else {
                final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user", user);

                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(false);
                acl.setPublicWriteAccess(false);
                acl.setReadAccess(user, true);
                acl.setWriteAccess(user, true);
                installation.setACL(acl);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("ATAuth");

                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.whereEqualTo("installation", installation);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> tokens, ParseException e) {
                        if (e == null) {
                            if(tokens.size() != 0){
                                tokens.get(0).put("token", AccessToken.getCurrentAccessToken().getToken());
                                tokens.get(0).saveInBackground();
                            } else {
                                ParseObject token= new ParseObject("ATAuth");
                                token.put("token", AccessToken.getCurrentAccessToken().getToken());
                                token.put("installation", installation);
                                token.put("user", ParseUser.getCurrentUser());
                                ParseACL tokenAcl = new ParseACL();
                                tokenAcl.setPublicReadAccess(false);
                                tokenAcl.setPublicWriteAccess(false);
                                tokenAcl.setReadAccess(ParseUser.getCurrentUser(), true);
                                tokenAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
                                token.setACL(tokenAcl);
                                token.saveInBackground();

                            }

                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });


                installation.saveInBackground();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        ;
    }
}
