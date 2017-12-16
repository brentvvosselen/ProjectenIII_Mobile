package com.brentvanvosselen.oogappl.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

/**
 * Created by brentvanvosselen on 16/12/2017.
 */

public class ChildrenHorizontalPickerAdapter extends RecyclerView.Adapter<ChildrenHorizontalPickerAdapter.ChildViewHolder> {

    private Context context;
    private List<Child> children;
    private RecyclerView recyclerView;

    public ChildrenHorizontalPickerAdapter(Context context, List<Child> children, RecyclerView rv){
        this.context = context;
        this.children = children;
        Log.i("childrenr", children.toString());
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
        if(position == 0){
            cvh.pickerTxt.setText("Geen kinderen");
            cvh.imageView.setVisibility(View.INVISIBLE);
        }else if(position == children.size() + 1){
            cvh.pickerTxt.setText("Alle kinderen");
            cvh.imageView.setVisibility(View.INVISIBLE);
        }else{
            Child selectedChild = children.get(position - 1);
            cvh.pickerTxt.setText(selectedChild.getFirstname());

            if(selectedChild.getPicture() != null){
                byte[] decodedString = Base64.decode(selectedChild.getPicture().getValue(),Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                cvh.imageView.setImageBitmap(decodedByte);
            }else{
                Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.no_picture);
                cvh.imageView.setImageBitmap(image);
            }

        }


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
        return children.size() + 2;
    }


    class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView pickerTxt;
        CircularImageView imageView;

        public ChildViewHolder(View itemView){
            super(itemView);
            pickerTxt = itemView.findViewById(R.id.textview_child_picker_name);
            imageView = itemView.findViewById(R.id.imageview_child_picker_child);
        }
    }
}
