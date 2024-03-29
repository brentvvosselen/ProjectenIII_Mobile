package com.brentvanvosselen.oogappl.fragments.calendar;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.mikhaellopez.circularimageview.CircularImageView;

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

    private APIInterface apiInterface;
    SharedPreferences sharedPreferences;

    private TextView vTextViewStartDate,vTextViewStartMonth, vTextViewStartTime,vTextViewEndDate,vTextViewEndMonth, vTextViewEndTime, vTextViewChildren,vTextViewDescription, vTextViewTitle;
    private ImageView vImageViewCategory;
    private LinearLayout vLinearChildren;

    private SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
    private SimpleDateFormat dateFormatForDate = new SimpleDateFormat("dd",Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("EEEE",Locale.getDefault());



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

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        View content = getView();

        vTextViewStartDate = content.findViewById(R.id.textview_calendar_item_start_date);
        vTextViewStartTime = content.findViewById(R.id.textview_calendar_item_start_time);
        vTextViewStartMonth = content.findViewById(R.id.textview_calendar_item_start_month);

        vTextViewEndDate = content.findViewById(R.id.textview_calendar_item_end_date);
        vTextViewEndTime = content.findViewById(R.id.textview_calendar_item_end_time);
        vTextViewEndMonth = content.findViewById(R.id.textview_calendar_item_end_month);

        vTextViewDescription = content.findViewById(R.id.textview_calendar_item_description);
        vTextViewTitle = content.findViewById(R.id.textview_calendar_item_title);
        vLinearChildren = content.findViewById(R.id.linear_agenda_item_children);

        Call itemCall = apiInterface.getEvent("bearer " + sharedPreferences.getString("token",null), itemId);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.getting_data));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        itemCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Event event = (Event) response.body();

                    vTextViewStartTime.setText(dateFormatForTime.format(event.getStart()));
                    vTextViewStartDate.setText(dateFormatForDate.format(event.getStart()));
                    vTextViewStartMonth.setText(dateFormatForMonth.format(event.getStart()));

                    vTextViewEndTime.setText(dateFormatForTime.format(event.getEnd()));
                    vTextViewEndDate.setText(dateFormatForDate.format(event.getEnd()));
                    vTextViewEndMonth.setText(dateFormatForMonth.format(event.getEnd()));

                    vTextViewDescription.setText(event.getDescription());
                    vTextViewTitle.setText(event.getTitle());



                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final ViewGroup main = vLinearChildren;

                    for (Child child : event.getchildren()) {

                        View childView = inflater.inflate(R.layout.child_list_item,null);

                        CircularImageView imgView = childView.findViewById(R.id.imageview_child_item);
                        TextView textView = childView.findViewById(R.id.textview_child_item_name);

                        textView.setText(child.getFirstname() + " " + child.getLastname());



                        if(child.getPicture() != null){
                            byte[] decodedString = Base64.decode(child.getPicture().getValue(),Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                            imgView.setImageBitmap(decodedByte);
                        }else{
                            Bitmap image = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.no_picture);
                            imgView.setImageBitmap(image);
                        }

                        main.addView(childView);
                    }


                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

                    //actionbar kleur
                    ColorDrawable color = new ColorDrawable(Color.parseColor(event.getCategory().getColor()));
                    actionBar.setBackgroundDrawable(color);
                    //actionbar tekst
                    TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
                    title.setText(event.getCategory().getType());



                }else{
                    Snackbar.make(getView(), R.string.get_event_neg,Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("ERROR", t.getMessage());
                Snackbar.make(getView(),R.string.geen_verbinding,Snackbar.LENGTH_SHORT).show();
                call.cancel();
                progressDialog.dismiss();
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
            AgendaFragment.OnCalendarItemSelected mCallback = (AgendaFragment.OnCalendarItemSelected)getActivity();
            mCallback.onItemEdit(itemId);
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
