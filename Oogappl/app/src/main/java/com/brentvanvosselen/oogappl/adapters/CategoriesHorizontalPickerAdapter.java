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



    String currentColor = "#2CA49D";
    APIInterface apiInterface;
    SharedPreferences sharedPreferences;
    User currentUser;

    public CategoriesHorizontalPickerAdapter(Context context, List<Category> categories, RecyclerView recyclerView, Fragment fragment, String fragment_tag) {
        this.context = context;
        this.categories = categories;
        this.recyclerView = recyclerView;
        this.fragment = fragment;
        this.fragment_tag = fragment_tag;

        apiInterface = RetrofitClient.getClient(context).create(APIInterface.class);
        sharedPreferences = context.getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));


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

        if(position == categories.size()){
            cvh.pickerTxt.setText(R.string.new_category_plus);
            cvh.imageView.setVisibility(View.INVISIBLE);
        }else{
            Category selectedCategory = categories.get(position);
            cvh.pickerTxt.setText(selectedCategory.getType());
            int color = Color.parseColor(selectedCategory.getColor());
            int[] colors = {color};
            Bitmap colorBm =  Bitmap.createBitmap(colors,1,1, Bitmap.Config.ARGB_8888);
            cvh.imageView.setImageBitmap(colorBm);
        }


        cvh.pickerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView != null){
                    recyclerView.smoothScrollToPosition(position);
                }

            }
        });

        cvh.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == categories.size()){
                    Snackbar.make(view,R.string.new_category_press,Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        cvh.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(position == categories.size()){
                    Log.i("event","add category");

                    //create an alert dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    //inflate custom dialog
                    final View mView = inflater.inflate(R.layout.dialog_add_category, null);

                    final ImageView vImageviewAddCategory = mView.findViewById(R.id.imageview_dialog_add_category_color);
                    vImageviewAddCategory.setBackgroundColor(Color.parseColor(currentColor));

                    final EditText vEdittextAddCategoryType = mView.findViewById(R.id.edittext_dialog_add_category_type);

                    vImageviewAddCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ColorPickerDialogBuilder
                                    .with(context)
                                    .setTitle(R.string.choose_color)
                                    .initialColor(Color.parseColor(currentColor))
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(8)
                                    .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int color, Integer[] colors) {
                                            vImageviewAddCategory.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(color)));
                                            currentColor = "#" + Integer.toHexString(color);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });


                    builder.setView(mView)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i("event", "add category");
                                    Category newCategory = new Category(vEdittextAddCategoryType.getText().toString(), currentColor);
                                    categories.add(newCategory);
                                    Call addCategoryCall = apiInterface.addCategory("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(),newCategory);
                                    addCategoryCall.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, R.string.new_category_pos, Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(context, R.string.new_category_neg, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call call, Throwable t) {
                                            //Toast.makeText(getContext(),"Kon geen connectie maken met server",Toast.LENGTH_SHORT).show();
                                            call.cancel();
                                        }
                                    });

                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i("event", "cancel category");
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            switch(fragment_tag){
                                case "agenda_edit":
                                    ((AgendaEditItemFragment)fragment).rerenderCategories(categories);
                                    break;
                                case "heen_en_weer_item":
                                    ((HeenEnWeerItemEditFragment)fragment).rerenderCategories(categories);
                                    break;
                                default:
                            }

                        }
                    }).show();

                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size() + 1 ;
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
