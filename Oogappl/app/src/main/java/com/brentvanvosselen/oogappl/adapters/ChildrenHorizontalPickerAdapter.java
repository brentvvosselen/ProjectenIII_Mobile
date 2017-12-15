package com.brentvanvosselen.oogappl.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;

import java.util.List;

/**
 * Created by brentvanvosselen on 16/12/2017.
 */

public class ChildrenHorizontalPickerAdapter extends RecyclerView.Adapter<ChildrenHorizontalPickerAdapter.ChildViewHolder> {

    private Context context;
    private List<String> children;
    private RecyclerView recyclerView;

    public ChildrenHorizontalPickerAdapter(Context context, List<String> children, RecyclerView rv){
        this.context = context;
        this.children = children;
        this.recyclerView = rv;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.child_picker_item_layout,parent,false);
        return new ChildrenHorizontalPickerAdapter.ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, final int position) {
        ChildViewHolder cvh = holder;
        cvh.pickerTxt.setText(children.get(position));
        cvh.pickerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView != null){
                    recyclerView.smoothScrollToPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView pickerTxt;

        public ChildViewHolder(View itemView){
            super(itemView);
            pickerTxt = (TextView) itemView.findViewById(R.id.child_picker_name);
        }
    }
}
