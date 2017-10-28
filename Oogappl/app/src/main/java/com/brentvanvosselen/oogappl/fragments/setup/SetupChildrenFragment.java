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

import com.brentvanvosselen.oogappl.ChildSetupItem;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.Child;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class SetupChildrenFragment extends Fragment {

    public interface OnEndSelected{
        public void onEndSetup(List<Child> children);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View child = inflater.inflate(R.layout.setup_child,null);
        final ViewGroup main = getView().findViewById(R.id.linearlayout_setup_children);
        main.addView(child);

        ImageButton vButtonAddChild = getView().findViewById(R.id.imagebutton_add_child);
        vButtonAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View newChild = inflater.inflate(R.layout.setup_child,null);
                main.addView(newChild);
            }
        });

        Button vButtonEndSetup = getView().findViewById(R.id.button_end_setup);
        vButtonEndSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Child> children = new ArrayList<>();
                for(int i = 0; i< main.getChildCount(); i++){
                    EditText vEdittextGender = main.getChildAt(i).findViewById(R.id.edittext_setup_child_gender);
                    EditText vEdittextFirstname = main.getChildAt(i).findViewById(R.id.edittext_setup_child_firstname);
                    EditText vEdittextLastname = main.getChildAt(i).findViewById(R.id.edittext_setup_child_lastname);
                    EditText vEdittextBirthdate = main.getChildAt(i).findViewById(R.id.edittext_setup_child_birthdate);
                    children.add(new Child(vEdittextFirstname.getText().toString(),vEdittextLastname.getText().toString(),vEdittextGender.getText().toString(),Integer.parseInt(vEdittextBirthdate.getText().toString())));
                }

                OnEndSelected mCallback = (OnEndSelected) getActivity();
                mCallback.onEndSetup(children);


            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_children,container,false);
    }
}
