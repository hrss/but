package com.but_app.but.adapter;

/**
 * Created by Hernando on 05/01/2015.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.but_app.but.R;
import com.but_app.but.to.Quote;

import java.util.ArrayList;


public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private ArrayList<Quote> mDataset;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView quote;
        public CheckBox cb;

        public ViewHolder(View v) {
            super(v);
            quote = (TextView) v.findViewById(R.id.checkbox_quote);
            cb = (CheckBox) v.findViewById(R.id.checkbox_quote);
            if(quote == null)
                quote = (TextView) v.findViewById(R.id.quote);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShareAdapter(ArrayList<Quote> myDataset,Context context) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if(viewType == 0)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_view_item, parent, false);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Sets the quote's text and the quote's id as a tag
        if(mDataset.get(position).getInQuote() == null) {
            holder.quote.setText(mDataset.get(position).getQuote());
            holder.quote.setTag(mDataset.get(position));
            if(!mDataset.get(position).getQuote().equals(context.getResources().getString(R.string.but)))
                holder.cb.setChecked(mDataset.get(position).isChecked());
        }
        else
            holder.quote.setText(mDataset.get(position).getInQuote());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(mDataset.get(position).getQuote().equals(context.getResources().getString(R.string.but)))
           return 1;
        else
           return 0;
    }

}