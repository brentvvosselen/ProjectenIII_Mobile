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

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    private String email;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View content = getView();
        TextView vTextViewRegister = content.findViewById(R.id.textview_button_createaccount);

        Bundle bundle = (Bundle) getArguments();

        if(bundle != null) {
            this.email = bundle.getString("email");
            Log.i("Saved", "Received email: " + this.email);
        }

        if(email != null) {
            TextView vTextViewEmail = content.findViewById(R.id.edittext_login_email);
            vTextViewEmail.setText(this.email);
        }

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}
