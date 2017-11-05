package com.brentvanvosselen.oogappl.fragments.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 05/11/2017.
 */

public class AgendaEditItemFragment extends Fragment {

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    EditText vEdittextTitle, vEdittextDescription, vEdittextStartDate, vEdittextEndDate;
    CircularImageView vImageViewCategory;
    Spinner vSpinnerCategory;

    List<Category> categories;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.add_item);

        vEdittextTitle = getView().findViewById(R.id.edittext_edit_event_title);
        vEdittextDescription = getView().findViewById(R.id.edittext_edit_event_description);
        vEdittextStartDate = getView().findViewById(R.id.edittext_edit_event_startDate);
        vEdittextEndDate = getView().findViewById(R.id.textview_edit_event_endDate);
        vImageViewCategory = getView().findViewById(R.id.imageview_edit_event_category);
        vSpinnerCategory = getView().findViewById(R.id.spinner_edit_event_category);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        Call categoriesCall = apiInterface.getCategoriesFromUser(currentUser.getEmail());
        categoriesCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    categories = (List<Category>) response.body();
                    fillSpinner();
                }else{
                    Toast.makeText(getContext(),"Could not retrieve categories",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(),"Could not connect to server",Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

        vSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == categories.size()){
                    //create an alert dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final LayoutInflater inflater = getActivity().getLayoutInflater();
                    //inflate custom dialog
                    final View mView = inflater.inflate(R.layout.dialog_add_category, null);


                    final ImageView vImageviewAddCategory = mView.findViewById(R.id.imageview_dialog_add_category_color);
                    vImageViewCategory.setBackgroundColor(Color.parseColor("#2CA49D"));

                    vImageviewAddCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ColorPickerDialogBuilder
                                    .with(getContext())
                                    .setTitle(R.string.choose_color)
                                    .initialColor(Color.parseColor("#2CA49D"))
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(8)
                                    .setPositiveButton("ok", new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int color, Integer[] colors) {
                                            vImageviewAddCategory.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(color)));

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
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i("event","cancel category");
                                }
                            }).show();
                }else{
                    ColorDrawable color = new ColorDrawable(Color.parseColor(categories.get(i).getColor()));
                    vImageViewCategory.setBackground(color);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agenda_edit_item,container,false);
    }

    private void fillSpinner(){
        List<String> categorynames = new ArrayList<>();
        for (Category c: categories) {
            categorynames.add(c.getType());
        }
        categorynames.add(getResources().getString(R.string.new_category));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,categorynames);
        vSpinnerCategory.setAdapter(categoryAdapter);
    }
}
