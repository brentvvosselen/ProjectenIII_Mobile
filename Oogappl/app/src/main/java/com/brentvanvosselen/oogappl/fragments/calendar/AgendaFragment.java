package com.brentvanvosselen.oogappl.fragments.calendar;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class AgendaFragment extends Fragment {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    private final Calendar mCalendar = Calendar.getInstance();


    private CompactCalendarView vCalendarView;
    private TextView vTextViewCurrentMonth,vTextViewNextItem;
    private ImageButton vButtonPrev;
    private ImageButton vButtonNext;

    //next event
    private TextView vTextViewNextTitle, vTextViewNextTime, vTextViewNextDate;
    private ImageView vImageViewNextCategory;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM",Locale.getDefault());

    private String nextItemId = "";

    public interface OnCalendarItemSelected{
        public void onDateSelected(Date date);
        public void onItemSelected(String id);
        public void onAddItemSelected();
        public void onItemEdit(String eventId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.agenda);

        View content = getView();
        vTextViewCurrentMonth = content.findViewById(R.id.textview_calendar_monthyear);
        vTextViewNextItem = content.findViewById(R.id.textview_calendar_nextitem);
        vCalendarView = content.findViewById(R.id.calendarview_calendar);
        vCalendarView.setDayColumnNames(new String[]{"M","D","W","D","V","Z","Z"});

        vButtonPrev = content.findViewById(R.id.imagebutton_calendar_month_back);
        vButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vCalendarView.showPreviousMonth();
                updateCurrentMonth();
            }
        });

        vButtonNext = content.findViewById(R.id.imagebutton_calendar_month_forward);
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

        vTextViewNextTitle = content.findViewById(R.id.textview_calendar_next_title);
        vTextViewNextTime = content.findViewById(R.id.textview_calendar_next_time);
        vTextViewNextDate = content.findViewById(R.id.textview_calendar_next_date);
        vImageViewNextCategory = content.findViewById(R.id.imageview_calendar_next_color);

        CardView vCardViewNext = content.findViewById(R.id.cardview_calendar_next);
        vCardViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnCalendarItemSelected mCallback = (OnCalendarItemSelected) getActivity();
                mCallback.onItemSelected(nextItemId);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        Call nextItemCall = apiInterface.getNextEvent(currentUser.getEmail());
        nextItemCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    com.brentvanvosselen.oogappl.RestClient.models.Event event = (com.brentvanvosselen.oogappl.RestClient.models.Event) response.body();
                    vTextViewNextTitle.setText(event.getTitle());
                    vTextViewNextTime.setText(dateFormatForTime.format(event.getStart()));
                    vTextViewNextDate.setText(dateFormatForDay.format(event.getStart()));
                    vImageViewNextCategory.setBackgroundColor(Color.parseColor(event.getCategory().getColor()));
                    nextItemId = event.getId();

                }else{
                    Toast.makeText(getContext(),"not succesful",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(),"call failed",Toast.LENGTH_SHORT).show();
                call.cancel();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            OnCalendarItemSelected mCallback = (OnCalendarItemSelected)getActivity();
            mCallback.onAddItemSelected();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentMonth(){
        vTextViewCurrentMonth.setText(dateFormatForMonth.format(vCalendarView.getFirstDayOfCurrentMonth()));
    }

    private void addEventsToCalendar(){
        Calendar c = Calendar.getInstance();

        //get current user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        //get events
        Call call = apiInterface.getEvents(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    List<com.brentvanvosselen.oogappl.RestClient.models.Event> events = (List<com.brentvanvosselen.oogappl.RestClient.models.Event>) response.body();
                    List<Event> parsedEvents = new ArrayList<Event>();
                    for(com.brentvanvosselen.oogappl.RestClient.models.Event event: events){
                        int color = Color.parseColor(event.getCategory().getColor());
                        Date date = new Date(event.getStart().getYear(),event.getStart().getMonth(),event.getStart().getDate());
                        while(!date.after(event.getEnd())){
                            mCalendar.setTime(date);
                            long timeInMIllis = mCalendar.getTimeInMillis();
                            Log.i("time",String.valueOf(timeInMIllis));
                            String id = event.getId();
                            Event e = new Event(color,timeInMIllis,id);
                            parsedEvents.add(e);
                            mCalendar.add(Calendar.DATE,1);
                            date = mCalendar.getTime();
                        }

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
