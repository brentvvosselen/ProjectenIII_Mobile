package com.brentvanvosselen.oogappl.fragments.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by brentvanvosselen on 03/11/2017.
 */

public class AgendaDayFragment extends Fragment{

    private Date dateShown;
    private Calendar mCalendar = Calendar.getInstance();
    private String dateString;
    private String dayOfWeekString;

    private TextView vTextViewDate, vTextViewDayOfWeek;

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dateShown = (Date)getArguments().getSerializable("date");
        Log.i("date",dateShown.toString());

        return inflater.inflate(R.layout.fragment_agenda_day,container,false);
    }
}
