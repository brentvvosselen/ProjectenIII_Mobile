package com.brentvanvosselen.oogappl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by joshi on 08/10/2017.
 */

public class LoginFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.login);

        View content = getView();
        TextView vRegisterTextView = (TextView) content.findViewById(R.id.registerTextView);

        vRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Event: ", "Clicked on create new account");
                Fragment register_fragment = new RegisterFragment();

                if (register_fragment != null){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_login,register_fragment);
                    ft.commit();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}
