package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.Child;

public class ChildInfoItem extends GridLayout {

    private String name;
    private String value;

    public ChildInfoItem(Context context, String name, String value, boolean editable) {
        super(context);
        initItem(name, value, editable);
    }

    private void initItem(String name, String value, boolean editable) {
        this.name = name;
        this.value = value;

        this.setRowCount(1);
        this.setColumnCount(2);

        if(editable) {
            TextView textViewName = new TextView(getContext());
            textViewName.setText(name);
            this.addView(textViewName);

            EditText textViewValue = new EditText(getContext());
            textViewValue.setText(value);
            this.addView(textViewValue);
        } else {
            TextView textViewName = new TextView(getContext());
            textViewName.setText(name);
            this.addView(textViewName);

            TextView textViewValue = new TextView(getContext());
            textViewValue.setText(value);
            this.addView(textViewValue);
        }

    }
}
