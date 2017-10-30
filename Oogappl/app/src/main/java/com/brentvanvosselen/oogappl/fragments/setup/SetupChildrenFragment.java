package com.brentvanvosselen.oogappl.fragments.setup;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.Child;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class SetupChildrenFragment extends Fragment {

    final String DATE_FORMAT = "dd/MM/yyyy";
    final String[] GENDERS = {"MAN","VROUW"};

    public interface OnEndSelected{
        public void onEndSetup(List<Child> children);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Calendar myCalendar = Calendar.getInstance();

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View child = inflater.inflate(R.layout.setup_child,null);
        final ViewGroup main = getView().findViewById(R.id.linearlayout_setup_children);
        //spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,GENDERS);
        Spinner s = (Spinner) child.findViewById(R.id.spinner_setup_child_gender);
        s.setAdapter(genderAdapter);
        //add child
        main.addView(child);

        ImageButton vButtonAddChild = getView().findViewById(R.id.imagebutton_add_child);
        vButtonAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View newChild = inflater.inflate(R.layout.setup_child,null);
                ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,GENDERS);
                Spinner s = newChild.findViewById(R.id.spinner_setup_child_gender);
                s.setAdapter(genderAdapter);
                main.addView(newChild);


                final EditText edittext2 = (EditText)newChild.findViewById(R.id.edittext_setup_child_birthdate);
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(myCalendar,edittext2);
                    }
                };




                edittext2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            DatePickerDialog dialog =  new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                            dialog.getDatePicker().setMaxDate(new Date().getTime());
                            dialog.show();
                        }
                        return true;
                    }

                });

            }
        });


        Button vButtonEndSetup = getView().findViewById(R.id.button_end_setup);
        vButtonEndSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Child> children = new ArrayList<>();
                boolean correctForm = true;
                for(int i = 0; i< main.getChildCount(); i++){
                    Spinner vSpinnerGender = main.getChildAt(i).findViewById(R.id.spinner_setup_child_gender);
                    //EditText vEdittextGender = main.getChildAt(i).findViewById(R.id.edittext_setup_child_gender);
                    EditText vEdittextFirstname = main.getChildAt(i).findViewById(R.id.edittext_setup_child_firstname);
                    EditText vEdittextLastname = main.getChildAt(i).findViewById(R.id.edittext_setup_child_lastname);
                    EditText vEdittextBirthdate = main.getChildAt(i).findViewById(R.id.edittext_setup_child_birthdate);

                    //String gender = vEdittextGender.getText().toString();
                    String gender = vSpinnerGender.getSelectedItem().toString();
                    switch(gender){
                        case "MAN":
                            gender = "M";
                            break;
                        case "VROUW":
                            gender = "F";
                            break;
                    }
                    String firstname = vEdittextFirstname.getText().toString();
                    String lastname = vEdittextLastname.getText().toString();

                    if(firstname.trim().equals("") || firstname.trim().length() < 3 ){
                        vEdittextFirstname.setError("De voornaam moet minstens 3 karakters bevatten");
                        correctForm = false;
                    }
                    if(lastname.trim().equals("") || lastname.trim().length() < 3 ){
                        vEdittextLastname.setError("De achternaam moet minstens 3 karakters bevatten");
                        correctForm = false;
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date birthdate = new Date();
                    try{
                        birthdate = dateFormat.parse(vEdittextBirthdate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    children.add(new Child(firstname,lastname,gender,birthdate));
                }

                if(correctForm){
                    OnEndSelected mCallback = (OnEndSelected) getActivity();
                    mCallback.onEndSetup(children);
                }



            }
        });




        final EditText edittext = (EditText)getView().findViewById(R.id.edittext_setup_child_birthdate);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar,edittext);
            }
        };




        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    DatePickerDialog dialog =  new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                    dialog.show();
                }
                return true;
            }

        });

    }


    private void updateLabel(Calendar cal,EditText editText){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        //EditText edittext = (EditText)getView().findViewById(R.id.edittext_setup_child_birthdate);
        editText.setText(sdf.format(cal.getTime()));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_children,container,false);
    }
}
