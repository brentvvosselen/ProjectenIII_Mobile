package com.brentvanvosselen.oogappl.fragments.calendar;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Event;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 04/11/2017.
 */

public class AgendaItemFragment extends Fragment{

    private String itemId;
    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private TextView vTextViewDate, vTextViewTime, vTextViewCategory,vTextViewDescription;
    private ImageView vImageViewCategory;

    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatForDate = new SimpleDateFormat("EEEE, dd MMMM yyyy",Locale.getDefault());




    public static AgendaItemFragment newInstance(String s){
        AgendaItemFragment fragment = new AgendaItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",s);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View content = getView();

        vTextViewCategory = content.findViewById(R.id.textview_calendar_item_category);
        vTextViewDate = content.findViewById(R.id.textview_calendar_item_date);
        vTextViewTime = content.findViewById(R.id.textview_calendar_item_time);
        vTextViewDescription = content.findViewById(R.id.textview_calendar_item_description);
        vImageViewCategory = content.findViewById(R.id.imageview_calendar_item_color);

        Call itemCall = apiInterface.getEvent(itemId);
        itemCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Event event = (Event) response.body();
                    vTextViewCategory.setText(event.getCategory().getType());
                    //vImageViewCategory.setBackgroundColor(Color.parseColor(event.getCategory().getColor()));
                    vTextViewTime.setText(dateFormatForTime.format(event.getDatetime()));
                    vTextViewDate.setText(dateFormatForDate.format(event.getDatetime()));
                    vTextViewDescription.setText(event.getDescription());

                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                    //actionbar kleur
                    ColorDrawable color = new ColorDrawable(Color.parseColor(event.getCategory().getColor()));
                    actionBar.setBackgroundDrawable(color);
                    //actionbar tekst
                    TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
                    title.setText(event.getTitle());

                }else{
                    Toast.makeText(getContext(),"Could not retrieve event",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(),"Could not connect to the server",Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemId = (String)getArguments().getSerializable("id");
        Log.i("item",itemId);

        return inflater.inflate(R.layout.fragment_agenda_item,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit){
            Log.i("action","edit item");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_blue));

    }
}
