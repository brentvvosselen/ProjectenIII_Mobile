package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

<<<<<<< HEAD
import com.brentvanvosselen.oogappl.RestClient.ChildinfoCategory;
=======
import com.brentvanvosselen.oogappl.RestClient.models.Category;
>>>>>>> c404d82ee1e6d4b3f1c75bcf54c42e5590a7f985

public class ChildInfoItem extends GridLayout {

    private String name;
    private String value;
    private ChildinfoCategory category;

    public ChildInfoItem(Context context, String name, String value) {
        this(context, name, value, false, null);
    }

    public ChildInfoItem(Context context, String name, String value, boolean editable, ChildinfoCategory category) {
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
