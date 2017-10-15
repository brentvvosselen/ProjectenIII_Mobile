package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class ChildInfoView extends LinearLayout {

    public ChildInfoView(Context context) {
        super(context);
        initView();
    }

    public ChildInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ChildInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0; i < 5; i++) {
            Log.i("VIEW", "Add item");
            ChildInfoItem item = new ChildInfoItem(getContext(), "Name" + i, "Valuee" + i);
            this.addView(item);
        }
    }
}