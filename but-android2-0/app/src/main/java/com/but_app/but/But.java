package com.but_app.but;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.but_app.but.to.Quote;
import com.but_app.but.to.Review;
import com.but_app.but.util.ConfigHelper;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by iGor Montella on 04/10/2014.
 */
public class But extends Application{
    private static But instance = new But();
    private static ConfigHelper configHelper;
    private final AtomicBoolean debugMode = new AtomicBoolean(false);

    public static final String TAG = But.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        savePackageInfoInformation();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Quote.class);
        ParseObject.registerSubclass(Review.class);
        Parse.initialize(this, "TFkqT4mJlrzRnBqFuEK06HgmYdHo6ClvT8uqLrM5", "qbeDQ4YsgwdUtzBvBrc5okwKuKyqofxxKOwTlN1q");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        ParseFacebookUtils.initialize(this.getApplicationContext());
        ParseInstallation.getCurrentInstallation().saveInBackground();
        configHelper = new ConfigHelper();
        configHelper.fetchQuotesIfNeeded();
    }

    public static But get() {
        return instance;
    }

    public static ConfigHelper getConfigHelper(){
        return configHelper;
    }

    @SuppressWarnings("ConstantConditions")
    private void savePackageInfoInformation() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getClass().getPackage().getName(), 0);

            int flags = packageInfo.applicationInfo.flags;
            debugMode.set((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }


    public boolean isDebugMode() {
        return debugMode.get();
    }

}
