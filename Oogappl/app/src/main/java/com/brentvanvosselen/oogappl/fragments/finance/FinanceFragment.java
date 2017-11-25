package com.brentvanvosselen.oogappl.fragments.finance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.brentvanvosselen.oogappl.RestClient.models.Cost;
import com.brentvanvosselen.oogappl.RestClient.models.CostCategory;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.activities.FinanceSetupActivity;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinanceFragment extends Fragment {

    private User currentUser;
    private Parent parent;
    private List<Cost> costs = new ArrayList<>();
    private CardView vCardSetup;
    private List<CostCategory> categories;
    private List<String> categorieNames;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    SharedPreferences sharedPreferences;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.finance);

        vCardSetup = getView().findViewById(R.id.card_finance_setup);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Call call = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    parent = (Parent) response.body();

                    if(parent.getGroup().bothParentsAccepted()) {
                        // Beide parents hebben de financien geaccepteerd
                        bothAccepted();
                        Log.i("TYPE", "BOTH");

                    } else if(parent.getGroup().parentHasAccepted(parent)) {
                        // Ene parent heeft setup doorlopen, andere nog niet
                        currentAccepted();
                        Log.i("TYPE", "CURRENT");

                    } else if(parent.getGroup().otherParentHasAccepted(parent)) {
                        // Andere parent heeft setup doorlopen, huidige nog niet
                        otherAccepted();
                        Log.i("TYPE", "OTHER");

                    } else if(parent.hasDoneSetup()){
                        //Indien de gebruiker de financien setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
                        noneAccepted();
                        Log.i("TYPE", "NONE");
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Could not find parent", Toast.LENGTH_SHORT).show();
                Log.i("API:", "could not find parent (finance-setup)");
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
                    categorieNames.add("+ Nieuwe categorie");
                } else {
                    Toast.makeText(getContext(), "BAD RESPONSE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.geen_verbinding), Toast.LENGTH_SHORT).show();
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

        Call call = apiInterface.getAllCosts("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    costs = (List<Cost>) response.body();
                    initCostscards();
                } else {
                    Toast.makeText(getContext(), "BAD RESPONSE", Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.geen_verbinding), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCostscards() {
        LinearLayout container = getView().findViewById(R.id.lineairlayout_costs);
        container.removeAllViews();

        for(Cost c : costs) {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View costCard = inflater.inflate(R.layout.cost_item, null);

            ((TextView) costCard.findViewById(R.id.textView_card_cost_title)).setText(c.getTitle());
            ((TextView) costCard.findViewById(R.id.textView_card_cost_date)).setText(dateFormat.format(c.getDate()));
            ((TextView) costCard.findViewById(R.id.textView_card_cost_amount)).setText("€ " + String.valueOf(c.getAmount()));
            ((TextView) costCard.findViewById(R.id.textView_card_cost_description)).setText(c.getDescription());

            container.addView(costCard);
            Log.i("COST", "Cost added");
        }
    }

    private void createDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        //inflate custom dialog
        final View mView = inflater.inflate(R.layout.dialog_add_cost, null);

        final List<EditText> fields = new ArrayList<>();

        final EditText editTextTitle = mView.findViewById(R.id.editText_cost_title);
        fields.add(editTextTitle);
        final EditText editTextDescription = mView.findViewById(R.id.editText_card_cost_description);
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

        final AlertDialog dialog = builder.setView(mView)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

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

                        if(correct) {
                            String title = editTextTitle.getText().toString();
                            String desciption = editTextDescription.getText().toString();
                            double amount = Double.parseDouble(editTextAmount.getText().toString());
                            String date = editTextDate.getText().toString();

                            CostCategory category = categories.get(spinnerCategory.getSelectedItemPosition());
                            Cost temp = new Cost(title, desciption, amount, new Date(), category);
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
                    Log.i("ADD CAT", "SUCCESFULL");
                    CostCategory cat = (CostCategory) response.body();
                    categories.add(cat);
                    categorieNames.remove(categorieNames.size() - 1);
                    categorieNames.add(cat.getType());
                    categorieNames.add("+ Nieuwe categorie");
                } else {
                    Toast.makeText(getContext(), "BAD RESPONSE", Toast.LENGTH_SHORT).show();
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
                    Log.i("RESPONSE", cost.getTitle());

                    initCostscards();
                } else {
                    Toast.makeText(getContext(), "BAD RESPONSE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // Toast.makeText(getContext(), getResources().getString(R.string.geen_verbinding), Toast.LENGTH_SHORT).show();
                Log.i("FOUT", t.getMessage());

                costs.add(c);

                call.cancel();
            }
        });
    }

    private void currentAccepted() {
        vCardSetup.setVisibility(View.VISIBLE);

        TextView textViewCardTitle = vCardSetup.findViewById(R.id.textview_title_finance_setup);
        textViewCardTitle.setText("Kostenmodule");

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText("Er zijn al gegevens ingevuld. Wacht op aanvaarding.");

        Button button = vCardSetup.findViewById(R.id.button_finance_setup);
        button.setVisibility(View.GONE);
    }

    private void otherAccepted() {
        vCardSetup.setVisibility(View.VISIBLE);

        TextView textViewCardTitle = vCardSetup.findViewById(R.id.textview_title_finance_setup);
        textViewCardTitle.setText("Kostenmodule");

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText("Er zijn al gegevens ingevuld. " +
                "Aanvaard of pas deze aan om de kostenmodule te gebruiken");

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
        textViewCardTitle.setText("Kostenmodule");

        TextView textViewCardDesc = vCardSetup.findViewById(R.id.textview_desc_finance_setup);
        textViewCardDesc.setText("Vervolledig de setup om de kostenmodule te gebruiken");


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
}
