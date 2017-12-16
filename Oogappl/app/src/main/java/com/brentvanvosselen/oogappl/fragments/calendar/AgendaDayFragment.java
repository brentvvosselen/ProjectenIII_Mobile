package com.brentvanvosselen.oogappl.fragments.calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.DayChildrenAdapter;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerFragment;
import com.brentvanvosselen.oogappl.layout.ExpandableHeightGridView;
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

    private APIInterface apiInterface;
    SharedPreferences sharedPreferences;

    private Date dateShown;
    private Calendar mCalendar = Calendar.getInstance();
    private String dateString;
    private String dayOfWeekString;

    private TextView vTextViewDate, vTextViewDayOfWeek, vTextViewUpcoming;

    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM",Locale.getDefault());

    private List<Event> events;
    private List<HeenEnWeerDag> childrenBooks;

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

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.overview);

        mCalendar.setTime(dateShown);
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        dateString = dateFormat.format(dateShown);

        SimpleDateFormat dayofWeekFormat = new SimpleDateFormat("EEEE",Locale.getDefault());
        dayOfWeekString = dayofWeekFormat.format(dateShown);

        vTextViewDate = getView().findViewById(R.id.textview_calendar_day_date);
        vTextViewDayOfWeek = getView().findViewById(R.id.textview_calendar_day_dayOfWeek);
        vTextViewUpcoming = getView().findViewById(R.id.textview_calendar_day_upcoming);


        vTextViewDate.setText(dateString);
        vTextViewDayOfWeek.setText(dayOfWeekString);

        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        //progress
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.getting_data));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Call itemsCall = apiInterface.getEventsFromDate("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail(),dateShown);
        itemsCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful()){
                    events = (List<Event>) response.body();
                    renderLayout();
                    if(events.size() == 0)
                        vTextViewUpcoming.setText(R.string.no_events);

                }else{
                    Toast.makeText(getContext(), R.string.retrieve_items_neg + dateString,Toast.LENGTH_SHORT).show();
                }
                progressDialog.setProgress(50);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

        final Call childrenCall = apiInterface.getChildrenFromBookFromDate("bearer " + sharedPreferences.getString("token",null),currentUser.getEmail(),dateShown);
        childrenCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful()){
                    childrenBooks = (List<HeenEnWeerDag>)response.body();
                    renderChildren();
                }else{
                    Toast.makeText(getContext(),R.string.retrieve_items_neg + dateString,Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                call.cancel();
                progressDialog.dismiss();
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

    private void renderChildren(){
        ExpandableHeightGridView gridView = (ExpandableHeightGridView) getView().findViewById(R.id.gridview_calendar_day_children);
        DayChildrenAdapter childrenAdapter = new DayChildrenAdapter(getContext(),childrenBooks.toArray(new HeenEnWeerDag[childrenBooks.size()]));
        gridView.setAdapter(childrenAdapter);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HeenEnWeerDag dag = childrenBooks.get(i);
                HeenEnWeerFragment.OnHeenEnWeerAction mCallback = (HeenEnWeerFragment.OnHeenEnWeerAction)getActivity();
                mCallback.showDay(dag.getId());
            }
        });
        /*LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup main = getView().findViewById(R.id.linearlayout_calendar_day_children);
        for (final HeenEnWeerDag c : childrenBooks){
            View childView = inflater.inflate(R.layout.row_child_book,null);

            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeenEnWeerFragment.OnHeenEnWeerAction mCallback = (HeenEnWeerFragment.OnHeenEnWeerAction)getActivity();
                    mCallback.showDay(c.getId());
                }
            });

            TextView vTextViewName = childView.findViewById(R.id.textview_row_child_book_name);
            vTextViewName.setText(c.getChild().getFirstname());
            main.addView(childView);
        }*/

    }

    private void renderLayout(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup main = getView().findViewById(R.id.linearlayout_calendar_day_items);
        for (final Event e: events) {
            View eventView = inflater.inflate(R.layout.calendar_day_item,null);

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AgendaFragment.OnCalendarItemSelected mCallback = (AgendaFragment.OnCalendarItemSelected)getActivity();
                    mCallback.onItemSelected(e.getId());
                }
            });

            TextView vTextViewTitle = eventView.findViewById(R.id.textview_calendar_day_item_title);
            TextView vTextViewTime = eventView.findViewById(R.id.textview_calendar_day_item_time);
            TextView vTextViewDate = eventView.findViewById(R.id.textview_calendar_day_item_date);
            ImageView vImageViewCategory = eventView.findViewById(R.id.imageview_calendar_day_item_color);

            vTextViewTitle.setText(e.getTitle());
            vTextViewTime.setText(dateFormatForTime.format(e.getStart()));
            vTextViewDate.setText(dateFormatForDay.format(e.getStart()));
            vImageViewCategory.setBackgroundColor(Color.parseColor(e.getCategory().getColor()));

            Log.i("VIEW", "ADDED ");

            main.addView(eventView);
        }
    }
}
