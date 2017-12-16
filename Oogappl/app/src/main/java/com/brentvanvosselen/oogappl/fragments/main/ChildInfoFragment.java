package com.brentvanvosselen.oogappl.fragments.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Base64;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.models.Image;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.ChildinfoCategory;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Info;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    private final int PICTURE_REQUEST = 1;

    final String DATE_FORMAT = "dd/MM/yyyy";
    final String[] GENDERS = {"MAN","VROUW"};

    private Parent parent;
    private int selectedChild;

    APIInterface apiInterface;
    SharedPreferences sharedPreferences;

    private ViewGroup main;

    EditText vEdittextBirthdate;
    private CircularImageView vImageViewPicture;

    public ChildInfoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        vImageViewPicture = getView().findViewById(R.id.imageview_child_info_picture);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.childinfo);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mainView = inflater.inflate(R.layout.fragment_childinfo, null);
        main = getView().findViewById(R.id.linearLayout_childinfo_child);
        main.addView(mainView);

        initFragment();


        vImageViewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getView(), R.string.new_picture_press, Snackbar.LENGTH_SHORT).show();
            }
        });

        vImageViewPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(takePictureIntent,PICTURE_REQUEST);
                }
                return true;
            }
        });
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
                            if(firstname.trim().equals("")){
                                vEdittextFirstname.setError(getResources().getString(R.string.err_firstname_empty));
                                correctform = false;
                            }
                            if(lastname.trim().equals("")){
                                vEdittextLastname.setError(getResources().getString(R.string.err_lastname_empty));
                                correctform = false;
                            }

                            //final Child child = new Child(firstname, lastname, gender, birthdate);



                            final Child child = new Child(firstname, lastname, gender, birthdate);
                            //get user from localstorage
                            User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser", null));
                            //create call to save child if correctform
                            if(correctform) {
                                Call callUser = apiInterface.getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());
                                callUser.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if (response.isSuccessful()) {
                                            Parent parent = (Parent) response.body();
                                            Call callChild = apiInterface.addChild("bearer " + sharedPreferences.getString("token",null), parent.getId(), child);
                                            callChild.enqueue(new Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(getContext(), R.string.new_child_pos, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), R.string.new_child_neg, Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialogInterface.dismiss();
                                                    initFragment();
                                                }

                                                @Override
                                                public void onFailure(Call call, Throwable t) {
                                                    Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                                                    dialogInterface.dismiss();
                                                    initFragment();
                                                }
                                            });
                                        } else {
                                            Log.i("CHILD CALL", "FAIL");
                                            Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        Log.i("PARENT CALL", "FAIL");
                                        Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
                                Toast.makeText(getContext(),R.string.err_incomplete_form, Toast.LENGTH_SHORT).show();
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
        Call call = RetrofitClient.getClient(getContext()).create(APIInterface.class).getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    parent = (Parent) response.body();
                    initSpinner();
                    initCategories();
                } else {
                    Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN", "FAIL: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<String> childAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_blue, getChildNames());
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

        if(children.length > 0) {
            final Child selectedChild = children[this.selectedChild];
            List<ChildinfoCategory> categories = selectedChild.getCategory();

            if(selectedChild.getPicture() != null){
                byte[] decodedString = Base64.decode(selectedChild.getPicture().getValue(),Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                vImageViewPicture.setImageBitmap(decodedByte);
            }else{
                Bitmap image = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(),R.drawable.no_picture);
                vImageViewPicture.setImageBitmap(image);
            }

            for (final ChildinfoCategory c : categories) {
                RelativeLayout cat = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.childinfo_category, null);
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
    }

    private void initItems(RelativeLayout cat, ChildinfoCategory c) {
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
        Call call = apiInterface.updateChild("bearer " + sharedPreferences.getString("token",null), parent.getChildren()[selectedChild]);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Log.i("SAVE", "Save succesful");
                } else {
                    Toast.makeText(getContext(), R.string.get_child_neg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_REQUEST && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,out);
            Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            vImageViewPicture.setImageBitmap(compressed);

            byte[] byteArray = out.toByteArray();
            String value = Base64.encodeToString(byteArray,Base64.DEFAULT);
            String name = String.valueOf(new Date().getTime());
            String type = "image/jpeg";

            Image image = new Image(name,type,value);

            Call changeChildPictureCall = apiInterface.changeChildPicture("bearer " + sharedPreferences.getString("token",null),this.parent.getChildren()[selectedChild].get_id(),image);
            changeChildPictureCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()){
                        Snackbar.make(getView(), R.string.change_picture_pos, Snackbar.LENGTH_SHORT).show();

                    }else{
                        Snackbar.make(getView(), R.string.change_picture_neg, Snackbar.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();

                }
            });

        }
    }
}