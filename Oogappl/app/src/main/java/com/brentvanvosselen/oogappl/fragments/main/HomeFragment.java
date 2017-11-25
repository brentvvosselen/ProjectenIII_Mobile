package com.brentvanvosselen.oogappl.fragments.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.activities.SetupActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 03/10/2017.
 */

public class HomeFragment extends Fragment {

    private Parent parent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.home);

        Button vButtonSetup = getView().findViewById(R.id.button_home_setup);
        vButtonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SetupActivity.class);
                startActivity(intent);
            }
        });

        //Indien de gebruiker de setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Log.i("API:", currentUser.getEmail());
        Call call = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    parent = (Parent) response.body();
                    if(!parent.hasDoneSetup()){
                        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
                        vCardSetup.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Could not find parent", Toast.LENGTH_SHORT).show();
                Log.i("API:", "could not find parent (home-setup)");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();

        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
        vCardSetup.setVisibility(View.INVISIBLE);

        //Indien de gebruiker de setup nog niet doorlopen heeft, krijgt hij dit kaartje te zien
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        Call call = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    parent = (Parent) response.body();
                    Log.i("TEST", "Done1: " + parent.hasDoneSetup());
                    if(!parent.hasDoneSetup()){
                        Log.i("TEST", "Done2: " + parent.hasDoneSetup());
                        CardView vCardSetup = getView().findViewById(R.id.card_home_setup);
                        vCardSetup.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Could not find parent", Toast.LENGTH_SHORT).show();
                Log.i("API:", "could not find parent (home-setup)");
            }
        });
    }
}
