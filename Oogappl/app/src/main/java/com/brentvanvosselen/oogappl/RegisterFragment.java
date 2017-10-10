package com.brentvanvosselen.oogappl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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


                if(vEditTextPassword.getText() != vEditTextPasswordConfirm.getText()) {
                    Log.i("Event", "");
                }


                Log.i("event","register button click");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}
