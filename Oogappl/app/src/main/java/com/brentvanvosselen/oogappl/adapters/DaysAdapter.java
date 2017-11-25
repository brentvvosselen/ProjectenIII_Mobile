package com.brentvanvosselen.oogappl.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by brentvanvosselen on 20/11/2017.
 */

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private List<HeenEnWeerDag> daysList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView child, date, description;

        public MyViewHolder(View itemView) {
            super(itemView);
            child = itemView.findViewById(R.id.textview_days_list_child);
            date = itemView.findViewById(R.id.textview_days_list_date);
            description = itemView.findViewById(R.id.textview_days_list_description);


        }
    }

    public DaysAdapter(List<HeenEnWeerDag> daysList){
        this.daysList = daysList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_list_days,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HeenEnWeerDag day = daysList.get(position);
        holder.child.setText(day.getChild().getFirstname());
        holder.date.setText(dateFormat.format(day.getDate()));
        holder.description.setText(day.getDescription());
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }
}
