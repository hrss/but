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

public class SlideTopQuoteFragment extends Fragment {
    private String quote;
    private String count;
    private String position;
    private Boolean lastPosition;
    private Boolean firstPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.top_quotes_row, container, false);
        TextView quoteText = (TextView) rootView.findViewById(R.id.quote);
        quoteText.setText(quote);

        TextView countText = (TextView) rootView.findViewById(R.id.count);

        if (quote.length() > 50) {
            quoteText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        countText.setText(count);

        TextView positionText = (TextView) rootView.findViewById(R.id.position);
        positionText.setText(position);

        if (firstPosition) {
            ImageView previousItem = (ImageView) rootView.findViewById(R.id.previous_item);
            previousItem.setAlpha(0.2f);
            previousItem.setOnClickListener(null);
        }

        if (lastPosition) {
            ImageView nextItem = (ImageView) rootView.findViewById(R.id.next_item);
            nextItem.setAlpha(0.2f);
            nextItem.setOnClickListener(null);
        }

        return rootView;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLastPosition(Boolean lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void setFirstPosition(Boolean firstPosition) {
        this.firstPosition = firstPosition;
    }
}