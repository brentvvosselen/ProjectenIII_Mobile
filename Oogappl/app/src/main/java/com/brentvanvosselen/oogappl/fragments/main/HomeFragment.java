package com.brentvanvosselen.oogappl.fragments.main;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.models.Costbill;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.activities.MainActivity;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.activities.SetupActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 03/10/2017.
 */

public class HomeFragment extends Fragment {

    private Parent parent;

    SharedPreferences sharedPreferences;
    APIInterface apiInterface;
    User currentUser;

    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM",Locale.getDefault());

    private LinearLayout vLinearLayout;
    private boolean financeSetupCompleted;

    ProgressDialog progressDialog;

    public interface OnHideNavigationItems {
        public void hideNavItems();
        public void showNavItems();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.home);

        sharedPreferences = getContext().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);

        vLinearLayout = getView().findViewById(R.id.linear_home);


        Button vButtonSetup = getView().findViewById(R.id.button_home_setup);
        vButtonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SetupActivity.class);
                startActivity(intent);
            }
        });

        //Indien de gebruiker de setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
        currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Log.i("API:", currentUser.getEmail());
        Call call = apiInterface.getParentByEmail("bearer "+ sharedPreferences.getString("token",null),currentUser.getEmail());

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
                    OnHideNavigationItems mCallback = (OnHideNavigationItems)getActivity();
                    parent = (Parent) response.body();
                    if(!parent.hasDoneSetup()){
                        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
                        vCardSetup.setVisibility(View.VISIBLE);
                        mCallback.hideNavItems();
                    }else{
                        mCallback.showNavItems();
                        generateItems();
                    }
                    financeSetupCompleted = parent.getGroup().bothParentsAccepted();

                } else {
                    Snackbar.make(getView(), R.string.get_parent_neg, Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();
                Log.i("API:", "could not find parent (home-setup)");
                progressDialog.dismiss();
            }
        });
    }

    private void generateItems() {

        Call nextItemCall = apiInterface.getNextEvent("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        //progress

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.getting_data));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View welcome = inflater.inflate(R.layout.welcome_message,null);
        vLinearLayout.addView(welcome);

        nextItemCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    final Event event = (Event) response.body();

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final ViewGroup main = vLinearLayout;

                    View titleView = inflater.inflate(R.layout.home_message,null);
                    TextView tv = titleView.findViewById(R.id.textview_home_message);
                    tv.setText(getText(R.string.next_item));

                    main.addView(titleView);

                    View eventView = inflater.inflate(R.layout.calendar_day_item, null);
                    eventView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AgendaFragment.OnCalendarItemSelected mCallback = (AgendaFragment.OnCalendarItemSelected) getActivity();
                            mCallback.onItemSelected(event.getId());
                        }
                    });

                    TextView vTextViewTitle = eventView.findViewById(R.id.textview_calendar_day_item_title);
                    TextView vTextViewTime = eventView.findViewById(R.id.textview_calendar_day_item_time);
                    TextView vTextViewDate = eventView.findViewById(R.id.textview_calendar_day_item_date);
                    ImageView vImageViewCategory = eventView.findViewById(R.id.imageview_calendar_day_item_color);

                    vTextViewTitle.setText(event.getTitle());
                    vTextViewTime.setText(dateFormatForTime.format(event.getStart()));
                    vTextViewDate.setText(dateFormatForDay.format(event.getStart()));
                    vImageViewCategory.setBackgroundColor(Color.parseColor(event.getCategory().getColor()));

                    main.addView(eventView);

                    showCost();

                }else{
                    Snackbar.make(getView(), R.string.geen_verbinding,Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

                showCost();
            }
        });

    }

    private void showCost() {
        if(financeSetupCompleted){
            Call costCall = apiInterface.getCostbill("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
            costCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()){
                        final Costbill costbill = (Costbill) response.body();

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final ViewGroup main = vLinearLayout;

                        View costView = inflater.inflate(R.layout.card_to_pay,null);

                        TextView vTextViewToPay = costView.findViewById(R.id.textview_to_pay_amount);

                        vTextViewToPay.setText("€ " + costbill.getToPay());

                        main.addView(costView);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    call.cancel();
                    progressDialog.dismiss();
                }
            });
        }else{
            progressDialog.dismiss();
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();

        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
        vCardSetup.setVisibility(View.INVISIBLE);

        //Indien de gebruiker de setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

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
                    Log.i("TEST", "Done1: " + parent.hasDoneSetup());
                    if(!parent.hasDoneSetup()){
                        Log.i("TEST", "Done2: " + parent.hasDoneSetup());
                        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
                        vCardSetup.setVisibility(View.VISIBLE);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(), R.string.get_parent_neg, Snackbar.LENGTH_SHORT).show();
                Log.i("API:", "could not find parent (home-setup)");
                progressDialog.dismiss();
            }
        });
    }
}
