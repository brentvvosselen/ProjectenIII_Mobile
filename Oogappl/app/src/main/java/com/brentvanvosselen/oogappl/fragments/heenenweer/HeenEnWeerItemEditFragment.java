package com.brentvanvosselen.oogappl.fragments.heenenweer;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.CategoriesHorizontalPickerAdapter;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;

/**
 * Created by brentvanvosselen on 21/11/2017.
 */

public class HeenEnWeerItemEditFragment extends Fragment{

    private HeenEnWeerItem item;
    private String dayid;

    private TextView vTextViewTitle;
    private EditText vEditTextValue;
    private Spinner vSpinnerCategory;
    private Button vButtonSave;
    private RecyclerView vRecyclerCategories;
    private PickerLayoutManager categoriesPickerLayoutManager;
    private CategoriesHorizontalPickerAdapter mCategoryAdapter;
    private ImageButton vButtonAddCategory;

    APIInterface apiInterface;
    SharedPreferences sharedPreferences;

    private User currentUser;
    private int selectedCategory = 0;

    private List<Category> categories;

    String currentColor = "#2CA49D";

    public static HeenEnWeerItemEditFragment newInstance(HeenEnWeerItem item){
        HeenEnWeerItemEditFragment fragment = new HeenEnWeerItemEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("item",item);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static HeenEnWeerItemEditFragment newInstance(String dayid){
        HeenEnWeerItemEditFragment fragment = new HeenEnWeerItemEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString("dayid",dayid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        //set title
        final TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        //get current user
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        vButtonSave = getView().findViewById(R.id.button_heenenweer_item_edit_save);
        vEditTextValue = getView().findViewById(R.id.edittext_heenenweer_item_edit_value);
        vTextViewTitle = getView().findViewById(R.id.textview_heenenweer_item_edit_title);
        vButtonAddCategory = getView().findViewById(R.id.imagebutton_heenenweer_item_edit_add_category);

        fillSpinner();

        if(item!= null){
            vEditTextValue.setText(item.getValue());
            vTextViewTitle.setText(R.string.edit_item);
        }else{
            vTextViewTitle.setText(R.string.add_item);
            vButtonSave.setText(R.string.add);
        }


        vButtonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create an alert dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getActivity().getLayoutInflater();
                //inflate custom dialog
                final View mView = inflater.inflate(R.layout.dialog_add_category, null);

                final ImageView vImageviewAddCategory = mView.findViewById(R.id.imageview_dialog_add_category_color);
                vImageviewAddCategory.setBackgroundColor(Color.parseColor(currentColor));

                final EditText vEdittextAddCategoryType = mView.findViewById(R.id.edittext_dialog_add_category_type);

                vImageviewAddCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ColorPickerDialogBuilder
                                .with(getContext())
                                .setTitle(R.string.choose_color)
                                .initialColor(Color.parseColor(currentColor))
                                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                .density(8)
                                .setPositiveButton("ok", new ColorPickerClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int color, Integer[] colors) {
                                        vImageviewAddCategory.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(color)));
                                        currentColor = "#" + Integer.toHexString(color);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
                                Log.i("event","add category");
                                Category newCategory = new Category(vEdittextAddCategoryType.getText().toString(), currentColor);

                                Call addCategoryCall = apiInterface.addCategory("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(),newCategory);
                                addCategoryCall.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if(response.isSuccessful()){
                                            Snackbar.make(getView(),R.string.new_category_pos,Snackbar.LENGTH_SHORT).show();

                                        }else{
                                            Snackbar.make(getView(),R.string.new_category_neg,Snackbar.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        Snackbar.make(getView(),"Could not connect to server",Snackbar.LENGTH_SHORT).show();
                                        call.cancel();
                                    }
                                });

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("event","cancel category");
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        fillSpinner();
                    }
                }).show();
            }

        });
        vButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctForm = true;

                String value = vEditTextValue.getText().toString();
                Category category = null;

                if(selectedCategory == categories.size()){
                    correctForm = false;
                    Snackbar.make(getView(),"Geen categorie geselecteerd",Snackbar.LENGTH_SHORT).show();
                }else{
                    category = categories.get(selectedCategory);
                }


                if(!value.isEmpty() && value != null && category != null){
                    if(item != null){
                        item.setCategory(category);
                        item.setValue(value);
                        Call editHeenEnWeerItemCall = apiInterface.editHeenEnWeerItem("bearer " + sharedPreferences.getString("token",null), item.getId(),item);
                        editHeenEnWeerItemCall.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if(response.isSuccessful()){
                                    Snackbar.make(getView(),"Item gewijzigd",Snackbar.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }else{
                                    Snackbar.make(getView(),"Item niet gewijzigd",Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Snackbar.make(getView(),"Item niet gewijzigd",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        item = new HeenEnWeerItem(category,value);
                        Call addHeenEnWeerItemCall = apiInterface.addHeenEnWeerItem("bearer " + sharedPreferences.getString("token",null), dayid,item);
                        addHeenEnWeerItemCall.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if(response.isSuccessful()){
                                    Snackbar.make(getView(),"item toegevoegd",Snackbar.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }else{
                                    Snackbar.make(getView(), "item niet toegevoegd",Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Snackbar.make(getView(),"Kon niet verbinden met server",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    Snackbar.make(getView(),"Velden niet ingevuld",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        if(item != null){
            title.setText(R.string.edit_item);
            Log.i("ITEM",item.getId());

        }else{
            title.setText(R.string.add_item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try{
            item = (HeenEnWeerItem)getArguments().getSerializable("item");
        }catch(NullPointerException ex){
            Snackbar.make(getView(),"Geen item gevonden",Snackbar.LENGTH_SHORT).show();
        }catch(Exception e){
            Snackbar.make(getView(),"Geen item gevonden",Snackbar.LENGTH_SHORT).show();
        }

        try{
            dayid = getArguments().getString("dayid");
        }catch(Exception e){
            Snackbar.make(getView(),"Geen dag gevonden",Snackbar.LENGTH_SHORT).show();
        }



        return inflater.inflate(R.layout.fragment_heenenweer_edit_item,container,false);
    }


    private void fillSpinner(){
        Call categoriesCall = apiInterface.getCategoriesFromUser("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        categoriesCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    categories = (List<Category>) response.body();

                    vRecyclerCategories = getView().findViewById(R.id.recycler_heenenweer_item_categories);

                    categoriesPickerLayoutManager = new PickerLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                    categoriesPickerLayoutManager.setChangeAlpha(true);
                    categoriesPickerLayoutManager.setScaleDownBy(0.8f);
                    categoriesPickerLayoutManager.setScaleDownDistance(0.99f);

                    mCategoryAdapter = new CategoriesHorizontalPickerAdapter(getContext(),categories,vRecyclerCategories);

                    SnapHelper snapHelper = new LinearSnapHelper();
                    vRecyclerCategories.setOnFlingListener(null);
                    snapHelper.attachToRecyclerView(vRecyclerCategories);

                    vRecyclerCategories.setLayoutManager(categoriesPickerLayoutManager);

                    vRecyclerCategories.setAdapter(mCategoryAdapter);



                    categoriesPickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
                        @Override
                        public void selectedView(View view) {
                            selectedCategory = categoriesPickerLayoutManager.getPosition(view);

                        }
                    });



                    if(item != null){
                        for(int i = 0 ; i<categories.size();i++){
                            if(categories.get(i).getType().equals(item.getCategory().getType()))
                                selectedCategory = i;

                        }
                        vRecyclerCategories.scrollToPosition(selectedCategory);
                    }

                }else{
                    Snackbar.make(getView(),"Could not retrieve categories",Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(),"Could not connect to server",Snackbar.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

}
