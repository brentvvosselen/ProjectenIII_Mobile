package com.brentvanvosselen.oogappl.fragments.heenenweer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.adapters.DaysAdapter;
import com.brentvanvosselen.oogappl.listeners.ClickListener;
import com.brentvanvosselen.oogappl.listeners.DayRecyclerTouchListener;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());


    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    SharedPreferences sharedPreferences;

    SectionedRecyclerViewAdapter sectionAdapter;

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

                    sectionAdapter = new SectionedRecyclerViewAdapter();


                    days = new ArrayList<>();
                    for (HeenEnWeerBoek book:books) {
                        List<HeenEnWeerDag> bookDay = new ArrayList<>(Arrays.asList(book.getDays()));
                        days.addAll(bookDay);

                        sectionAdapter.addSection(new BookSection(book.getChild().getFirstname(),Arrays.asList(book.getDays())));
                    }



                    //mAdapter = new DaysAdapter(days);




                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                   /* recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                    recyclerView.setHasFixedSize(true);*/
                    //recyclerView.setAdapter(mAdapter);
                    recyclerView.setAdapter(sectionAdapter);
                   /* recyclerView.addOnItemTouchListener(new DayRecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
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
                    Log.i("VALUES",books.toString());*/
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


    private class BookSection extends StatelessSection {

        String title;
        List<HeenEnWeerDag> days = new ArrayList<>();
        boolean expanded = false;

        public BookSection(String title, List<HeenEnWeerDag>list){
            super(new SectionParameters.Builder(R.layout.row_list_days)
                    .headerResourceId(R.layout.section_list_days)
                    .build());

            this.title = title;
            this.days = list;
        }



        @Override
        public int getContentItemsTotal() {
            return expanded? days.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new HeenEnWeerBoekViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            HeenEnWeerBoekViewHolder itemHolder = (HeenEnWeerBoekViewHolder) holder;

            //viewbinding
            final HeenEnWeerDag day = days.get(position);

            itemHolder.tvDescription.setText(day.getDescription());
            itemHolder.tvDate.setText(dateFormat.format(day.getDate()));
            itemHolder.tvChild.setText(day.getChild().getFirstname());

            if(day.getChild().getPicture() != null){
                byte[] decodedString = Base64.decode(day.getChild().getPicture().getValue(),Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                itemHolder.ivChild.setImageBitmap(decodedByte);
            }else{
                Bitmap image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.no_picture);
                itemHolder.ivChild.setImageBitmap(image);
            }

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeenEnWeerFragment.OnHeenEnWeerAction mCallback = (HeenEnWeerFragment.OnHeenEnWeerAction) getActivity();
                    mCallback.showDay(day.getId());
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new DayHeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final DayHeaderViewHolder headerHolder = (DayHeaderViewHolder) holder;

            headerHolder.tvTitle.setText(title);

            headerHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expanded = !expanded;
                    headerHolder.igChevron.setImageResource(
                            expanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down
                    );

                    sectionAdapter.notifyDataSetChanged();
                }
            });


        }


        private class HeenEnWeerBoekViewHolder extends RecyclerView.ViewHolder{

            private final TextView tvChild, tvDate, tvDescription;
            private final CircularImageView ivChild;
            private final View rootView;


            HeenEnWeerBoekViewHolder(View view){
                super(view);
                rootView = view;
                tvChild = view.findViewById(R.id.textview_days_list_child);
                tvDate = view.findViewById(R.id.textview_days_list_date);
                tvDescription = view.findViewById(R.id.textview_days_list_description);
                ivChild = view.findViewById(R.id.imageview_days_list_child);

            }

        }

        private class DayHeaderViewHolder extends RecyclerView.ViewHolder{
            private final View rootView;
            private final TextView tvTitle;
            private final ImageView igChevron;

            DayHeaderViewHolder(View view){
                super(view);

                rootView = view;
                tvTitle = view.findViewById(R.id.text_view_section_days);
                igChevron = view.findViewById(R.id.imageview_section_days);
            }

        }
    }
}
