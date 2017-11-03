package com.brentvanvosselen.oogappl.fragments.main;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
<<<<<<< HEAD:Oogappl/app/src/main/java/com/brentvanvosselen/oogappl/fragments/ChildInfoFragment.java
import com.brentvanvosselen.oogappl.RestClient.ChildinfoCategory;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.Info;
import com.brentvanvosselen.oogappl.RestClient.Parent;
=======
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Info;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
>>>>>>> c404d82ee1e6d4b3f1c75bcf54c42e5590a7f985:Oogappl/app/src/main/java/com/brentvanvosselen/oogappl/fragments/main/ChildInfoFragment.java
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoFragment extends Fragment {

    final String DATE_FORMAT = "dd/MM/yyyy";
    final String[] GENDERS = {"MAN","VROUW"};

    private Parent parent;
    private int selectedChild;

    private ViewGroup main;

    EditText vEdittextBirthdate;

    public ChildInfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.childinfo);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mainView = inflater.inflate(R.layout.fragment_childinfo, null);
        main = getView().findViewById(R.id.linearLayout_childinfo_child);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_child, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_child) {
            //create an alert dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            //inflate custom dialog
            final View mView = inflater.inflate(R.layout.dialog_add_child, null);

            //final EditText vEdittextGender = mView.findViewById(R.id.edittext_add_child_gender);
            final Spinner vSpinnerGender = mView.findViewById(R.id.spinner_add_child_gender);
            final EditText vEdittextFirstname = mView.findViewById(R.id.edittext_add_child_firstname);
            final EditText vEdittextLastname = mView.findViewById(R.id.edittext_add_child_lastname);

            //setup spinner
            ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,GENDERS);
            vSpinnerGender.setAdapter(genderAdapter);

            vEdittextBirthdate = mView.findViewById(R.id.edittext_add_child_birthdate);
            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        DatePickerDialog dialog = new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
                            boolean correctform = true;
                            //parse date
                            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                            Date birthdate = new Date();
                            try {
                                birthdate = dateFormat.parse(vEdittextBirthdate.getText().toString());
                            } catch (ParseException e) {
                                Log.i("DATE", e.getMessage());
                            }

                            String gender = vSpinnerGender.getSelectedItem().toString();
                            switch(gender){
                                case "MAN":
                                    gender = "M";
                                    break;
                                case "VROUW":
                                    gender = "F";
                                    break;
                            }

                            //create new child
                            String firstname = vEdittextFirstname.getText().toString();
                            String lastname = vEdittextLastname.getText().toString();
                            if(firstname.trim().equals("") || firstname.trim().length() < 3 ){
                                vEdittextFirstname.setError("De voornaam moet minstens 3 karakters bevatten");
                                correctform = false;
                            }
                            if(lastname.trim().equals("") || lastname.trim().length() < 3 ){
                                vEdittextLastname.setError("De achternaam moet minstens 3 karakters bevatten");
                                correctform = false;
                            }

                            //final Child child = new Child(firstname, lastname, gender, birthdate);



                            final Child child = new Child(firstname, lastname, gender, birthdate);
                            //get user from localstorage
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
                            User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser", null));
                            //create call to save child if correctform
                            if(correctform) {
                                Call callUser = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
                                callUser.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if (response.isSuccessful()) {
                                            Parent parent = (Parent) response.body();
                                            Call callChild = RetrofitClient.getClient().create(APIInterface.class).addChild(parent.getId(), child);
                                            callChild.enqueue(new Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(getContext(), "New child created", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Not saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialogInterface.dismiss();
                                                    initFragment();
                                                }

                                                @Override
                                                public void onFailure(Call call, Throwable t) {
                                                    Toast.makeText(getContext(), "Failed1", Toast.LENGTH_SHORT).show();
                                                    dialogInterface.dismiss();
                                                    initFragment();
                                                }
                                            });
                                        } else {
                                            Log.i("CHILD CALL", "FAIL");
                                            Toast.makeText(getContext(), "Not saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        Log.i("PARENT CALL", "FAIL");
                                        Toast.makeText(getContext(), "Failed2", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            /*else {
                                Log.i("FORM", "INCORRECT FORM");
                            }*/ else{
                                Toast.makeText(getContext(),"Kan niet toevoegen: ongeldige gegevens", Toast.LENGTH_SHORT).show();
                            }
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

    private void updateLabel(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        vEdittextBirthdate.setText(sdf.format(cal.getTime()));
    }

    private void initFragment() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser", null));
        Call call = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    parent = (Parent) response.body();
                    initSpinner();
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

        ImageButton addCat = getView().findViewById(R.id.imageButton_childinfo_add_category);
        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View mView = getActivity().getLayoutInflater().inflate(R.layout.childinfo_category_add, null);

                final EditText name = mView.findViewById(R.id.editText_category_add_name);

                builder.setView(mView)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                parent.getChildren()[selectedChild].addCategory(name.getText().toString());
                                initCategories();
                                saveChanges();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });
    }

    private void initSpinner() {
        ArrayAdapter<String> childAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getChildNames());
        Spinner s = getView().findViewById(R.id.spinner_child);
        s.setAdapter(childAdapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChild = i;
                initCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCategories() {
        final Child[] children = parent.getChildren();

        ViewGroup categoryLayout = getView().findViewById(R.id.linearLayout_childinfo_child);
        categoryLayout.removeAllViews();
        final Child selectedChild = children[this.selectedChild];
        List<ChildinfoCategory> categories = selectedChild.getCategory();

        for (final ChildinfoCategory c : categories) {
            CardView cat = (CardView) getActivity().getLayoutInflater().inflate(R.layout.childinfo_category, null);
            TextView title = cat.findViewById(R.id.textView_catName);
            title.setText(c.getName());

            ImageButton buttonEdit = cat.findViewById(R.id.imageButton_category_edit);
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChildInfoEditFragment fragment = new ChildInfoEditFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("category", ObjectSerializer.serialize2(c));
                    bundle.putString("child", ObjectSerializer.serialize2(selectedChild));
                    fragment.setArguments(bundle);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, fragment, "CURRENT_FRAGMENT");
                    ft.commit();
                }
            });

            ImageButton buttonRemove = cat.findViewById(R.id.imageButton_category_remove);
            buttonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedChild.removeCategory(c);
                    saveChanges();
                    initCategories();
                }
            });

            categoryLayout.addView(cat);

            initItems(cat, c);
        }
    }

    private void initItems(CardView cat, ChildinfoCategory c) {
        List<Info> items = c.getInfo();
        ViewGroup categoryItems = cat.findViewById(R.id.linearLayout_catItems);

        for (Info i : items) {
            LinearLayout item = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.childinfo_item, null);
            TextView name = item.findViewById(R.id.textView_item_name);
            name.setText(i.getName());
            TextView value = item.findViewById(R.id.textView_item_value);
            value.setText(i.getValue());
            categoryItems.addView(item);
        }
    }

    private ArrayList<String> getChildNames() {
        Child[] children = parent.getChildren();

        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < children.length; i++) {
            String temp = children[i].getFirstname() + " " + children[i].getLastname();
            names.add(temp);
        }

        return names;
    }

    private void saveChanges() {
        Call call = RetrofitClient.getClient().create(APIInterface.class).updateChild(parent.getChildren()[selectedChild]);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Log.i("SAVE", "Save succesful");
                } else {
                    Toast.makeText(getContext(), "Cannot find child", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}