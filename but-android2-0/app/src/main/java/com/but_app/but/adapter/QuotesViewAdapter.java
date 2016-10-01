package com.but_app.but.adapter;

/**
 * Created by Hernando on 05/01/2015.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.but_app.but.R;
import com.but_app.but.to.Quote;

import java.util.ArrayList;


public class QuotesViewAdapter extends RecyclerView.Adapter<QuotesViewAdapter.ViewHolder> {
    private ArrayList<Quote> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quote;

        public ViewHolder(View v) {
            super(v);
            quote = (TextView) v.findViewById(R.id.quote);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuotesViewAdapter(ArrayList<Quote> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuotesViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_view_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataset.get(position).getInQuote() == null)
            holder.quote.setText(mDataset.get(position).getQuote());
        else
            holder.quote.setText(mDataset.get(position).getInQuote());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}