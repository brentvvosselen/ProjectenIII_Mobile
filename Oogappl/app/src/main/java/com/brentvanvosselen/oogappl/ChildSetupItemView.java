package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.brentvanvosselen.oogappl.RestClient.Child;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class ChildSetupItemView extends LinearLayout{

    private Child[] child = new Child[0];

    public ChildSetupItemView(Context context) {
        super(context);
    }

    public ChildSetupItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildSetupItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(){
        this.setOrientation(LinearLayout.VERTICAL);

    }
}
