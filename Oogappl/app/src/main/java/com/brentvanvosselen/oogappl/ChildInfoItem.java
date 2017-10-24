package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.Child;

public class ChildInfoItem extends GridLayout {

    private String name;
    private String value;

    public ChildInfoItem(Context context, String name, String value) {
        super(context);
        initItem(name, value);
    }

    /*
    public ChildInfoItem(Context context, AttributeSet attrs, String name, String value) {
        super(context, attrs);
    }

    public ChildInfoItem(Context context, AttributeSet attrs, int defStyleAttr, String name, String value) {
        super(context, attrs, defStyleAttr);
    }
    */

    private void initItem(String name, String value) {
        this.name = name;
        this.value = value;

        this.setRowCount(1);
        this.setColumnCount(2);

        TextView textViewName = new TextView(getContext());
        textViewName.setText(name);
        this.addView(textViewName);

        TextView textViewValue = new TextView(getContext());
        textViewValue.setText(value);
        this.addView(textViewValue);
    }
}
