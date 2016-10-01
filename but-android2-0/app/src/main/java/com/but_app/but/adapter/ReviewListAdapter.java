package com.but_app.but.adapter;

/**
 * Created by Hernando on 12/19/2015.
 */

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.but_app.but.Fragment.ScreenSlideQuoteFragment;
import com.but_app.but.R;
import com.but_app.but.to.Quote;
import com.but_app.but.to.ReviewCard;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    private ArrayList<ReviewCard> mDataset;
    private static final int SWIPE_THRESHOLD_VELOCITY = 80;
    private static final int SWIPE_MIN_DISTANCE = 30;
    private Context ctx;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private ImageView nextGoodQuote;
        private ImageView nextBadQuote;
        private ImageView previousGoodQuote;
        private ImageView previousBadQuote;
        private TextSwitcher badTextSwitcher;
        private TextSwitcher goodTextSwitcher;
        private ImageView share;
        //public ImageView userPicture;
        private TextView but;


        public ViewHolder(View v) {
            super(v);
            goodTextSwitcher = (TextSwitcher) v.findViewById(R.id.good_quote);
            badTextSwitcher = (TextSwitcher) v.findViewById(R.id.bad_quote);
            userName = (TextView) v.findViewById(R.id.card_name);
            nextGoodQuote = (ImageView) v.findViewById(R.id.good_next_item);
            nextBadQuote = (ImageView) v.findViewById((R.id.bad_next_item));
            previousGoodQuote = (ImageView) v.findViewById(R.id.good_previous_item);
            previousBadQuote = (ImageView) v.findViewById((R.id.bad_previous_item));
            share = (ImageView) v.findViewById(R.id.share_review) ;

            but = (TextView) v.findViewById(R.id.but_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewListAdapter(ArrayList<ReviewCard> myDataset, Context context) {
        mDataset = myDataset;
        this.ctx = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cart_row, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.userName.setText(mDataset.get(position).getUserName());
        holder.userName.setTag(mDataset.get(position).getReviewId());

        holder.share.setTag(mDataset.get(position).getReviewId());

        ArrayList<Quote> badQuotes = mDataset.get(position).getBadQuotes();
        ArrayList<Quote> goodQuotes = mDataset.get(position).getGoodQuotes();

        if (holder.goodTextSwitcher.getCurrentView() == null) {
            TextView oldBadText = new TextView(ctx);
            TextView oldGoodText = new TextView(ctx);
            TextView newBadText = new TextView(ctx);
            TextView newGoodText = new TextView(ctx);

            oldBadText.setGravity(Gravity.CENTER);
            oldGoodText.setGravity(Gravity.CENTER);
            newBadText.setGravity(Gravity.CENTER);
            newGoodText.setGravity(Gravity.CENTER);

            holder.badTextSwitcher.addView(oldBadText);
            holder.goodTextSwitcher.addView(oldGoodText);

            holder.badTextSwitcher.addView(newBadText);
            holder.goodTextSwitcher.addView(newGoodText);
        }

        holder.goodTextSwitcher.setText(goodQuotes.get(mDataset.get(position).getGoodPosition()).getQuote());

        holder.badTextSwitcher.setText(badQuotes.get(mDataset.get(position).getBadPosition()).getQuote());

        if (mDataset.get(position).getGoodPosition() == 0) {
            holder.previousGoodQuote.setAlpha(0.3f);
        }

        if (mDataset.get(position).getGoodPosition() == mDataset.get(position).getGoodQuotes().size() -1) {
            holder.nextGoodQuote.setAlpha(0.3f);
        }

        if (mDataset.get(position).getBadPosition() == 0) {
            holder.previousBadQuote.setAlpha(0.3f);
        }

        if (mDataset.get(position).getBadPosition() == mDataset.get(position).getBadQuotes().size() -1) {
            holder.nextBadQuote.setAlpha(0.3f);
        }

        final GestureDetector goodGestureDetector = new GestureDetector(ctx,
                new MyGestureDetector(holder.goodTextSwitcher, mDataset.get(position), 0, holder.previousGoodQuote,
                        holder.nextGoodQuote, holder.previousBadQuote, holder.nextBadQuote));
        holder.goodTextSwitcher.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return goodGestureDetector.onTouchEvent(event);
            }
        });

        final GestureDetector badGestureDetector = new GestureDetector(ctx, new MyGestureDetector(holder.badTextSwitcher, mDataset.get(position), 1,
                holder.previousGoodQuote, holder.nextGoodQuote, holder.previousBadQuote, holder.nextBadQuote));
        holder.badTextSwitcher.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return badGestureDetector.onTouchEvent(event);
            }
        });

        holder.nextBadQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position).getBadPosition() < mDataset.get(position).getBadQuotes().size() - 1) {
                    holder.badTextSwitcher.setInAnimation(ctx, R.anim.fab_slide_in_from_right);
                    holder.badTextSwitcher.setOutAnimation(ctx, R.anim.fab_slide_out_to_left);
                    holder.badTextSwitcher.setText(mDataset.get(position).getBadQuotes().get(mDataset.get(position).getBadPosition() + 1).getQuote());
                    mDataset.get(position).setBadPosition(mDataset.get(position).getBadPosition() + 1);

                    if (mDataset.get(position).getBadPosition() == mDataset.get(position).getBadQuotes().size() - 1){
                        holder.nextBadQuote.setAlpha(0.3f);
                    } else {
                        holder.nextBadQuote.setAlpha(1f);
                    }

                    holder.previousBadQuote.setAlpha(1.0f);
                }
            }
        });

        holder.previousBadQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position).getBadPosition() > 0) {
                    holder.badTextSwitcher.setInAnimation(ctx, R.anim.fab_slide_in_from_left);
                    holder.badTextSwitcher.setOutAnimation(ctx, R.anim.fab_slide_out_to_right);
                    holder.badTextSwitcher.setText(mDataset.get(position).getBadQuotes().get(mDataset.get(position).getBadPosition() - 1).getQuote());
                    mDataset.get(position).setBadPosition(mDataset.get(position).getBadPosition() - 1);

                    if (mDataset.get(position).getBadPosition() == 0) {
                        holder.previousBadQuote.setAlpha(0.3f);
                    } else {
                        holder.previousBadQuote.setAlpha(1f);
                    }

                    holder.nextBadQuote.setAlpha(1.0f);
                }
            }
        });

        holder.nextGoodQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position).getGoodPosition() < mDataset.get(position).getGoodQuotes().size() - 1) {
                    holder.goodTextSwitcher.setText(mDataset.get(position).getGoodQuotes().get(mDataset.get(position).getGoodPosition() + 1).getQuote());
                    mDataset.get(position).setGoodPosition(mDataset.get(position).getGoodPosition() + 1);

                    if (mDataset.get(position).getGoodPosition() == mDataset.get(position).getGoodQuotes().size() - 1) {
                        holder.nextGoodQuote.setAlpha(0.3f);
                    } else {
                        holder.nextGoodQuote.setAlpha(1f);
                    }

                    holder.previousGoodQuote.setAlpha(1.0f);
                }

            }
        });

        holder.previousGoodQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(position).getGoodPosition() > 0) {
                    holder.goodTextSwitcher.setText(mDataset.get(position).getGoodQuotes().get(mDataset.get(position).getGoodPosition() - 1).getQuote());
                    mDataset.get(position).setGoodPosition(mDataset.get(position).getGoodPosition() - 1);

                    if (mDataset.get(position).getGoodPosition() == 0) {
                        holder.previousGoodQuote.setAlpha(0.3f);
                    } else {
                        holder.previousGoodQuote.setAlpha(1f);
                    }

                    holder.nextGoodQuote.setAlpha(1.0f);
                }
            }
        });


