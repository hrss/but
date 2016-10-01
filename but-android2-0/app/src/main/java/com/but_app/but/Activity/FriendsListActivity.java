package com.but_app.but.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.but_app.but.R;
import com.but_app.but.Fragment.FriendsListFragment;
import com.but_app.but.to.Friend;
import com.facebook.appevents.AppEventsLogger;

public class FriendsListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Boolean isConnected;
    private ConnectivityManager connectivityManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if (isConnected){
            setContentView(R.layout.activity_friends_list);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FriendsListFragment friendsListFragment = new FriendsListFragment();

            fragmentTransaction.add(R.id.friends_list_fragment, friendsListFragment);
            fragmentTransaction.commit();

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            setContentView(R.layout.fragment_no_internet_connection);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setLogo(R.drawable.ic_launcher);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

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

        if(!isConnected) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onFriendClick(View v) {
        Intent intent = new Intent(FriendsListActivity.this, FriendViewActivity.class);
        Friend tag = (Friend)(v.findViewById(R.id.friendName).getTag());
        intent.putExtra("friend", tag);
        startActivity(intent);
    }
}
