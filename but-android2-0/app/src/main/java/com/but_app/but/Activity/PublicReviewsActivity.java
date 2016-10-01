//package com.but_app.but.Activity;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.app.Activity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//
//import com.but_app.but.R;
//import com.but_app.but.adapter.ReviewListAdapter;
//import com.but_app.but.to.Friend;
//import com.but_app.but.to.Quote;
//import com.but_app.but.to.Review;
//import com.but_app.but.to.ReviewCard;
//import com.parse.FindCallback;
//import com.parse.GetCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseRelation;
//import com.parse.ParseUser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PublicReviewsActivity extends Activity {
//    private ArrayList<ReviewCard> cards;
//    private RecyclerView listView;
//    private RecyclerView.LayoutManager mLayoutManager;
//    private Context ctx;
//    private String userId;
//    private ParseUser user;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_public_reviews);
//
//        listView = (RecyclerView) findViewById(R.id.cards_list);
//        mLayoutManager = new LinearLayoutManager(this);
//        listView.setLayoutManager(mLayoutManager);
//
//        cards = new ArrayList<>();
//        ctx = this;
//
//        userId = (String) getIntent().getSerializableExtra("userId");
//
//        ParseQuery query = new ParseQuery<>("_User");
//        query.getInBackground(userId, new GetCallback<ParseObject>(){
//            public void done(ParseObject object, ParseException e){
//                if (e == null){
//                    user = (ParseUser) object;
//                    setList(0, 30);
//                } else {
//                    //TODO
//                }
//            }
//        });
//
//
//
//        setList(0, 30);
//
//    }
//
//    public void setList(int skip, int limit){
//        ParseQuery<Review> reviewsQuery = Review.getQuery();
//        reviewsQuery.orderByDescending("createdAt");
//        reviewsQuery.whereEqualTo("originalUser", user);
//        reviewsQuery.setLimit(limit);
//        reviewsQuery.setSkip(skip);
//
//        reviewsQuery.findInBackground(new FindCallback<Review>() {
//            @Override
//            public void done(final List<Review> objects, ParseException e) {
//                if(e == null) {
//                    for (final Review review : objects) {
//
//                        ParseRelation<ParseObject> relation = review.getRelation("quotes");
//
//                        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
//                            public void done(List<ParseObject> results, ParseException e) {
//                                if (e == null) {
//                                    ArrayList<Quote> mGoodDataset = new ArrayList<Quote>();
//                                    ArrayList<Quote> mBadDataset = new ArrayList<Quote>();
//                                    for (ParseObject quote : results)
//                                        if (((Quote) quote).getGoodOrBad())
//                                            mGoodDataset.add((Quote) quote);
//                                        else
//                                            mBadDataset.add((Quote) quote);
//
//
//                                    ReviewCard card = new ReviewCard();
//
//                                    card.setGoodQuote(mGoodDataset.get(0).getQuote());
//                                    card.setBadQuote(mBadDataset.get(0).getQuote());
//                                    card.setPlusGood(mGoodDataset.size() - 1);
//                                    card.setPlusBad(mBadDataset.size() - 1);
//
//                                    try {
//                                        review.getOriginalUser().fetchIfNeeded();
//                                    } catch (Exception e1){
//                                        e1.printStackTrace();
//                                    }
//
//                                    card.setUser(review.getOriginalUser());
//                                    card.setReviewId(review.getId());
//                                    card.setUserName((String) review.getOriginalUser().get("realName"));
//
//                                    cards.add(card);
//
//                                    ReviewListAdapter mCardArrayAdapter;
//
//                                    if (review.equals(objects.get(objects.size() - 1))) {
//                                        mCardArrayAdapter = new ReviewListAdapter(cards, ctx);
//                                        if (listView != null) {
//                                            listView.setAdapter(mCardArrayAdapter);
//                                        }
//                                    }
//
//                                } else {
//                                    Log.d("But", "Couldn't find the review's quotes.: " + e.toString());
//                                }
//                            }
//                        });
//
//                    }
//                } else {
//                    //NO INTERNET CONNECTION
//                }
//            }
//        });
//
//    }
//
//}
