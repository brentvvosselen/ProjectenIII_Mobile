package com.brentvanvosselen.oogappl.fragments.finance;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.Cost;

import java.text.SimpleDateFormat;
import java.util.List;

public class CostItemAdapter  extends RecyclerView.Adapter<CostItemAdapter.CostViewHolder> {

    private List<Cost> costs;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CostItemAdapter(List<Cost> costs) {
        this.costs = costs;
    }

    @Override
    public CostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cost_item, parent, false);

        return new CostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CostViewHolder holder, int position) {
        Cost c = costs.get(position);
        holder.title.setText(c.getTitle());
        holder.amount.setText("€ " + c.getAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(sdf.format(c.getDate()));
        holder.description.setText(c.getDescription());
    }

    @Override
    public int getItemCount() {
        return this.costs.size();
    }

    public class CostViewHolder extends RecyclerView.ViewHolder {
        public TextView title, amount, date, description;

        public CostViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textView_card_cost_title);
            amount = (TextView) view.findViewById(R.id.textView_card_cost_amount);
            date = (TextView) view.findViewById(R.id.textView_card_cost_date);
            description = (TextView) view.findViewById(R.id.textView_card_cost_description);
        }
    }
}
