package com.but_app.but.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


import com.but_app.but.R;
import com.but_app.but.But;
import com.but_app.but.adapter.QuotesAdapter;
import com.but_app.but.to.Quote;


import java.util.ArrayList;
import java.util.HashMap;

public class QuotesActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Quote> mDataset;
    private ArrayList<String> savedQuotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quotes);
        mRecyclerView = (RecyclerView) findViewById(R.id.quotes_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        But.getConfigHelper().fetchQuotesIfNeeded();
        savedQuotes = new ArrayList<>();

    }

    @Override
    public void onResume(){
        super.onResume();
        Integer reqCode = getIntent().getExtras().getInt("requestCode");
        if(reqCode != null){
            if (reqCode == 100){
                mDataset = new ArrayList<Quote>(But.getConfigHelper().getGoodQuotes().values());
            } else if(reqCode == 101){
                mDataset = new ArrayList<Quote>(But.getConfigHelper().getBadQuotes().values());
            }
        } else {
            mDataset = new ArrayList<>();
        }

        //Unchecking all the quotes
        for(int i = 0; i < mDataset.size(); i++)
            mDataset.get(i).setChecked(false);

        //Checking the quotes that should be checked
        if(getIntent().getSerializableExtra("savedQuotes") != null){
            savedQuotes = (ArrayList)getIntent().getSerializableExtra("savedQuotes");
            for(String quote : savedQuotes){
                for(Quote mQuote : mDataset){
                    if(mQuote.getId().equals(quote))
                        mQuote.setChecked(true);
                }
            }
        }

        mAdapter = new QuotesAdapter(mDataset, savedQuotes);
        mRecyclerView.setAdapter(mAdapter);
    }

//    public void onCheckboxClicked(View view) {
//        boolean checked = ((CheckBox) view).isChecked();
//        String tag = (String) view.findViewById(R.id.checkbox_quote).getTag(); //selected quote
//        if(checked){
//            if(!savedQuotes.contains(tag))
//                savedQuotes.add(tag);
//        } else {
//            for(int i = 0; i < savedQuotes.size(); i++){
//                if(tag.equals(savedQuotes.get(i))){
//                    savedQuotes.remove(i);
//                }
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quotes, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent result = new Intent();
        result.putExtra("savedQuotes", savedQuotes);
        setResult(RESULT_OK, result);
        super.onBackPressed();
    }

    public void goBack(View v){
        onBackPressed();
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