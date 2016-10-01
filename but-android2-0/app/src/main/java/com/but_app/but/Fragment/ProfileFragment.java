package com.but_app.but.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.but_app.but.Activity.FriendsListActivity;
import com.but_app.but.R;
import com.but_app.but.But;
import com.but_app.but.adapter.TopQuotesAdapter;
import com.but_app.but.adapter.TopQuotesPagerAdapter;
import com.but_app.but.to.Quote;
import com.but_app.but.util.ButSingleton;
import com.but_app.but.util.ButUtils;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends android.support.v4.app.Fragment {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Boolean isConnected;
    private ConnectivityManager connectivityManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle params = new Bundle();
        params.putString("fields", "id, name");

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView profileName = (TextView) view.findViewById(R.id.profileName);
        final TextView description = (TextView) view.findViewById(R.id.descripton);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View view = inflater.inflate(R.layout.edit_text_dialog, container, false);
                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        description.setText(((EditText) view.findViewById(R.id.description_edit)).getText().toString());
                        ParseUser.getCurrentUser().put("description", ((EditText) view.findViewById(R.id.description_edit)).getText().toString());
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.setTitle(R.string.description_title);
                builder.setCancelable(true);
                builder.show();
            }
        });
        profileName.setText((String) ParseUser.getCurrentUser().get("realName"));

        if (ParseUser.getCurrentUser().get("description") != null)
            description.setText((String) ParseUser.getCurrentUser().get("description"));


        View img = view.findViewById(R.id.imageLayoutProfile);
        final ImageLoader imgLoader = ButSingleton.getInstance(getActivity()).getImageLoader();
        img.post(new Runnable() {
            @Override
            public void run() {
                imgLoader.get("https://graph.facebook.com/" + ParseUser.getCurrentUser().get("facebookId") + "/picture?height=250", new ImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            view.findViewById(R.id.imageLayoutProfile).setLayoutParams(new LinearLayout.LayoutParams(view.findViewById(R.id.imageLayoutProfile).getMeasuredWidth(), view.findViewById(R.id.imageLayoutProfile).getMeasuredWidth()));
                            view.findViewById(R.id.imageLayoutProfile).setBackground(new BitmapDrawable(getActivity().getResources(), response.getBitmap()));
                            ParseCloud.callFunctionInBackground("topQuotes", new HashMap<String, Object>(), new FunctionCallback<ArrayList<HashMap<String, Object>>>() {

                                public void done(ArrayList<HashMap<String, Object>> topQuotesArray, com.parse.ParseException e) {
                                    if (e == null) {
                                        if (topQuotesArray.size() == 0) {
                                            topQuotesArray = new ArrayList<HashMap<String, Object>>();
                                            HashMap<String, Object> emptyTopQuotes = new HashMap<String, Object>();
                                            Quote q = new Quote(getString(R.string.no_top_quotes));
                                            emptyTopQuotes.put("quote", q);
                                            emptyTopQuotes.put("count", new Integer(0));
                                            topQuotesArray.add(emptyTopQuotes);
                                        }

                                        mPager = (ViewPager) getActivity().findViewById(R.id.top_quotes);
                                        mPagerAdapter = new TopQuotesPagerAdapter(getFragmentManager(), topQuotesArray);
                                        mPager.setAdapter(mPagerAdapter);

                                    } else {
                                        if (topQuotesArray == null || topQuotesArray.size() == 0) {
                                            topQuotesArray = new ArrayList<HashMap<String, Object>>();
                                            HashMap<String, Object> emptyTopQuotes = new HashMap<String, Object>();
                                            Quote q = new Quote(getString(R.string.no_top_quotes));
                                            emptyTopQuotes.put("quote", q);
                                            emptyTopQuotes.put("count", new Integer(0));
                                            topQuotesArray.add(emptyTopQuotes);
                                            mPager = (ViewPager) getActivity().findViewById(R.id.top_quotes);
                                            mPagerAdapter = new TopQuotesPagerAdapter(getFragmentManager(), topQuotesArray);
                                            mPager.setAdapter(mPagerAdapter);
                                        }

                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

        //Set Background

//        if(But.getConfigHelper().getBackground() == null)
//            But.getConfigHelper().setBackground(new BitmapDrawable(
//                    getResources(), ButUtils.decodeSampledBitmapFromResource(
//                    getResources(), R.drawable.background, view.getWidth(), view.getHeight())));
//
//        view.setBackground(But.getConfigHelper().getBackground());

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showPreviousItem () {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    public void showNextItem () {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

}