//        if(mDataset.get(position).getPlusBad() > 0)
//            holder.plusBad.setText(mDataset.get(position).getPlusBad());
//
//        if(mDataset.get(position).getPlusGood() > 0)
//            holder.plusGood.setText(mDataset.get(position).getPlusGood());


//        Transformation transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(100)
//                .oval(false)
//                .build();
//
//        Picasso.with(ctx)
//                .load("https://graph.facebook.com/" + mDataset.get(position).getUser().get("facebookId")
//                        + "/picture?type=large")
//                .fit()
//                .transform(transformation)
//                .into(holder.userPicture);
//
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        TextSwitcher view;
        ReviewCard card;
        int type;
        ImageView previousGoodQuote;
        ImageView previousBadQuote;
        ImageView nextGoodQuote;
        ImageView nextBadQuote;

        MyGestureDetector (TextSwitcher view, ReviewCard card, int type, ImageView previousGoodQuote,
                           ImageView nextGoodQuote, ImageView previousBadQuote, ImageView nextBadQuote) {
            this.view = view;
            this.card = card;
            this.type = type;
            this.previousBadQuote = previousBadQuote;
            this.previousGoodQuote = previousGoodQuote;
            this.nextGoodQuote = nextGoodQuote;
            this.nextBadQuote = nextBadQuote;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (type == 0) {
                       return nextGoodQuote();
                    } else {
                        return nextBadQuote();
                    }
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if(type == 0) {
                        return previousGoodQuote();
                    } else {
                        return  previousBadQuote();
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean previousGoodQuote() {
            if (card.getGoodPosition() > 0) {
                view.setInAnimation(ctx, R.anim.fab_slide_in_from_left);
                view.setOutAnimation(ctx, R.anim.fab_slide_out_to_right);
                view.setText(card.getGoodQuotes().get(card.getGoodPosition() - 1).getQuote());
                card.setGoodPosition(card.getGoodPosition() - 1);

                if (card.getGoodPosition() == 0) {
                    previousGoodQuote.setAlpha(0.3f);
                } else {
                    previousGoodQuote.setAlpha(1.0f);
                }

                nextGoodQuote.setAlpha(1.0f);

            } else {
                return false;
            }
            return true;
        }

        public boolean nextGoodQuote() {
            if (card.getGoodPosition() < card.getGoodQuotes().size() - 1) {
                view.setInAnimation(ctx, R.anim.fab_slide_in_from_right);
                view.setOutAnimation(ctx, R.anim.fab_slide_out_to_left);
                view.setText(card.getGoodQuotes().get(card.getGoodPosition() + 1).getQuote());
                card.setGoodPosition(card.getGoodPosition() + 1);

                if (card.getGoodPosition() == card.getGoodQuotes().size() - 1) {
                    nextGoodQuote.setAlpha(0.3f);
                } else {
                    nextGoodQuote.setAlpha(1.0f);
                }

                previousGoodQuote.setAlpha(1.0f);

            } else {
                return false;
            }

            return true;
        }

        public boolean previousBadQuote() {
            if (card.getBadPosition() > 0) {
                view.setInAnimation(ctx, R.anim.fab_slide_in_from_left);
                view.setOutAnimation(ctx, R.anim.fab_slide_out_to_right);
                view.setText(card.getBadQuotes().get(card.getBadPosition() - 1).getQuote());
                card.setBadPosition(card.getBadPosition() - 1);

                if (card.getBadPosition() == 0) {
                    previousBadQuote.setAlpha(0.3f);
                } else {
                    previousBadQuote.setAlpha(1f);
                }

                nextBadQuote.setAlpha(1.0f);
            } else {
                return false;
            }

            return true;
        }

        public boolean nextBadQuote() {
            if (card.getBadPosition() < card.getBadQuotes().size() - 1) {
                view.setInAnimation(ctx, R.anim.fab_slide_in_from_right);
                view.setOutAnimation(ctx, R.anim.fab_slide_out_to_left);
                view.setText(card.getBadQuotes().get(card.getBadPosition() + 1).getQuote());
                card.setBadPosition(card.getBadPosition() + 1);

                if (card.getBadPosition() == card.getBadQuotes().size() - 1){
                    nextBadQuote.setAlpha(0.3f);
                } else {
                    nextBadQuote.setAlpha(1.0f);
                }

                previousBadQuote.setAlpha(1.0f);

            } else {
                return false;
            }

            return true;
        }
    }


}