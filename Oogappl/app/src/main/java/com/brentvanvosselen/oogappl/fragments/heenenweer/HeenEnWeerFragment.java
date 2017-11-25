package com.brentvanvosselen.oogappl.fragments.heenenweer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.DaysAdapter;
import com.brentvanvosselen.oogappl.listeners.ClickListener;
import com.brentvanvosselen.oogappl.listeners.DayRecyclerTouchListener;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 19/11/2017.
 */

public class HeenEnWeerFragment extends Fragment {

    private HeenEnWeerBoek[] books;
    private List<HeenEnWeerDag> days;
    private RecyclerView recyclerView;
    private DaysAdapter mAdapter;

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    SharedPreferences sharedPreferences;

    public interface OnHeenEnWeerAction{
        public void showDay(String id);
        public void onEditItem(HeenEnWeerItem id);
        public void onAddItem(String dayid);
        public void onAddDay();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.heenenweer);

        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        final Call booksCall = apiInterface.getAllBooks("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
        booksCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    books = (HeenEnWeerBoek[]) response.body();
                    //fill list
                    recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_books);

                    days = new ArrayList<>();
                    for (HeenEnWeerBoek book:books) {
                        List<HeenEnWeerDag> bookDay = new ArrayList<>(Arrays.asList(book.getDays()));
                        days.addAll(bookDay);
                    }
                    mAdapter = new DaysAdapter(days);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.addOnItemTouchListener(new DayRecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            HeenEnWeerDag dag = days.get(position);
                            OnHeenEnWeerAction mCallback = (OnHeenEnWeerAction)getActivity();
                            mCallback.showDay(dag.getId());
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
                    Log.i("VALUES",books.toString());
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
        return inflater.inflate(R.layout.fragment_heenenweer,container,false);
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
            OnHeenEnWeerAction mCallback = (OnHeenEnWeerAction)getActivity();
            mCallback.onAddDay();
        }
        return super.onOptionsItemSelected(item);

    }
}
