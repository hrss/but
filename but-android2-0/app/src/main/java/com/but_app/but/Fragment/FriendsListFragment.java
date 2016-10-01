package com.but_app.but.Fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.but_app.but.Activity.FriendsListActivity;
import com.but_app.but.Activity.MainActivity;
import com.but_app.but.R;
import com.but_app.but.But;
import com.but_app.but.adapter.FriendsListAdapter;
import com.but_app.but.to.Friend;
import com.but_app.but.util.ButUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FriendsListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Friend> mDataset;
    private Boolean isConnected;
    private ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDataset = new ArrayList<>();

        final ArrayList<String> values = new ArrayList<String>();

        if(But.getConfigHelper().getFriends() == null || But.getConfigHelper().getFriends().isEmpty()) {
            Bundle params = new Bundle();
            params.putString("fields", "id, name, picture");

            GraphRequest request = GraphRequest.newMyFriendsRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray array, GraphResponse response) {
                            FacebookRequestError error = response.getError();
                            if (error == null) {
                                try {
                                    Friend pictureByName;
                                    for (int i = 0; i < array.length(); i++) {
                                        values.add(array.getJSONObject(i).getString("name"));
                                        JSONObject arrayObject = array.getJSONObject(i);
                                        JSONObject picObject = arrayObject.getJSONObject("picture");
                                        String fbId = arrayObject.getString("id");
                                        String picUrl = picObject.getJSONObject("data").getString("url");
                                        pictureByName = new Friend(fbId, values.get(i), picUrl);
                                        mDataset.add(pictureByName);
                                    }
                                    Boolean b = (Boolean) ParseUser.getCurrentUser().get("friendsPushSent");
                                    if(b == null || !b){

                                        try {
                                            final HashMap<String, Object> params = new HashMap<String, Object>();
                                            ParseCloud.callFunction("notifyFriends", params);
                                            ParseUser.getCurrentUser().put("friendsPushSent", true);
                                        } catch (Exception e2){

                                        }

                                    }
                                    Collections.sort(mDataset);
                                    But.getConfigHelper().setFriends(mDataset);

                                    mAdapter = new FriendsListAdapter(mDataset, getActivity());
                                    mRecyclerView.setAdapter(mAdapter);

                                } catch (JSONException e) {

                                }
                            }

                        }
                    });
            request.setParameters(params);
            request.executeAsync();
        } else {
            mDataset = But.getConfigHelper().getFriends();
            mAdapter = new FriendsListAdapter(mDataset, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            FriendsListActivity activity = (FriendsListActivity)getActivity();
            activity.onResume();
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

}
