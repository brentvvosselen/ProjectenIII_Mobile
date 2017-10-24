package com.brentvanvosselen.oogappl.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment{

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private Context context;
    private EditText vEditTextFirstname;
    private EditText vEditTextLastname;
    private EditText vEditTextEmail;
    private EditText vEditTextPassword;
    private EditText vEditTextPasswordConfirm;

    private Boolean succes = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View content = getView();
        context = getActivity().getApplicationContext();

        vEditTextFirstname = content.findViewById(R.id.edittext_register_firstname);
        vEditTextLastname = content.findViewById(R.id.edittext_register_lastname);
        vEditTextEmail = content.findViewById(R.id.edittext_register_email);
        vEditTextPassword = content.findViewById(R.id.edittext_register_password);
        vEditTextPasswordConfirm = content.findViewById(R.id.edittext_register_passwordconfirm);

        Button vButtonRegister = (Button) view.findViewById(R.id.button_register);
        vButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean correctForm = true;

                String firstname = vEditTextFirstname.getText().toString();
                String lastname = vEditTextLastname.getText().toString();
                String email = vEditTextEmail.getText().toString();
                String password  = vEditTextPassword.getText().toString();
                String passwordConfirm = vEditTextPasswordConfirm.getText().toString();

                if (firstname.trim().equals("")) {
                    vEditTextFirstname.setError("Firstname is required!");
                    correctForm = false;
                }

                if (lastname.trim().equals("")) {
                    vEditTextLastname.setError("Lastname is required");
                    correctForm = false;
                }

                if (email.trim().equals("")) {
                    vEditTextEmail.setError("Email is required");
                    correctForm = false;
                }

                if (password.trim().equals("")) {
                    vEditTextPassword.setError("Password is required");
                    correctForm = false;
                }

                if (!password.equals(passwordConfirm)) {
                    vEditTextPasswordConfirm.setError("Password don't match");
                    correctForm = false;
                }

                if (correctForm) {
                    User u = new User(firstname, lastname, email, password);
                    Register(u);
                    Log.i("API call", succes?"SUCCES":"FAIL");

                    if(succes) {
                        Fragment login_fragment = new LoginFragment();

                        if (login_fragment != null){
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            login_fragment.setArguments(bundle);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.content_login, login_fragment);
                            ft.commit();
                        }
                    } else {
                        Toast.makeText(context, "Register failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    private void Register(User u) {
        Call call = apiInterface.createUser(u);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                succes = false;
                Log.i("API event", response.message());

                if(response.message() == "SUCCES") {
                    succes = true;
                } else {
                    succes = false;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                succes = false;
                call.cancel();
            }
        });
    }

    public void onBackPressed() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment login_fragment = new LoginFragment();
        ft.replace(R.id.content_login, login_fragment, "CURRENT_FRAGMENT");
        ft.commit();
    }
}
