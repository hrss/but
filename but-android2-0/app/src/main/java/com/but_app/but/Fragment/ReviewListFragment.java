package com.but_app.but.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.but_app.but.Activity.MainActivity;
import com.but_app.but.R;

import com.but_app.but.adapter.ReviewListAdapter;
import com.but_app.but.to.Quote;
import com.but_app.but.to.Review;
import com.but_app.but.to.ReviewCard;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ReviewListFragment extends Fragment {
    private ArrayList<ReviewCard> cards;
    private RecyclerView listView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progress;
    private Boolean isConnected;
    private ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.fragment_review_list, container, false);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        MainActivity mainActivity = (MainActivity)getActivity();

        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            mainActivity.onResume();
        } else {

            progress = new ProgressDialog(getActivity());
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();

            cards = new ArrayList<>();

            if(mLayoutManager == null) {
                mLayoutManager = new LinearLayoutManager(getActivity());

                listView = (RecyclerView) getActivity().findViewById(R.id.cards_list_rf);
                listView.setLayoutManager(mLayoutManager);

                    setList(0, 30);
            } else {
                progress.dismiss();
            }

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

    public void setList(int skip, int limit){
        ParseQuery<Review> reviewsQuery = Review.getQuery();
        reviewsQuery.orderByDescending("createdAt");
        reviewsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        reviewsQuery.setLimit(limit);
        reviewsQuery.setSkip(skip);

        reviewsQuery.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(final List<Review> objects, ParseException e) {
                if(e == null) {

                    for (final Review review : objects) {

                        ParseRelation<ParseObject> relation = review.getRelation("quotes");

                        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                            if (e == null) {
                                ArrayList<Quote> mGoodDataset = new ArrayList<Quote>();
                                ArrayList<Quote> mBadDataset = new ArrayList<Quote>();
                                for (ParseObject quote : results)
                                    if (((Quote) quote).getGoodOrBad())
                                        mGoodDataset.add((Quote) quote);
                                    else
                                        mBadDataset.add((Quote) quote);

                                ReviewCard card = new ReviewCard();

                                card.setGoodQuotes(mGoodDataset);

                                card.setBadQuotes(mBadDataset);

                                try {
                                    review.getReviewOwner().fetchIfNeeded();
                                } catch (Exception e1){
                                    e1.printStackTrace();
                                }

                                card.setUser(review.getReviewOwner());

                                card.setReviewId(review.getId());

                                card.setUserName((String) review.getReviewOwner().get("realName"));

                                cards.add(card);

                                ReviewListAdapter mCardArrayAdapter;

                                if (review.equals(objects.get(objects.size() - 1))) {
                                    mCardArrayAdapter = new ReviewListAdapter(cards, getActivity());
                                    if (listView != null) {
                                        listView.setAdapter(mCardArrayAdapter);
                                    }
                                    progress.dismiss();
                                }

                            } else {
                                progress.dismiss();
                                Log.d("But", "Couldn't find the review's quotes.: " + e.toString());
                            }
                            }
                        });

                    }
                    if (objects.size() == 0) {
                        getActivity().findViewById(R.id.no_reviews).setVisibility(View.VISIBLE);
                        progress.dismiss();
                    }
                } else {
                    //NO INTERNET CONNECTION
                }
            }
        });

    }

    public void showPreviousItem (View v) {
        ViewPager mPager = (ViewPager) v.getParent().getParent();
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    public void showNextItem (View v) {
        ViewPager mPager = (ViewPager) v.getParent().getParent();
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

}
