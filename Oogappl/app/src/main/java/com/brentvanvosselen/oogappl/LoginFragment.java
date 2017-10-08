package com.brentvanvosselen.oogappl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.RestClient;

/**
 * Created by joshi on 08/10/2017.
 */

public class LoginFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View content = getView();
        TextView vTextViewRegister = content.findViewById(R.id.textview_button_createaccount);

        vTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Event", "Clicked on create new account");
                Fragment register_fragment = new RegisterFragment();

                if (register_fragment != null){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_login,register_fragment);
                    ft.commit();
                }
            }
        });

        Button vButtonRegister = content.findViewById(R.id.button_login);
        vButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Event", "Clicked on log in");
            }
        });



        new RestClient(":5000/api/parents", "GET") {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i("json", s);
            }
        }.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}
