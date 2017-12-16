package com.brentvanvosselen.oogappl.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaEditItemFragment;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerItemEditFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.OPTIONS;

/**
 * Created by brentvanvosselen on 16/12/2017.
 */

public class CategoriesHorizontalPickerAdapter extends  RecyclerView.Adapter<CategoriesHorizontalPickerAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private RecyclerView recyclerView;
    private Fragment fragment;
    private String fragment_tag;





    public CategoriesHorizontalPickerAdapter(Context context, List<Category> categories, RecyclerView recyclerView) {
        this.context = context;
        this.categories = categories;
        this.recyclerView = recyclerView;

    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.category_picker_item_layout,parent,false);
        return new CategoriesHorizontalPickerAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        CategoryViewHolder cvh = holder;

            Category selectedCategory = categories.get(position);
            cvh.pickerTxt.setText(selectedCategory.getType());
            int color = Color.parseColor(selectedCategory.getColor());
            int[] colors = {color};
            Bitmap colorBm =  Bitmap.createBitmap(colors,1,1, Bitmap.Config.ARGB_8888);
            cvh.imageView.setImageBitmap(colorBm);


        cvh.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView != null){
                    recyclerView.smoothScrollToPosition(position);
                }

            }
        });


    }



    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView pickerTxt;
        CircularImageView imageView;
        LinearLayout linearLayout;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            pickerTxt = itemView.findViewById(R.id.textview_category_picker_name);
            imageView = itemView.findViewById(R.id.imageview_category_picker_color);
            linearLayout = itemView.findViewById(R.id.linearlayout_category_picker);
        }
    }
}
