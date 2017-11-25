package com.brentvanvosselen.oogappl.listeners;

import android.view.View;

/**
 * Created by brentvanvosselen on 21/11/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
