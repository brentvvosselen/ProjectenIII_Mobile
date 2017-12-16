package com.brentvanvosselen.oogappl.fragments.calendar;

import android.annotation.SuppressLint;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.CategoriesHorizontalPickerAdapter;
import com.brentvanvosselen.oogappl.adapters.ChildrenHorizontalPickerAdapter;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;

/**
 * Created by brentvanvosselen on 05/11/2017.
 */

public class AgendaEditItemFragment extends Fragment {

    final String DATE_FORMAT = "dd/MM/yyyy";
    final String TIME_FORMAT = "HH:mm";
    final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    //use this variable to edit an event, if this is null you want to create an event
    private String itemId = null;

    private int selectedChild = 0;
    private int selectedCategory = 0;

    APIInterface apiInterface;
    SharedPreferences sharedPreferences;
    User currentUser;

    EditText vEdittextTitle, vEdittextDescription, vEdittextStartDate, vEdittextEndDate, vEdittextStartTime, vEdittextEndTime, vEdittextWederkerendEinddatum;
    Button vButtonSave;
    CircularImageView vImageViewCategory;
    Spinner vSpinnerWederkerendFrequenty;
    CheckBox vCheckboxWederkerend;
    TextView vTextViewWederkerendEinddatum;

    RecyclerView vRecyclerChildren, vRecyclerCategories;
    ChildrenHorizontalPickerAdapter mChildrenAdapter;
    CategoriesHorizontalPickerAdapter mCategoriesAdapter;
    PickerLayoutManager pickerLayoutManagerChildren;
    PickerLayoutManager pickerLayoutManagerCategories;

    List<Category> categories;
    List<Child> children;
    String[] frequenties = {"Dagelijks", "Wekelijks", "Maandelijks"};

    String currentColor = "#2CA49D";


    //method to create a new instance and fill the currentEvent
    //you only need to use this if you want to edit an event
    public static AgendaEditItemFragment newInstance(String eventId) {
        AgendaEditItemFragment fragment = new AgendaEditItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", eventId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        final Calendar myCalendar = Calendar.getInstance();

        //set title
        final TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.add_item);

