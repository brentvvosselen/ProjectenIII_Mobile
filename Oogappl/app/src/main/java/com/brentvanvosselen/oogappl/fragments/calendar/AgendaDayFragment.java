package com.brentvanvosselen.oogappl.fragments.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 03/11/2017.
 */

public class AgendaDayFragment extends Fragment{

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private Date dateShown;
    private Calendar mCalendar = Calendar.getInstance();
    private String dateString;
    private String dayOfWeekString;

    private TextView vTextViewDate, vTextViewDayOfWeek;

    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM",Locale.getDefault());

    private List<Event> events;

    //method to create a new instance and pass data to this new fragment
    public static AgendaDayFragment newInstance(Date d){
        AgendaDayFragment fragment = new AgendaDayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("date",d);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        mCalendar.setTime(dateShown);
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        dateString = dateFormat.format(dateShown);

        SimpleDateFormat dayofWeekFormat = new SimpleDateFormat("EEEE",Locale.getDefault());
        dayOfWeekString = dayofWeekFormat.format(dateShown);

        vTextViewDate = getView().findViewById(R.id.textview_calendar_day_date);
        vTextViewDayOfWeek = getView().findViewById(R.id.textview_calendar_day_dayOfWeek);

        title.setText(dateString);
        vTextViewDate.setText(dateString);
        vTextViewDayOfWeek.setText(dayOfWeekString + ",");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        Call itemsCall = apiInterface.getEventsFromDate(currentUser.getEmail(),dateShown);
        itemsCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    events = (List<Event>) response.body();

                    renderLayout();

                }else{
                    Toast.makeText(getContext(),"Could not retrieve items from " + dateString,Toast.LENGTH_SHORT).show();
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

        dateShown = (Date)getArguments().getSerializable("date");
        Log.i("date",dateShown.toString());

        return inflater.inflate(R.layout.fragment_agenda_day,container,false);
    }


    private void renderLayout(){
        for (final Event e: events) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            ViewGroup main = getView().findViewById(R.id.linearlayout_calendar_day_items);


            View eventView = inflater.inflate(R.layout.calendar_day_item,null);

            TextView vTextViewTitle = eventView.findViewById(R.id.textview_calendar_day_item_title);
            TextView vTextViewTime = eventView.findViewById(R.id.textview_calendar_day_item_time);
            TextView vTextViewDate = eventView.findViewById(R.id.textview_calendar_day_item_date);
            ImageView vImageViewCategory = eventView.findViewById(R.id.imageview_calendar_day_item_color);

            vTextViewTitle.setText(e.getTitle());
            vTextViewTime.setText(dateFormatForTime.format(e.getDatetime()));
            vTextViewDate.setText(dateFormatForDay.format(e.getDatetime()));
            vImageViewCategory.setBackgroundColor(Color.parseColor(e.getCategory().getColor()));

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AgendaFragment.OnCalendarItemSelected mCallback = (AgendaFragment.OnCalendarItemSelected) getActivity();
                    mCallback.onItemSelected(e.getId());
                }
            });

            main.addView(eventView);
        }
    }
}
