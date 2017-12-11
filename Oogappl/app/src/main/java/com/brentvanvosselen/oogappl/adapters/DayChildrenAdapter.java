package com.brentvanvosselen.oogappl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;

/**
 * Created by brentvanvosselen on 11/12/2017.
 */

public class DayChildrenAdapter extends BaseAdapter {

    private final Context mContext;
    private final HeenEnWeerDag[] days;

    public DayChildrenAdapter(Context context, HeenEnWeerDag[] days){
        this.mContext = context;
        this.days = days;

    }
    @Override
    public int getCount() {
        return this.days.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final HeenEnWeerDag day = days[i];
        if (view == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.row_child_book,null);
        }

        final TextView vTextViewChild = (TextView) view.findViewById(R.id.textview_row_child_book_name);

        vTextViewChild.setText(day.getChild().getFirstname());

        return view;
    }
}
