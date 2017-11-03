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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 03/10/2017.
 */

public class AgendaFragment extends Fragment {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    private final Calendar mCalendar = Calendar.getInstance();


    private CompactCalendarView vCalendarView;
    private TextView vTextViewCurrentMonth;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    public interface OnCalendarItemSelected{
        public void onDateSelected(Date date);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.agenda);

        View content = getView();
        vTextViewCurrentMonth = content.findViewById(R.id.textview_calendar_monthyear);
        vCalendarView = content.findViewById(R.id.calendarview_calendar);
        vCalendarView.setDayColumnNames(new String[]{"M","D","W","D","V","Z","Z"});

        ImageButton vButtonPrev = content.findViewById(R.id.imagebutton_calendar_month_back);
        vButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vCalendarView.showPreviousMonth();
                updateCurrentMonth();
            }
        });

        ImageButton vButtonNext = content.findViewById(R.id.imagebutton_calendar_month_forward);
        vButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vCalendarView.showNextMonth();
                updateCurrentMonth();
            }
        });

        //listener for calendar
        vCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Log.i("EVENT",dateClicked.toString());
                OnCalendarItemSelected mCallback = (OnCalendarItemSelected) getActivity();
                mCallback.onDateSelected(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateCurrentMonth();
            }
        });

        addEventsToCalendar();

        updateCurrentMonth();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agenda,container,false);


    }

    private void updateCurrentMonth(){
        vTextViewCurrentMonth.setText(dateFormatForMonth.format(vCalendarView.getFirstDayOfCurrentMonth()));
    }

    private void addEventsToCalendar(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(2017,10,3);
        Log.i("date",String.valueOf(c.getTimeInMillis()));



        //get current user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        //get events
        Call call = apiInterface.getEvents(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    List<com.brentvanvosselen.oogappl.RestClient.Event> events = (List<com.brentvanvosselen.oogappl.RestClient.Event>) response.body();
                    List<Event> parsedEvents = new ArrayList<Event>();
                    for(com.brentvanvosselen.oogappl.RestClient.Event event: events){
                        int color = Color.parseColor(event.getCategory().getColor());
                        mCalendar.setTime(event.getDatetime());
                        long timeInMIllis = mCalendar.getTimeInMillis();
                        Log.i("time",String.valueOf(timeInMIllis));
                        String id = event.getId();
                        Event e = new Event(color,timeInMIllis,id);
                        parsedEvents.add(e);
                    }


                    vCalendarView.addEvents(parsedEvents);

                }else{
                    Toast.makeText(getContext(),"events could not be retrieved",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                call.cancel();
                Toast.makeText(getContext(),"Could not connect to server",Toast.LENGTH_SHORT).show();
            }
        });


    }

}
