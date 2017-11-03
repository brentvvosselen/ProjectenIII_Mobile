package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.models.Category;

public class ChildInfoItem extends GridLayout {

    private String name;
    private String value;
    private Category category;

    public ChildInfoItem(Context context, String name, String value) {
        this(context, name, value, false, null);
    }

    public ChildInfoItem(Context context, String name, String value, boolean editable, Category category) {
        super(context);
        this.name = name;
        this.value = value;
        this.category = category;
        initItem(editable);
    }

    private void initItem(boolean editable) {
        this.setRowCount(1);
        this.setColumnCount(2);

        TextView textViewName = new TextView(getContext());
        textViewName.setText(name + ": ");
        this.addView(textViewName);

        if(editable) {
            EditText textViewValue = new EditText(getContext());
            textViewValue.setText(value);
            textViewValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    category.changeInfo(name, editable.toString());
                }
            });
            this.addView(textViewValue);

        } else {
            TextView textViewValue = new TextView(getContext());
            textViewValue.setText(value);
            this.addView(textViewValue);
        }
    }
}
