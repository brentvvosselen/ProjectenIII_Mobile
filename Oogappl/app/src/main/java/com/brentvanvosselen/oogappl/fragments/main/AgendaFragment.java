package com.brentvanvosselen.oogappl.fragments.main;


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

import com.brentvanvosselen.oogappl.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by brentvanvosselen on 03/10/2017.
 */

public class AgendaFragment extends Fragment {

    private CompactCalendarView vCalendarView;
    private TextView vTextViewCurrentMonth;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.agenda);

        View content = getView();
        vTextViewCurrentMonth = content.findViewById(R.id.textview_calendar_monthyear);
        vCalendarView = content.findViewById(R.id.calendarview_calendar);

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
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateCurrentMonth();
            }
        });

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
}
