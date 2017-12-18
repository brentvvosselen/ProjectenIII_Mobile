package com.brentvanvosselen.oogappl.fragments.finance;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Cost;
import com.brentvanvosselen.oogappl.RestClient.models.CostCategory;
import com.brentvanvosselen.oogappl.RestClient.models.Costbill;
import com.brentvanvosselen.oogappl.RestClient.models.FinancialType;
import com.brentvanvosselen.oogappl.RestClient.models.Image;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.activities.FinanceSetupActivity;
import com.brentvanvosselen.oogappl.fragments.main.ProfileFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinanceFragment extends Fragment {

    APIInterface apiInterface;
    SharedPreferences sharedPreferences;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private User currentUser;
    private Parent parent;
    private FinancialType type;
    private List<Cost> costs = new ArrayList<>();
    private double toPay;
    private List<CostCategory> categories;
    private List<String> categorieNames;

    private CardView vCardSetup;
    private CardView vCardTotal;
    private RecyclerView vCostList;
    private CostItemAdapter mAdapter;

    private Image image;
    Button addPicture;
    android.app.AlertDialog dialog;
    private int GALLERY_REQUEST = 1;
    private int CAMERA_REQUEST = 2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.finance);

        vCardSetup = getView().findViewById(R.id.card_finance_setup);
        vCardTotal = getView().findViewById(R.id.card_finance_total);
        vCostList = getView().findViewById(R.id.recyclerView_cost_list);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Call call = RetrofitClient.getClient(getContext()).create(APIInterface.class).getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());
        //progress
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.getting_data));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    parent = (Parent) response.body();
                    type = parent.getGroup().getFinancialType();

                    if(parent.getGroup().bothParentsAccepted()) {
                        // Beide parents hebben de financien geaccepteerd
                        bothAccepted();

                    } else if(parent.getGroup().parentHasAccepted(parent)) {
                        // Ene parent heeft setup doorlopen, andere nog niet
                        currentAccepted();

                    } else if(parent.getGroup().otherParentHasAccepted(parent)) {
                        // Andere parent heeft setup doorlopen, huidige nog niet
                        otherAccepted();

                    } else if(parent.hasDoneSetup()){
                        //Indien de gebruiker de financien setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
                        noneAccepted();
                    }
                }
                progressDialog.setProgress(50);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(),  R.string.get_parent_neg, Snackbar.LENGTH_SHORT).show();
            }
        });

        Call call2 = apiInterface.getAllCostCategories("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        call2.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    categorieNames = new ArrayList<String>();
                    categories = (List<CostCategory>) response.body();
                    for(CostCategory c : categories) {
                        categorieNames.add(c.getType());
                    }
                    categorieNames.add( getResources().getString(R.string.new_category_plus));
                } else {
                    Snackbar.make(getView(), getResources().getString(R.string.geen_verbinding), Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(), getResources().getString(R.string.geen_verbinding), Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finance,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            createDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void bothAccepted() {
        vCardSetup.setVisibility(View.GONE);
        setHasOptionsMenu(true);

        Call call = apiInterface.getCostbill("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    Costbill temp = (Costbill) response.body();
                    costs = temp.getCostsMonth();
                    toPay = temp.getToPay();

                    initCostscards();
                } else {
                    Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("ERROR", t.getMessage());
                Snackbar.make(getView(), getResources().getString(R.string.geen_verbinding), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void initCostscards() {
        vCardTotal.setVisibility(View.VISIBLE);
        TextView totalTitle = vCardTotal.findViewById(R.id.textview_finance_total_title);
        TextView totalAmount = vCardTotal.findViewById(R.id.textview_finance_total_amount);
        totalTitle.setText("Totaal te betalen bedrag: ");
        totalAmount.setText("€ " + toPay);

        mAdapter = new CostItemAdapter(costs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        vCostList.setLayoutManager(mLayoutManager);
        vCostList.setItemAnimator(new DefaultItemAnimator());
        vCostList.setAdapter(mAdapter);
    }

    private void createDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        //inflate custom dialog
        final View mView = inflater.inflate(R.layout.dialog_add_cost, null);

        final List<EditText> fields = new ArrayList<>();

        final EditText editTextTitle = mView.findViewById(R.id.editText_cost_title);
        fields.add(editTextTitle);
        final EditText editTextDescription = mView.findViewById(R.id.editText_card_cost_description3);
        fields.add(editTextDescription);
        final EditText editTextAmount = mView.findViewById(R.id.editText_cost_bedrag);
        fields.add(editTextAmount);
        final EditText editTextDate = mView.findViewById(R.id.editText_cost_datum);
        fields.add(editTextDate);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                editTextDate.setText(sdf.format(myCalendar.getTime()));
                editTextDate.setError(null);
            }
        };

        editTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    DatePickerDialog dialog =  new DatePickerDialog(getContext(), date,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }
                return true;
            }
        });

        ArrayAdapter<String>  arrayAdapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, categorieNames);
        final Spinner spinnerCategory = mView.findViewById(R.id.spinner_category);
        spinnerCategory.setAdapter(arrayAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == categorieNames.size() - 1) {
                    createAddCategoryDialog();
                    spinnerCategory.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] nameSelect = this.parent.getGroup().getSelectChildnamesCost();

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, nameSelect);
        final Spinner spinnerChildren = mView.findViewById(R.id.spinner_child);
        spinnerChildren.setAdapter(arrayAdapter1);

        final AlertDialog dialog = builder.setView(mView)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

        addPicture = mView.findViewById(R.id.button_add_bewijs);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button pos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean correct = true;

                        for(EditText e : fields) {
                            if(e.getText().toString().isEmpty()) {
                                e.setError("Error");
                                correct = false;
                            }
                        }

                        if(type == FinancialType.KINDREKENING) {
                            double max = parent.getGroup().getFinType().getKindrekening().getMaxBedrag();
                            if(Double.parseDouble(editTextAmount.getText().toString()) < max) {
                                editTextAmount.setError( getResources().getString(R.string.amount_min) + max);
                                correct = false;
                            }
                        }

                        if(correct) {
                            String title = editTextTitle.getText().toString();
                            String desciption = editTextDescription.getText().toString();
                            double amount = Double.parseDouble(editTextAmount.getText().toString());

                            String date = editTextDate.getText().toString();
                            int day = Integer.parseInt(date.substring(0, 2));
                            int month = Integer.parseInt(date.substring(3, 5)) - 1;
                            int year = Integer.parseInt(date.substring(6, 10)) - 1900;

                            CostCategory category = categories.get(spinnerCategory.getSelectedItemPosition());

                            String childName = (String) spinnerChildren.getSelectedItem();

                            Cost temp = new Cost(title, desciption, amount, new Date(year, month, day), category, parent.getGroup().getChildByName(childName));

                            if(image != null) {
                                Log.i("IMAGE", "ADD IMAGE");
                                temp.setImage(image);
                            }

                            addCost(temp);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void createAddCategoryDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        //inflate custom dialog
        final View mView = inflater.inflate(R.layout.dialog_add_costcategory, null);

        final EditText editTextCatTitle = mView.findViewById(R.id.editText_costcategory_title);

        builder.setView(mView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCat(editTextCatTitle.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void addCat(String catTitle) {
        CostCategory cat = new CostCategory(catTitle);
        Call call = apiInterface.addCategory("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(), cat);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    CostCategory cat = (CostCategory) response.body();
                    categories.add(cat);
                    categorieNames.remove(categorieNames.size() - 1);
                    categorieNames.add(cat.getType());
                    categorieNames.add( getResources().getString(R.string.new_category_plus));
                } else {
                    Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //Toast.makeText(getContext(), getResources().getString(R.string.geen_verbinding), Toast.LENGTH_SHORT).show();
                //call.cancel();
            }
        });
    }

    private void addCost(final Cost c) {
        Call call = apiInterface.addCost("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(), c);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Cost cost = (Cost) response.body();
                    costs.add(cost);

                    initCostscards();
                } else {
                    Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // Toast.makeText(getContext(), getResources().getString(R.string.geen_verbinding), Toast.LENGTH_SHORT).show();
                Log.i("ERROR", t.getMessage());

                costs.add(c);

                call.cancel();
            }
        });
    }

    private void currentAccepted() {
        vCardSetup.setVisibility(View.VISIBLE);

        TextView textViewCardTitle = vCardSetup.findViewById(R.id.textview_title_finance_setup);
        textViewCardTitle.setText(getResources().getString(R.string.financemodule));

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText(R.string.wait_other_parent);


        Button button = vCardSetup.findViewById(R.id.button_finance_setup);
        button.setVisibility(View.GONE);
    }

    private void otherAccepted() {
        vCardSetup.setVisibility(View.VISIBLE);

        TextView textViewCardTitle = vCardSetup.findViewById(R.id.textview_title_finance_setup);
        textViewCardTitle.setText(getResources().getString(R.string.financemodule));

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText(R.string.accept_other);

        Button vButtonSetup = getView().findViewById(R.id.button_finance_setup);
        vButtonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FinanceSetupActivity.class);
                intent.putExtra("parent", ObjectSerializer.serialize2(parent));
                intent.putExtra("accepted", true);
                startActivity(intent);
            }
        });
    }

    private void noneAccepted() {
        vCardSetup.setVisibility(View.VISIBLE);

        TextView textViewCardTitle = vCardSetup.findViewById(R.id.textview_title_finance_setup);
        textViewCardTitle.setText(getResources().getString(R.string.financemodule));

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText(R.string.setup_costs);


        Button vButtonSetup = getView().findViewById(R.id.button_finance_setup);
        vButtonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FinanceSetupActivity.class);
                intent.putExtra("parent", ObjectSerializer.serialize2(parent));
                intent.putExtra("accepted", false);
                startActivity(intent);
            }
        });
    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getContext());
        pictureDialog.setTitle(getString(R.string.choose_picture));
        String[] items = {
                getString(R.string.select_gallery),
                getString(R.string.capture_camera)
        };
        pictureDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0:
                        choosePictureFromGallery();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        dialog = pictureDialog.create();
        dialog.show();
    }

    private void choosePictureFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void takePhotoFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_CANCELED){
            return;
        }
        if(requestCode == GALLERY_REQUEST){
            if (data != null){
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    saveImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(getView(),R.string.fail_picture,Snackbar.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_REQUEST){
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveImage(thumbnail);
        }
    }

    private void saveImage(Bitmap thumbnail) {
        Log.i("size",String.valueOf(thumbnail.getByteCount()));
        final int THUMBSIZE = 150;

        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(thumbnail,THUMBSIZE,THUMBSIZE);
        Log.i("size",String.valueOf(thumbImage.getByteCount()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        thumbImage.compress(Bitmap.CompressFormat.JPEG,100, out);

        byte[] byteArray = out.toByteArray();
        String value = Base64.encodeToString(byteArray,Base64.DEFAULT);
        String name = String.valueOf(new Date().getTime());
        String type = "image/jpeg";

        image = new Image(name,type,value);
        dialog.dismiss();
        addPicture.setEnabled(false);
    }
}
