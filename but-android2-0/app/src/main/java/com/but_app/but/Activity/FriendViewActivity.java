package com.but_app.but.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.but_app.but.R;
import com.but_app.but.to.Friend;
import com.but_app.but.util.ButSingleton;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;



public class FriendViewActivity extends AppCompatActivity {
    private Friend friend;
    private ParseUser user;
    private Button publicReviewsButton; //Make Review button, if there isn't public reviews
    private TextView publicReviewsText; //Same, but for the text
    private TextView userName;
    private TextView description;
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

            setContentView(R.layout.activity_friend_view);
            publicReviewsButton = (Button) findViewById(R.id.button_public_reviews);
//        publicReviewsText = (TextView) findViewById(R.id.public_reviews_text);
            userName = (TextView) findViewById(R.id.reviewProfileName);
            description = (TextView) findViewById(R.id.descripton);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            setContentView(R.layout.fragment_no_internet_connection);
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

            final Context ctx = this;
            friend = (Friend) getIntent().getSerializableExtra("friend");

            if (friend != null) {
                //Find the matching friend using his/her facebook Id
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("facebookId", friend.getFacebookId());
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            user = objects.get(0);

                            try {
                                user.fetch();
                                String firstName = ((String) user.get("realName")).split(" ")[0];

                                if (ParseUser.getCurrentUser().get("description") != null)
                                    description.setText((String) user.get("description"));

                                //When found, set the name on the profile picture
                                userName.setText((String) user.get("realName"));

                                //If the user has public reviews
//                                if (user.get("publicReviews") != null && (Integer) user.get("publicReviews") != 0) {
//                                    String text = getResources().getString(R.string.see_reviews_text, firstName, (Integer) user.get("publicReviews"));
//
//                                    publicReviewsText.setText(text);
//                                    publicReviewsButton.setText(getResources().getString(R.string.see_reviews));
//                                    publicReviewsButton.setTag(2); //Random tag just to distinguish it
//                                } else {
//                                    publicReviewsText.setText(getResources().getString(R.string.no_public_reviews, firstName));
//                                }
                            } catch (Exception innerE) {
                                innerE.printStackTrace();

                            }
                        } else {
                            Log.d("But", "Something went wrong. ReviewActivity");
                        }
                    }
                });

                final View v = findViewById(R.id.image_layout_review);
                final ImageLoader imgLoader = ButSingleton.getInstance(ctx).getImageLoader();

                //The screen should be already drawn here, so I can get its width.
                //That's why the v.post
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        imgLoader.get("https://graph.facebook.com/" + friend.getFacebookId() + "/picture?height=250", new ImageLoader.ImageListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }

                            //This is actually really tricky. Here I'm setting the profile picture.
                            //The thing is, Facebook sends us a square image, so, to keep the aspect ratio,
                            //we have to first measure the screen width and set the ImageView's height
                            //and width with the measured number.
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response.getBitmap() != null) {
                                    findViewById(R.id.image_layout_review).setLayoutParams(
                                            new LinearLayout.LayoutParams(findViewById(R.id.image_layout_review).
                                                    getMeasuredWidth(), findViewById(R.id.image_layout_review).getMeasuredWidth()));

                                    findViewById(R.id.image_layout_review).setBackground(
                                            new BitmapDrawable(ctx.getResources(), response.getBitmap()));
                                }
                            }
                        });

                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_review_edit, menu);
        return true;
    }

    public void goUp(View v){
        NavUtils.navigateUpFromSameTask(this);
    }

    public void showSettings(View v){

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!isConnected) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Assigned to the '+' button on the friend's profile (this activity)
     * Also assigne to the make review button in case the user doesn't have
     * any public reviews
     * @param v
     */
    public void makeReview(View v){
        Intent intent = new Intent(FriendViewActivity.this, ReviewEditActivity.class);
        intent.putExtra("friend", friend);

        if(v.getTag() != null && (Integer)v.getTag() == 2)
            viewPublicReview(v);
        else
            startActivityForResult(intent, 103);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && requestCode == 103) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.review_sent),
                    Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    /**
     * Called from makeReview in case the user has any public reviews
     * @param v
     */
    public void viewPublicReview(View v){
//        Intent intent = new Intent(FriendViewActivity.this, PublicReviewsActivity.class);
//        intent.putExtra("userId", user.getObjectId());
//        startActivity(intent);
    }
}