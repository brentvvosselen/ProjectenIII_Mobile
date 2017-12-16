package com.brentvanvosselen.oogappl.fragments.login;

import android.app.ProgressDialog;
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
import com.brentvanvosselen.oogappl.RestClient.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment{

    private APIInterface apiInterface;

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

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);

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
                    vEditTextFirstname.setError(getResources().getString(R.string.err_firstname_empty));
                    correctForm = false;
                }

                if (lastname.trim().equals("")) {
                    vEditTextLastname.setError(getResources().getString(R.string.err_lastname_empty));
                    correctForm = false;
                }

                if (email.trim().equals("")) {
                    vEditTextEmail.setError(getResources().getString(R.string.err_email_empty));
                    correctForm = false;
                }

                if (password.trim().equals("")) {
                    vEditTextPassword.setError(getResources().getString(R.string.err_password_empty));
                    correctForm = false;
                }

                if (!password.equals(passwordConfirm)) {
                    vEditTextPasswordConfirm.setError(getResources().getString(R.string.err_password_match));
                    correctForm = false;
                }

                if (correctForm) {
                    User u = new User(firstname, lastname, email, password);
                    Register(u);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    private void Register(final User u) {
        Call call = apiInterface.createUser(u);
        //progress
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.registreren));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("API event", response.message());

                if(response.isSuccessful()) {
                    Fragment login_fragment = new LoginFragment();

                    if (login_fragment != null){
                        Bundle bundle = new Bundle();
                        bundle.putString("email", u.getEmail());
                        login_fragment.setArguments(bundle);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_login, login_fragment);
                        ft.commit();
                    }
                } else {
                    Toast.makeText(context, R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(context, R.string.geen_verbinding, Toast.LENGTH_SHORT).show();
                call.cancel();
                progressDialog.dismiss();
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
