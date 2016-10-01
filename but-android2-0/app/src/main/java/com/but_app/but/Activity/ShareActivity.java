package com.but_app.but.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.but_app.but.R;
import com.but_app.but.But;
import com.but_app.but.adapter.ReviewListAdapter;
import com.but_app.but.adapter.ShareAdapter;
import com.but_app.but.to.Quote;
import com.but_app.but.to.Review;
import com.but_app.but.util.ButUtils;
import com.but_app.but.util.ReviewMap;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShareActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String review;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Quote> savedGoodQuotes;
    private ArrayList<Quote> savedBadQuotes;
    private ArrayList<Quote> allQuotes;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        setContentView(R.layout.activity_share);
        //mRecyclerView = (RecyclerView) findViewById(R.id.share_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        savedBadQuotes = new ArrayList<>();
        savedGoodQuotes = new ArrayList<>();
        allQuotes = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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
                reviewsQuery.whereEqualTo("objectId", review);

                reviewsQuery.findInBackground(new FindCallback<Review>() {
                    @Override
                    public void done(final List<Review> objects, ParseException e) {
                        if(e == null) {
                            for (final Review review : objects) {
                                ParseACL acl = new ParseACL();
                                acl.setPublicWriteAccess(false);
                                acl.setPublicReadAccess(true);

                                review.setACL(acl);
                                review.saveInBackground();

                                final HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("review", (String)review.getId());

                                try {
                                    ParseCloud.callFunction("publishReview", params);
                                } catch (ParseException e1){
                                    e1.printStackTrace();
                                }

                                finish();
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

        review = (String)getIntent().getSerializableExtra("reviewId");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
        query.getInBackground(review, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject review, ParseException e) {
                if(e == null){
                    ParseRelation<ParseObject> relation = review.getRelation("quotes");
                    relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            savedGoodQuotes = new ArrayList<>();
                            savedBadQuotes = new ArrayList<>();

                            for (ParseObject quote : list)
                                if (((Quote) quote).getGoodOrBad())
                                    savedGoodQuotes.add((Quote) quote);
                                else
                                    savedBadQuotes.add((Quote) quote);

                            for(Quote q : savedGoodQuotes)
                                allQuotes.add(q);

                            Quote butQuote = new Quote();

                            butQuote.setQuote(getResources().getString(R.string.but));

                            allQuotes.add(butQuote);

                            for(Quote q : savedBadQuotes)
                                allQuotes.add(q);

                            mAdapter = new ShareAdapter(allQuotes, ctx);
                            mRecyclerView.setAdapter(mAdapter);

                            savedGoodQuotes = new ArrayList<>();
                            savedBadQuotes = new ArrayList<>();
                        }
                    });
                } else {

                }
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quotes, menu);
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

    public void share(View v){
        String r = (String)v.getTag();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.facebook_share);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        canvas.drawText("Some Text here", 2, 3, paint);


    }

    private BitmapDrawable writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(ctx, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(ctx, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return new BitmapDrawable(getResources(), bm);
    }



    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

}

