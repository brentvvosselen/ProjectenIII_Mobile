package com.brentvanvosselen.oogappl.fragments.heenenweer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 21/11/2017.
 */

public class HeenEnWeerDayFragment extends Fragment {

    private String dayId;
    private HeenEnWeerDag day;
    private TextView vTextViewChild, vTextViewDescription, vTextViewDate;
    private LinearLayout vLinearLayoutItems;

    //dateformat
    private SimpleDateFormat dateFormat= new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);


    public static HeenEnWeerDayFragment newInstance(String dayId){
        HeenEnWeerDayFragment fragment = new HeenEnWeerDayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("day",dayId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set title
        final TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.heenenweer);

        //set textviews
        vTextViewChild = getView().findViewById(R.id.textview_heenenweer_day_child);
        vTextViewDate = getView().findViewById(R.id.textview_heenenweer_day_date);
        vTextViewDescription = getView().findViewById(R.id.textview_heenenweer_day_description);

        //set linearlayout
        vLinearLayoutItems = getView().findViewById(R.id.linearlayout_heenenweer_day_items);

        //get day
        Call dayCall = apiInterface.getHeenEnWeerDay("bearer " + sharedPreferences.getString("token",null), dayId);
        dayCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    day = (HeenEnWeerDag) response.body();

                    vTextViewChild.setText(day.getChild().getFirstname() + " " + day.getChild().getLastname());
                    vTextViewDate.setText(dateFormat.format(day.getDate()));
                    vTextViewDescription.setText(day.getDescription());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final ViewGroup main = vLinearLayoutItems;
                    for(final HeenEnWeerItem item: day.getItems()){
                        View itemView = inflater.inflate(R.layout.card_heenenweer_item,null);

                        TextView category = itemView.findViewById(R.id.card_heenenweer_item_category_name);
                        TextView value = itemView.findViewById(R.id.card_heenenweer_item_value);

                        ImageButton editButton = itemView.findViewById(R.id.card_heenenweer_item_edit);
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HeenEnWeerFragment.OnHeenEnWeerAction mCallback = (HeenEnWeerFragment.OnHeenEnWeerAction)getActivity();
                                mCallback.onEditItem(item);
                            }
                        });

                        category.setText(item.getCategory().getType());
                        value.setText(item.getValue());

                        main.addView(itemView);
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get argument
        try{
            dayId = (String)getArguments().getSerializable("day");
        }catch(NullPointerException e){
            Toast.makeText(getContext(),"Geen dag gevonden",Toast.LENGTH_SHORT).show();
        }


        return inflater.inflate(R.layout.fragment_heenenweer_day,container,false);
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
            HeenEnWeerFragment.OnHeenEnWeerAction mCallback = (HeenEnWeerFragment.OnHeenEnWeerAction)getActivity();
            mCallback.onAddItem(this.dayId);
        }
        return super.onOptionsItemSelected(item);
    }
}
