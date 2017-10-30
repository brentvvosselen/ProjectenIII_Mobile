package com.brentvanvosselen.oogappl.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.brentvanvosselen.oogappl.R;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class SetupOtherParentFragment extends Fragment {

    public interface OnParentNextSelected{
        public void onNextSelect(String email, String firstname, String lastname);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText vEditTextEmail = getView().findViewById(R.id.edittext_setup_parent_email);
        final EditText vEditTextFirstname = getView().findViewById(R.id.edittext_setup_parent_firstname);
        final EditText vEditTextLastname = getView().findViewById(R.id.edittext_setup_parent_lastname);

        ImageButton vButtonNext = getView().findViewById(R.id.button_setup_parent_next);
        vButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctform = true;
                String email = vEditTextEmail.getText().toString();
                String firstname = vEditTextFirstname.getText().toString();
                String lastname = vEditTextLastname.getText().toString();
                if(firstname.trim().equals("") || firstname.trim().length() < 3 ){
                    vEditTextFirstname.setError("De voornaam moet minstens 3 karakters bevatten");
                    correctform = false;
                }
                if(lastname.trim().equals("") || lastname.trim().length() < 3 ){
                    vEditTextLastname.setError("De achternaam moet minstens 3 karakters bevatten");
                    correctform = false;
                }
                if(!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
                    vEditTextEmail.setError("Dit is geen geldig email-adres");
                    correctform = false;
                }

                if(correctform){
                    OnParentNextSelected mCallback = (OnParentNextSelected) getActivity();
                    mCallback.onNextSelect(email,firstname,lastname);
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_other_parent,container,false);
    }
}
