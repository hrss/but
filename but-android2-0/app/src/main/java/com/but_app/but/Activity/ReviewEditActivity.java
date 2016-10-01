package com.but_app.but.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import com.but_app.but.Fragment.SendReviewDialogFragment;
import com.but_app.but.R;

import com.but_app.but.But;
import com.but_app.but.adapter.ReviewQuotesAdapter;
import com.but_app.but.to.Friend;
import com.but_app.but.to.Quote;
import com.but_app.but.to.Review;
import com.but_app.but.util.ButSingleton;
import com.but_app.but.util.ButUtils;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ReviewEditActivity extends AppCompatActivity implements SendReviewDialogFragment.SendReviewDialogListener{
    private ArrayList<String> savedGoodQuotes;
    private ArrayList<String> savedBadQuotes;
    private RecyclerView goodQuotesRecyclerView; //Contains the selected good quotes
    private RecyclerView badQuotesRecyclerView;  //Contains the selected bad quotes
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager2;
    private ArrayList<Quote> mDataset;
    private Friend friend;
    private ParseUser user;
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

        if (isConnected) {
            setContentView(R.layout.activity_review_edit);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            goodQuotesRecyclerView = (RecyclerView) findViewById(R.id.quotes_edit_list_good);
            goodQuotesRecyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            goodQuotesRecyclerView.setLayoutManager(layoutManager);

            badQuotesRecyclerView = (RecyclerView) findViewById(R.id.quotes_edit_list_bad);
            badQuotesRecyclerView.setHasFixedSize(true);
            layoutManager2 = new LinearLayoutManager(this);
            badQuotesRecyclerView.setLayoutManager(layoutManager2);

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
    public void onResume(){
        super.onResume();

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected) {
            setContentView(R.layout.fragment_no_internet_connection);
        } else {

            friend = (Friend) getIntent().getSerializableExtra("friend");

            if (friend != null) {

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("facebookId", friend.getFacebookId());
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            user = objects.get(0);
                            try {
                                user.fetch();

                                //Setting the name in the first card with the user's first name
                                ((TextView) findViewById(R.id.card_name)).
                                        setText(((String) user.get("realName")).split(" ")[0] + ":");

                            } catch (Exception innerE) {
                                innerE.printStackTrace();
                            }

                        } else {
                            Log.d("But", "Something went wrong. ReviewActivity");
                        }
                    }
                });
            }
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
        getMenuInflater().inflate(R.menu.menu_review_edit, menu);
        return true;
    }

    public void onClickFuncI(View view){
        Intent intent = new Intent(ReviewEditActivity.this, QuotesActivity.class);
        intent.putExtra("savedQuotes", savedGoodQuotes);
        intent.putExtra("requestCode", 100);
        startActivityForResult(intent, 100);//Good Quotes
    }

    public void onFriendClick(View view){
        //TODO
    }

    public void onClickFuncB(View view){
        Intent intent = new Intent(ReviewEditActivity.this, QuotesActivity.class);
        intent.putExtra("savedQuotes", savedBadQuotes);
        intent.putExtra("requestCode", 101);
        startActivityForResult(intent, 101);//Bad Quotes
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && requestCode == 100) {
            savedGoodQuotes = (ArrayList) intent.getSerializableExtra("savedQuotes");
            if (savedGoodQuotes != null && !savedGoodQuotes.isEmpty()) {
                if (savedGoodQuotes.size() > 4) {
                    ArrayList<Quote> moreThan4 = new ArrayList<>();

                    for (int i = 0; i < 4; i++)
                        moreThan4.add(But.getConfigHelper().getGoodQuotes().get(savedGoodQuotes.get(i)));

                    moreThan4.add(new Quote("..."));
                    mDataset = moreThan4;
                } else {
                    mDataset = new ArrayList<>();
                    for(String i : savedGoodQuotes){
                        mDataset.add(But.getConfigHelper().getGoodQuotes().get(i));
                    }
                }
                mAdapter = new ReviewQuotesAdapter(mDataset);
                goodQuotesRecyclerView.setAdapter(mAdapter);

            } else {
                mDataset = new ArrayList<Quote>();
                mAdapter = new ReviewQuotesAdapter(mDataset);
                goodQuotesRecyclerView.setAdapter(mAdapter);

            }
        } else if (resultCode == RESULT_OK && requestCode == 101){
            savedBadQuotes = (ArrayList) intent.getSerializableExtra("savedQuotes");
            if (savedBadQuotes != null && !savedBadQuotes.isEmpty()) {
                if(savedBadQuotes.size() > 4){
                    ArrayList<Quote> moreThan4 = new ArrayList<>();

                    for (int i = 0; i < 4; i++)
                        moreThan4.add(But.getConfigHelper().getBadQuotes().get(savedBadQuotes.get(i)));

                    moreThan4.add(new Quote("..."));
                    mDataset = moreThan4;
                } else {
                    mDataset = new ArrayList<>();
                    for(String i : savedBadQuotes){
                        mDataset.add(But.getConfigHelper().getBadQuotes().get(i));
                    }
                }
                mAdapter = new ReviewQuotesAdapter(mDataset);
                badQuotesRecyclerView.setAdapter(mAdapter);
            } else {
                mDataset = new ArrayList<Quote>();
                mAdapter = new ReviewQuotesAdapter(mDataset);
                badQuotesRecyclerView.setAdapter(mAdapter);

            }
        }
    }

    public void post(){
        final Review review = new Review();
        final ArrayList allQuotes = new ArrayList();

        if (savedBadQuotes != null && savedBadQuotes.size() > 0
                && savedGoodQuotes != null && savedGoodQuotes.size() > 0) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.send_review_dialog);
            builder.setPositiveButton(R.string.action_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (user != null) {
                        review.setUser(user);

                        for (String i : savedBadQuotes) {
                            allQuotes.add(But.getConfigHelper().getBadQuotes().get(i));
                        }

                        for (String i : savedGoodQuotes) {
                            allQuotes.add(But.getConfigHelper().getGoodQuotes().get(i));
                        }

                        review.setQuotes(allQuotes);
                        review.put("reviewOwner", user);

                        ParseACL acl = new ParseACL();
                        acl.setPublicReadAccess(false);
                        acl.setPublicWriteAccess(false);
                        acl.setReadAccess(user, true);
                        review.setACL(acl);


                        final HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("recipientUser", user.getObjectId());

                        review.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                try {
                                    ParseCloud.callFunction("sendPush", params);
                                } catch (Exception e2) {
                                    Log.e("But", e2.toString());
                                    e2.printStackTrace();
                                }
                                Intent result = new Intent();
                                setResult(RESULT_OK, result);

                                finish();
                            }
                        });

                    } else {
                        Log.d("But", "Something went wrong. ReviewActivity");
                    }
                }
            });

            builder.setNegativeButton(R.string.cancel, null);
            builder.setTitle(R.string.send_review_dialog);
            builder.setCancelable(true);
            builder.show();

        } else {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.send_both_quotes),
                    Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    public void goUp(View v){
        NavUtils.navigateUpFromSameTask(this);
    }

    public void showSettings(View v){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            post();
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        SendReviewDialogFragment dialog = new SendReviewDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }



}
