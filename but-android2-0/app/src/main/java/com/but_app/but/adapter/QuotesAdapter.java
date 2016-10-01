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


public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.ViewHolder> {
    private ArrayList<Quote> mDataset;
    private ArrayList<String> savedQuotes;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView quote;
        public CheckBox cb;

        public ViewHolder(View v) {
            super(v);
            cb = (CheckBox) v.findViewById(R.id.checkbox_quote);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuotesAdapter(ArrayList<Quote> myDataset, ArrayList<String> savedQuotes) {
        mDataset = myDataset;
        this.savedQuotes = savedQuotes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Sets the quote's text and the quote's id as a tag
        if(mDataset.get(position).getInQuote() == null) {
            holder.cb.setText(mDataset.get(position).getQuote());
            holder.cb.setTag(mDataset.get(position).getId());
            if(mDataset.get(position).isChecked() != null)
                holder.cb.setChecked(mDataset.get(position).isChecked());

        } else {
            holder.quote.setText(mDataset.get(position).getInQuote());
        }

        holder.cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                String tag = (String) v.findViewById(R.id.checkbox_quote).getTag(); //selected quote
                if(cb.isChecked()){
                    if(!savedQuotes.contains(tag))
                        savedQuotes.add(tag);
                } else {
                    for(int i = 0; i < savedQuotes.size(); i++){
                        if(tag.equals(savedQuotes.get(i))){
                            savedQuotes.remove(i);
                        }
                    }
                }

                mDataset.get(position).setChecked(cb.isChecked());
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}