package com.but_app.but.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.but_app.but.Fragment.SlideTopQuoteFragment;
import com.but_app.but.R;
import com.but_app.but.to.Quote;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hrss on 2/24/16.
 */
public class TopQuotesPagerAdapter  extends FragmentStatePagerAdapter{
    private ArrayList<HashMap<String, Object>> mDataset;

    public TopQuotesPagerAdapter(FragmentManager fm, ArrayList<HashMap<String, Object>> mDataset) {
        super(fm);
        this.mDataset = mDataset;
    }

    @Override
    public Fragment getItem(int position) {
        SlideTopQuoteFragment sl;

        if((Integer) mDataset.get(position).get("count") != 0) {
            Quote q = (Quote) mDataset.get(position).get("quote");

            sl = new SlideTopQuoteFragment();
            sl.setQuote(q.getQuote());
            sl.setCount("(x"+mDataset.get(position).get("count").toString()+")");
            sl.setPosition("#" + (position + 1));

            if (position == getCount() - 1) {
                sl.setLastPosition(true);
            } else {
                sl.setLastPosition(false);
            }

            if (position == 0) {
                sl.setFirstPosition(true);
            } else {
                sl.setFirstPosition(false);
            }
        } else {
            Quote q = (Quote) mDataset.get(position).get("quote");
            sl = new SlideTopQuoteFragment();
            sl.setQuote(q.getQuote());
            sl.setFirstPosition(true);
            sl.setLastPosition(true);
            sl.setQuote(q.getInQuote());
            sl.setCount("");
            sl.setPosition("");
        }

        return sl;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }


}
