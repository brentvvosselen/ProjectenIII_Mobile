package com.brentvanvosselen.oogappl.fragments.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.fragments.login.RegisterFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.activities.MainActivity;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
    private String registerEmail;

    private TextView vTextViewRegister;
    private Button vButtonRegister;
    private EditText vEditTextEmail;
    private EditText vEditTextPassword;
    private AppCompatActivity activity;
    private Context context;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View content = getView();
        activity = (AppCompatActivity) getActivity();
        context = getActivity().getApplicationContext();

        // Wanneer van RegisterFragment komen, email invullen

        Bundle bundle = (Bundle) getArguments();
        if(bundle != null) {
            this.registerEmail = bundle.getString("email");
        }

        if(registerEmail != null) {
            TextView vTextViewEmail = content.findViewById(R.id.edittext_login_email);
            vTextViewEmail.setText(this.registerEmail);
        }

        this.vTextViewRegister = content.findViewById(R.id.textview_button_createaccount);
        vTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment register_fragment = new RegisterFragment();

                if (register_fragment != null){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_login,register_fragment, "CURRENT_FRAGMENT");
                    ft.detach(getFragmentManager().findFragmentById(R.id.content_login));
                    ft.commit();
                }
            }
        });

        vButtonRegister = content.findViewById(R.id.button_login);
        vEditTextEmail = content.findViewById(R.id.edittext_login_email);
        vEditTextPassword = content.findViewById(R.id.edittext_login_password);
        vButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = vEditTextEmail.getText().toString();
                String password = vEditTextPassword.getText().toString();

                if(email.trim().isEmpty()) {
                    vEditTextEmail.setError("Email can't be empty");
                } else if (password.trim().isEmpty()) {
                    vEditTextPassword.setError("Password can't be empty");
                } else {
                    User u = new User(email, password);
                    login(u);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void login(final User u) {
        Call call = apiInterface.loginUser(u);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    Log.i("LOGIN", "SUCCESS");

                    Parent p = (Parent)response.body();
                    Log.i("VALUE",p.getToken());
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("token",p.getToken()).apply();


                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("currentUser", (Parcelable) u);
                    saveUser(u);

                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN", "FAIL: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(context, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    public void onBackPressed() {
        getActivity().finishAffinity();
    }

    private void saveUser(User u){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments",Context.MODE_PRIVATE);
        String serialized = ObjectSerializer.serialize2(u);
        try{
            sharedPreferences.edit().putString("currentUser", serialized).apply();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
