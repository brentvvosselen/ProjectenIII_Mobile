package com.brentvanvosselen.oogappl.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.ChildInfoView;
import com.brentvanvosselen.oogappl.ObjectSerializer;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.User;
import com.brentvanvosselen.oogappl.activities.MainActivity;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoFragment extends Fragment {

    final String DATE_FORMAT = "dd/MM/yyyy";

    private Parent parent;
    private View mainView;

    EditText vEdittextBirthdate;

    public ChildInfoFragment(){
        setHasOptionsMenu(true);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.childinfo);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mainView =  inflater.inflate(R.layout.fragment_childinfo, null);
        ViewGroup main = getView().findViewById(R.id.linearLayout_childinfo_child);
        main.addView(mainView);

        initFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_childinfo, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*
    public void onResume() {
        super.onResume();
        initFragment();
    }
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_child,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_child){
            //create an alert dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            //inflate custom dialog
            final View mView = inflater.inflate(R.layout.dialog_add_child,null);

            final EditText vEdittextGender = mView.findViewById(R.id.edittext_add_child_gender);
            final EditText vEdittextFirstname = mView.findViewById(R.id.edittext_add_child_firstname);
            final EditText vEdittextLastname = mView.findViewById(R.id.edittext_add_child_lastname);

            vEdittextBirthdate = mView.findViewById(R.id.edittext_add_child_birthdate);
            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){

                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(myCalendar);
                }
            };

            vEdittextBirthdate.setOnTouchListener(new View.OnTouchListener() {
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
            //set the custom dialog to the alertDialogBuilder and add 2 buttons
            builder.setView(mView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            //parse date
                            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                            Date birthdate = new Date();
                            try{
                                birthdate = dateFormat.parse(vEdittextBirthdate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //create new child
                            final Child child = new Child(vEdittextFirstname.getText().toString(),vEdittextLastname.getText().toString(),vEdittextGender.getText().toString(),birthdate);
                            //get user from localstorage
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
                            User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
                            //create call to save child
                            Call callUser = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
                            callUser.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if(response.isSuccessful()){
                                        Parent parent = (Parent) response.body();
                                        Call callChild = RetrofitClient.getClient().create(APIInterface.class).addChild(parent.getId(),child);
                                        callChild.enqueue(new Callback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                if(response.isSuccessful()){
                                                    Toast.makeText(getContext(),"New child created", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(),"Not saved", Toast.LENGTH_SHORT).show();
                                                }
                                                dialogInterface.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call call, Throwable t) {
                                                Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getContext(),"Not saved", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLabel(Calendar cal){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        vEdittextBirthdate.setText(sdf.format(cal.getTime()));


    }

    private void initFragment() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Call call =  RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    parent = (Parent) response.body();
                    initCategories();
                } else {
                    Toast.makeText(getContext(), "Call failed", Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN", "FAIL: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void initCategories() {
        Child[] children = parent.getChildren();
        ArrayAdapter<String> childAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getChildNames(children));
        Spinner s = mainView.findViewById(R.id.spinner_child);
        s.setAdapter(childAdapter);
    }

    private String[] getChildNames(Child[] children) {
        String[] names = new String[children.length];

        for (int i = 0; i < children.length; i++) {
            String temp = children[i].getFirstname() + " " + children[i].getLastname();
            names[i] = temp;
        }

        return names;
    }
}