        //make a call for the categories and fill the adapter
        fillSpinner();
        fillChildSpinner();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //get event
        if(itemId != null){
            Call itemCall = apiInterface.getEvent("bearer " + sharedPreferences.getString("token",null), itemId);
            itemCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        Event e = (Event) response.body();
                        title.setText(R.string.edit_item);


                        vEdittextTitle.setText(e.getTitle());
                        vEdittextDescription.setText(e.getDescription());
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
                        vEdittextStartDate.setText(dateFormat.format(e.getStart()));
                        vEdittextStartTime.setText(timeFormat.format(e.getStart()));
                        vEdittextEndDate.setText(dateFormat.format(e.getEnd()));
                        vEdittextEndTime.setText(timeFormat.format(e.getEnd()));
                        vButtonSave.setText(R.string.save);
                        int categoryIndex = -1;
                        for (int i = 0; i < categories.size(); i++) {
                            if (categories.get(i).getType().equals(e.getCategory().getType()))
                                categoryIndex = i;
                        }
                        vRecyclerCategories.scrollToPosition(categoryIndex);

                        int childIndex = -1;
                        if(e.getchildren().length > 0){
                            for(int i = 0; i <  children.size(); i ++){
                                if(children.get(i).get_id().equals(e.getchildren()[0].get_id())){
                                    childIndex = i;
                                }
                            }
                            if(e.getchildren().length == children.size()){
                                childIndex = children.size();
                            }
                        }


                        vRecyclerChildren.scrollToPosition(childIndex + 1);
                    } else {
                        Toast.makeText(getContext(), R.string.get_event_neg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                    call.cancel();
                }
            });
        }

        vEdittextTitle = getView().findViewById(R.id.edittext_edit_event_title);
        vEdittextDescription = getView().findViewById(R.id.edittext_edit_event_description);
        vEdittextStartDate = getView().findViewById(R.id.edittext_edit_event_startDate);
        vEdittextEndDate = getView().findViewById(R.id.edittext_edit_event_endDate);
        vImageViewCategory = getView().findViewById(R.id.imageview_edit_event_category);
        vButtonSave = getView().findViewById(R.id.button_edit_event_save);
        vEdittextStartTime = getView().findViewById(R.id.edittext_edit_event_startTime);
        vEdittextEndTime = getView().findViewById(R.id.edittext_edit_event_endTime);
        vCheckboxWederkerend = getView().findViewById(R.id.checkBox_wederkerend);
        vSpinnerWederkerendFrequenty = getView().findViewById(R.id.spinner_wederkerend_frequenty);
        vTextViewWederkerendEinddatum = getView().findViewById(R.id.textview_wederkerend_enddate);
        vEdittextWederkerendEinddatum = getView().findViewById(R.id.editText_wederkerend_einddatum);






        ArrayAdapter<String> frequentyArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, this.frequenties);
        vSpinnerWederkerendFrequenty.setAdapter(frequentyArrayAdapter);
        vSpinnerWederkerendFrequenty.setVisibility(View.GONE);


        final DatePickerDialog.OnDateSetListener dateStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(DATE_FORMAT, myCalendar, vEdittextStartDate);
            }
        };

        vEdittextStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //set date of textfield in datepicker
                    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    try {
                        myCalendar.setTime(format.parse(vEdittextStartDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), dateStartListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMinDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }
        });

        final DatePickerDialog.OnDateSetListener dateEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(DATE_FORMAT, myCalendar, vEdittextEndDate);
            }
        };

        vEdittextEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //set date of textfield in datepicker
                    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    try {
                        myCalendar.setTime(format.parse(vEdittextEndDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), dateEndListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_WEEK));
                    dialog.getDatePicker().setMinDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }
        });

        final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hours);
                myCalendar.set(Calendar.MINUTE, minutes);
                updateLabel(TIME_FORMAT, myCalendar, vEdittextStartTime);
            }
        };

        vEdittextStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //set date of textfield in datepicker
                    SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
                    try {
                        myCalendar.setTime(format.parse(vEdittextStartTime.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TimePickerDialog dialog = new TimePickerDialog(getContext(), timeStartListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                    dialog.show();
                }
                return true;
            }
        });

        final TimePickerDialog.OnTimeSetListener timeEndListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hours);
                myCalendar.set(Calendar.MINUTE, minutes);
                updateLabel(TIME_FORMAT, myCalendar, vEdittextEndTime);
            }
        };

        vEdittextEndTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //set date of textfield in datepicker
                    SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
                    try {
                        myCalendar.setTime(format.parse(vEdittextEndTime.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    TimePickerDialog dialog = new TimePickerDialog(getContext(), timeEndListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                    dialog.show();
                }
                return true;
            }
        });

        final DatePickerDialog.OnDateSetListener dateWederkerendEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel(DATE_FORMAT, myCalendar, vEdittextWederkerendEinddatum);
            }
        };

        vEdittextWederkerendEinddatum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //set date of textfield in datepicker
                    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                    try {
                        myCalendar.setTime(format.parse(vEdittextWederkerendEinddatum.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), dateWederkerendEndListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_WEEK));
                    dialog.getDatePicker().setMinDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }
        });

        vEdittextWederkerendEinddatum.setVisibility(View.GONE);
        vTextViewWederkerendEinddatum.setVisibility(View.GONE);

        vCheckboxWederkerend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    vSpinnerWederkerendFrequenty.setVisibility(View.VISIBLE);
                    vEdittextWederkerendEinddatum.setVisibility(View.VISIBLE);
                    vTextViewWederkerendEinddatum.setVisibility(View.VISIBLE);
                } else {
                    vSpinnerWederkerendFrequenty.setVisibility(View.GONE);
                    vEdittextWederkerendEinddatum.setVisibility(View.GONE);
                    vTextViewWederkerendEinddatum.setVisibility(View.GONE);
                }
            }
        });

        vButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctForm = true;


                String title = vEdittextTitle.getText().toString();
                String description = vEdittextDescription.getText().toString();
                Category category = null;
                if(selectedCategory == categories.size()){
                    correctForm = false;
                    Snackbar.make(getView(),"Geen categorie geselecteerd",Snackbar.LENGTH_SHORT).show();
                }else{
                    category = categories.get(selectedCategory);
                }

                List<Child> myChildren = new ArrayList<>();

                if(selectedChild == children.size() + 1){
                    myChildren = children;
                }else if (selectedChild > 0 && selectedChild <= children.size()){
                    myChildren.add(children.get(selectedChild - 1));
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                Date start = new Date();
                Date end = new Date();
                try {
                    start = dateFormat.parse(vEdittextStartDate.getText().toString() + " " + vEdittextStartTime.getText().toString());
                    end = dateFormat.parse(vEdittextEndDate.getText().toString() + " " + vEdittextEndTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.err_start_end_event,Toast.LENGTH_SHORT).show();
                    correctForm = false;
                }


                if(title.isEmpty() || title == null){
                    correctForm = false;
                    vEdittextTitle.setError( getResources().getString(R.string.err_title_event));
                }
                if(category == null){
                    correctForm = false;
                }

                if(start == null || end == null){
                    correctForm = false;
                }else if(start.after(end)){
                    correctForm = false;
                }

                if(correctForm){


                    Child[] childs = new Child[myChildren.size()];
                    for(int i = 0; i< myChildren.size();i++){
                        childs[i] = myChildren.get(i);
                    }
                    Event newEvent = new Event(title,start,end,description,category, childs);
                    if(itemId != null){
                        //edit event
                        Call editEventCall = apiInterface.editEvent("bearer " + sharedPreferences.getString("token",null), itemId,newEvent);
                        editEventCall.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getContext(), R.string.change_event_pos,Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }else{
                                    Toast.makeText(getContext(), R.string.change_event_neg,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getContext(), R.string.geen_verbinding,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        //add event
                        Call addEventCall = apiInterface.addEvent("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(),newEvent);
                        addEventCall.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getContext(), R.string.add_event_pos,Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();

                                }else{
                                    Toast.makeText(getContext(), R.string.add_event_neg,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(getContext(),R.string.geen_verbinding,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    Log.i("FORM","not correct");
                }

            }
        });
    }

    private void fillChildSpinner() {
        Call parentCall = apiInterface.getParentByEmail("bearer " + sharedPreferences.getString("token",null),currentUser.getEmail());
        parentCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){
                    Parent p = (Parent) response.body();
                    children = Arrays.asList(p.getChildren());

                    vRecyclerChildren = getView().findViewById(R.id.recycler_agenda_edit_children);

                    pickerLayoutManagerChildren = new PickerLayoutManager(getContext(),PickerLayoutManager.HORIZONTAL,false);
                    pickerLayoutManagerChildren.setChangeAlpha(true);
                    pickerLayoutManagerChildren.setScaleDownBy(0.7f);
                    pickerLayoutManagerChildren.setScaleDownDistance(0.8f);


                    mChildrenAdapter = new ChildrenHorizontalPickerAdapter(getContext(),children,vRecyclerChildren,true,true);

                    SnapHelper snapHelper = new LinearSnapHelper();
                    snapHelper.attachToRecyclerView(vRecyclerChildren);

                    vRecyclerChildren.setLayoutManager(pickerLayoutManagerChildren);
                    vRecyclerChildren.setAdapter(mChildrenAdapter);

                    pickerLayoutManagerChildren.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
                        @Override
                        public void selectedView(View view) {
                            selectedChild = pickerLayoutManagerChildren.getPosition(view);

                        }
                    });



                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get event from arguments
        try {
            itemId = (String) getArguments().getSerializable("event");
        } catch (NullPointerException ex) {
            Log.i("event", "none");
        } catch (Exception e) {
            Log.i("event", "none");
        }

        return inflater.inflate(R.layout.fragment_agenda_edit_item, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_delete){
            final User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

            Call deleteEvent = apiInterface.deleteEvent("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(),itemId);
            deleteEvent.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.del_event_pos, Toast.LENGTH_SHORT).show();
                        AgendaFragment.OnCalendarItemSelected mCallback = (AgendaFragment.OnCalendarItemSelected) getActivity();
                        mCallback.onItemDeleted();
                    } else {
                        Toast.makeText(getContext(), R.string.del_event_neg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    call.cancel();
                    Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }

    private void fillSpinner() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        final User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser", null));


        Call categoriesCall = apiInterface.getCategoriesFromUser("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        categoriesCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    categories = (List<Category>) response.body();

                    //picker
                    vRecyclerCategories = getView().findViewById(R.id.recycler_agenda_edit_categories);

                    pickerLayoutManagerCategories = new PickerLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                    pickerLayoutManagerCategories.setChangeAlpha(true);
                    pickerLayoutManagerCategories.setScaleDownBy(0.8f);
                    pickerLayoutManagerCategories.setScaleDownDistance(0.9f);

                    mCategoriesAdapter = new CategoriesHorizontalPickerAdapter(getContext(),categories,vRecyclerCategories,getActivity().getSupportFragmentManager().findFragmentById(R.id.content_main),"agenda_edit");

                    SnapHelper snapHelper = new LinearSnapHelper();
                    vRecyclerCategories.setOnFlingListener(null);
                    snapHelper.attachToRecyclerView(vRecyclerChildren);

                    vRecyclerCategories.setLayoutManager(pickerLayoutManagerCategories);
                    vRecyclerCategories.setAdapter(mCategoriesAdapter);

                    pickerLayoutManagerCategories.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
                        @Override
                        public void selectedView(View view) {
                            selectedCategory = pickerLayoutManagerCategories.getPosition(view);
                            Log.i("selected",String.valueOf(selectedCategory));
                        }
                    });


                    //
                } else {
                    Toast.makeText(getContext(), R.string.get_category_neg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void updateLabel(String format, Calendar calendar, EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        editText.setText(sdf.format(calendar.getTime()));
    }

    public void rerenderCategories(List<Category> categories){
        Log.i("rerender","true");
        mCategoriesAdapter = new CategoriesHorizontalPickerAdapter(getContext(),categories,vRecyclerCategories,this,"agenda_edit");
        vRecyclerCategories.setAdapter(mCategoriesAdapter);

    }
}
