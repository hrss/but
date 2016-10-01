package com.but_app.but.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.but_app.but.R;

public class ScreenSlideQuoteFragment extends Fragment {
    private String quote;
    private Boolean lastPosition;
    private Boolean firstPosition;

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.sliding_quote, container, false);

        TextView quoteText = (TextView) rootView.findViewById(R.id.quote);

        if (quote.length() > 50) {
            quoteText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        quoteText.setText(quote);

        if(firstPosition) {
            ImageView previousItem = (ImageView) rootView.findViewById(R.id.previous_item);
            previousItem.setOnClickListener(null);
            previousItem.setAlpha(0.2f);
        }

        if(lastPosition) {
            ImageView nextItem = (ImageView) rootView.findViewById(R.id.next_item);
            nextItem.setOnClickListener(null);
            nextItem.setAlpha(0.2f);
        }

        return rootView;
    }

    public void setLastPosition(Boolean lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void setFirstPosition(Boolean firstPosition) {
        this.firstPosition = firstPosition;
    }
}