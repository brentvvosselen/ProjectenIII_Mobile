package com.brentvanvosselen.oogappl;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.RestClient;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment{

    EditText vEditTextFirstname;
    EditText vEditTextLastname;
    EditText vEditTextEmail;
    EditText vEditTextPassword;
    EditText vEditTextPasswordConfirm;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View content = getView();

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
                    Log.i("Event", "Passwords doen't match");
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Passwords don't match", Toast.LENGTH_SHORT);
                    toast.show();
                    correctForm = false;
                }

                if (correctForm) {
                    Log.i("Register", "Form valid, Register");
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}
