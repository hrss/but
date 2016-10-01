package com.but_app.but.adapter;

/**
 * Created by Hernando on 02/17/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.but_app.but.R;
import com.but_app.but.to.Friend;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {
    private ArrayList<Friend> mDataset;
    private Context ctx;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public ImageView friendPicture;

        public ViewHolder(View v) {
            super(v);
            friendName = (TextView) v.findViewById(R.id.friendName);
            friendPicture = (ImageView) v.findViewById(R.id.friendPicture);
        }
    }

    public FriendsListAdapter(ArrayList<Friend> myDataset, Context context) {
        mDataset = myDataset;
        ctx = context;
    }

    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.friendName.setText(mDataset.get(position).getName());
        holder.friendName.setTag(mDataset.get(position));

        if(mDataset.get(position).getImageUrl() != null && !mDataset.get(position).getImageUrl().isEmpty()) {
            holder.friendPicture.setImageBitmap(null);

            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();

            Picasso.with(ctx)
                    .load(mDataset.get(position).getImageUrl())
                    .fit()
                    .transform(transformation)
                    .into(holder.friendPicture);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}