package com.brentvanvosselen.oogappl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.fragments.ChildInfoFragment;

public class ChildInfoView extends LinearLayout {

    private Child[] children = new Child[0];

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

    public void initView() {
        this.setOrientation(LinearLayout.VERTICAL);
        
        for(Child c : this.children) {
            String[] temp = c.getInfo().split(";");
            for(String s : temp) {
                String[] sTemp = s.split(":");
                Log.i("VIEW", "Add item: " + sTemp[0] + ":  " + sTemp[1]);
                ChildInfoItem item = new ChildInfoItem(getContext(), sTemp[0], sTemp[1]);
                this.addView(item);
            }
        }
    }
}