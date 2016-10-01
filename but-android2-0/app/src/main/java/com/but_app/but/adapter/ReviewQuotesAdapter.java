package com.but_app.but.adapter;

/**
 * Created by Hernando on 08/17/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.but_app.but.R;
import com.but_app.but.to.Quote;

import java.util.ArrayList;


public class ReviewQuotesAdapter extends RecyclerView.Adapter<ReviewQuotesAdapter.ViewHolder> {
    private ArrayList<Quote> mDataset;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView quote;

        public ViewHolder(View v) {
            super(v);
            quote = (TextView) v.findViewById(R.id.text_quote);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewQuotesAdapter(ArrayList<Quote> myDataset) {

        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewQuotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_quote_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Sets the quote's text and the quote's id as a tag
        holder.quote.setText(mDataset.get(position).getQuote());
        holder.quote.setTag(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}