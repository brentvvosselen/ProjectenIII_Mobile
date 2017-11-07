package com.brentvanvosselen.oogappl.fragments.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.Console;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 05/11/2017.
 */

public class AgendaEditItemFragment extends Fragment {

    final String DATE_FORMAT = "dd/MM/yyyy";
    final String TIME_FORMAT = "HH:mm";
    final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    EditText vEdittextTitle, vEdittextDescription, vEdittextStartDate, vEdittextEndDate, vEdittextStartTime, vEdittextEndTime;
    Button vButtonSave;
    CircularImageView vImageViewCategory;
    Spinner vSpinnerCategory;

    List<Category> categories;

    String currentColor = "#2CA49D";





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Calendar myCalendar = Calendar.getInstance();

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.add_item);

        vEdittextTitle = getView().findViewById(R.id.edittext_edit_event_title);
        vEdittextDescription = getView().findViewById(R.id.edittext_edit_event_description);
        vEdittextStartDate = getView().findViewById(R.id.edittext_edit_event_startDate);
        vEdittextEndDate = getView().findViewById(R.id.textview_edit_event_endDate);
        vImageViewCategory = getView().findViewById(R.id.imageview_edit_event_category);
        vSpinnerCategory = getView().findViewById(R.id.spinner_edit_event_category);
        vButtonSave = getView().findViewById(R.id.button_edit_event_save);
        vEdittextStartTime = getView().findViewById(R.id.edittext_edit_event_startTime);
        vEdittextEndTime = getView().findViewById(R.id.edittext_edit_event_endTime);



        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        final User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        //make a call for the categories and fill the adapter
        fillSpinner();

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

                                    Call addCategoryCall = apiInterface.addCategory(currentUser.getEmail(),newCategory);
                                    addCategoryCall.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            if(response.isSuccessful()){
                                                Toast.makeText(getContext(),"Categorie toegevoegd.",Toast.LENGTH_SHORT).show();
                                                fillSpinner();
                                            }else{
                                                Toast.makeText(getContext(),"Categorie NIET toegevoegd.",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call call, Throwable t) {
                                            Toast.makeText(getContext(),"Kon geen connectie maken met server",Toast.LENGTH_SHORT).show();
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

        final DatePickerDialog.OnDateSetListener dateStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(DATE_FORMAT,myCalendar,vEdittextStartDate);
            }
        };

        vEdittextStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    DatePickerDialog dialog = new DatePickerDialog(getContext(),dateStartListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMinDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }
        });

        final DatePickerDialog.OnDateSetListener dateEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(DATE_FORMAT,myCalendar,vEdittextEndDate);
            }
        };

        vEdittextEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    DatePickerDialog dialog = new DatePickerDialog(getContext(),dateEndListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_WEEK));
                    dialog.getDatePicker().setMinDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }
        });

        final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                myCalendar.set(Calendar.HOUR_OF_DAY,hours);
                myCalendar.set(Calendar.MINUTE,minutes);
                updateLabel(TIME_FORMAT,myCalendar,vEdittextStartTime);
            }
        };

        vEdittextStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    TimePickerDialog dialog = new TimePickerDialog(getContext(),timeStartListener,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),true);
                    dialog.show();
                }
                return true;
            }
        });

        final TimePickerDialog.OnTimeSetListener timeEndListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                myCalendar.set(Calendar.HOUR_OF_DAY,hours);
                myCalendar.set(Calendar.MINUTE,minutes);
                updateLabel(TIME_FORMAT,myCalendar,vEdittextEndTime);
            }
        };

        vEdittextEndTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    TimePickerDialog dialog = new TimePickerDialog(getContext(),timeEndListener,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),true);
                    dialog.show();
                }
                return true;
            }
        });


        vButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = vEdittextTitle.getText().toString();
                String description = vEdittextDescription.getText().toString();
                Category category = categories.get(vSpinnerCategory.getSelectedItemPosition());
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                Date start = new Date();
                Date end = new Date();
                try {
                    start = dateFormat.parse(vEdittextStartDate.getText().toString() + " " + vEdittextStartTime.getText().toString());
                    end = dateFormat.parse(vEdittextEndDate.getText().toString() + " " + vEdittextEndTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Event newEvent = new Event(title,start,description,category);

                Call addEventCall = apiInterface.addEvent(currentUser.getEmail(),newEvent);
                addEventCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getContext(),"Categorie toegevoegd",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"Categorie niet toegevoegd",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(getContext(),"Kon niet verbinden met server",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agenda_edit_item,container,false);
    }

    private void fillSpinner(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        final User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));


        Call categoriesCall = apiInterface.getCategoriesFromUser(currentUser.getEmail());
        categoriesCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    categories = (List<Category>) response.body();
                    List<String> categorynames = new ArrayList<>();
                    for (Category c: categories) {
                        categorynames.add(c.getType());
                    }
                    categorynames.add(getResources().getString(R.string.new_category));
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,categorynames);
                    vSpinnerCategory.setAdapter(categoryAdapter);
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
    }

    private void updateLabel(String format, Calendar calendar, EditText editText){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        editText.setText(sdf.format(calendar.getTime()));
    }
}
