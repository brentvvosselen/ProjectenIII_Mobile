package com.brentvanvosselen.oogappl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.fragments.ChildInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class ChildInfoView extends LinearLayout {

    private Child[] children = new Child[0];

    public ChildInfoView(Context context) {
        super(context);
    }

    public ChildInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView() {
        this.setOrientation(LinearLayout.VERTICAL);

        if(this.children != null) {
            for(int i = 0; i < this.children.length; i++) {
                final int index = i;
                ChildInfoItem name = new ChildInfoItem(getContext(), children[i].getFirstname(), children[i].getLastname(), false);
                this.addView(name);

                String[] temp = children[i].getInfo().split(";");
                for(String s : temp) {
                    String[] sTemp = s.split(":");
                    Log.i("VIEW", "Add item: " + sTemp[0] + ":  " + sTemp[1]);
                    ChildInfoItem item = new ChildInfoItem(getContext(), sTemp[0], sTemp[1], true);
                    this.addView(item);
                }

                Button buttonAdd = new Button(getContext());
                buttonAdd.setText("Add info");
                buttonAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("Button", "pressed " + index);
                    }
                });
                this.addView(buttonAdd);
            }
        }
    }

    public void setChildren(Child[] children) {
        this.children = children;
        initView();
    }
}