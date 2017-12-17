package com.brentvanvosselen.oogappl.fragments.heenenweer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.ChildrenHorizontalPickerAdapter;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;

/**
 * Created by brentvanvosselen on 25/11/2017.
 */

public class HeenEnWeerAddFragment extends Fragment{

    private EditText vEdittextDate, vEdittextDescription;
    private Button vButtonSave;
    private RecyclerView vRecyclerChildren;
    private PickerLayoutManager pickerLayoutManagerChildren;
    private ChildrenHorizontalPickerAdapter mChildrenAdapter;


    private List<Child> children;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private Calendar myCalendar = Calendar.getInstance();

    private APIInterface apiInterface;
    SharedPreferences sharedPreferences;

    private User currentUser;
    private int selectedChild;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.add);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        vEdittextDate = getView().findViewById(R.id.edittext_heenenweer_add_date);
        vEdittextDescription = getView().findViewById(R.id.edittext_heenenweer_add_description);
        vButtonSave = getView().findViewById(R.id.button_heenenweer_add_save);

        //datepicker
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                vEdittextDate.setText(DATE_FORMAT.format(myCalendar.getTime()));
            }
        };

        vEdittextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //set date of textfield in datepicker
                    try {
                        myCalendar.setTime(DATE_FORMAT.parse(vEdittextDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog dialog = new DatePickerDialog(getContext(),dateListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }
                return true;
            }
        });

        Call parentCall = apiInterface.getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());
        parentCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent p = (Parent) response.body();
                    children = Arrays.asList(p.getChildren());

                    vRecyclerChildren = getView().findViewById(R.id.recycler_heenenweer_add_children);

                    pickerLayoutManagerChildren = new PickerLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    pickerLayoutManagerChildren.setChangeAlpha(true);
                    pickerLayoutManagerChildren.setScaleDownBy(0.7f);
                    pickerLayoutManagerChildren.setScaleDownDistance(0.8f);

                    mChildrenAdapter = new ChildrenHorizontalPickerAdapter(getContext(),children,vRecyclerChildren,false,false);

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
                Snackbar.make(getView(),"Kon server niet bereiken",Snackbar.LENGTH_SHORT).show();
            }
        });

        vButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctform = true;
                String description = vEdittextDescription.getText().toString();
                Date day = null;
                Child child = children.get(selectedChild);
                try {
                    day = DATE_FORMAT.parse(vEdittextDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(day == null){
                    vEdittextDate.setError("Geen datum");
                    correctform = false;
                }
                if(correctform){
                    HeenEnWeerDag newDay = new HeenEnWeerDag(day,description,child);
                    Call addDayCall = apiInterface.addHeenEnWeerDay("bearer " + sharedPreferences.getString("token",null), newDay);
                    addDayCall.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()){
                                Snackbar.make(getView(),"Dag toegevoegd",Snackbar.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }else{
                                Snackbar.make(getView(),"Dag niet toegevoegd",Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Snackbar.make(getView(),"Kon server niet bereiken",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heenenweer_add,container,false);
    }
}
