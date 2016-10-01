package com.but_app.but.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.but_app.but.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (/*ButUtils.tutorialShown() == */false) {

                } else {
                    FacebookSdk.sdkInitialize(getApplicationContext());

                    if (AccessToken.getCurrentAccessToken() == null) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    } else {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ATAuth");

                        query.whereEqualTo("user", ParseUser.getCurrentUser());
                        query.whereEqualTo("installation", ParseInstallation.getCurrentInstallation());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> tokens, ParseException e) {
                                if (e == null) {
                                    if(tokens.size() != 0){
                                        tokens.get(0).put("token", AccessToken.getCurrentAccessToken().getToken());
                                        tokens.get(0).saveInBackground();
                                    } else {
                                        ParseObject token= new ParseObject("ATAuth");
                                        token.put("token", AccessToken.getCurrentAccessToken().getToken());
                                        token.put("installation", ParseInstallation.getCurrentInstallation());
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

                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        finish();
                        startActivity(i);
                    }
                }
            }
        }, SPLASH_TIME_OUT);

    }
}
