package com.brentvanvosselen.oogappl.fragments.finance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;


public class SetupKindrekeningFragment extends Fragment {

    public interface OnKindrekeningSelected {
        void onKindrekeningSelected(int bedrag);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final OnKindrekeningSelected mCallback = (OnKindrekeningSelected) getActivity();
        final EditText editTextMaxBedrag = getView().findViewById(R.id.editText_max_bedrag);
        final TextView textViewMaxBedrag = getView().findViewById(R.id.textView_max_bedrag);

        editTextMaxBedrag.setVisibility(View.GONE);
        textViewMaxBedrag.setVisibility(View.GONE);

        final CheckBox useMax = getView().findViewById(R.id.checkBox_use_max);
        useMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    editTextMaxBedrag.setVisibility(View.VISIBLE);
                    textViewMaxBedrag.setVisibility(View.VISIBLE);
                } else {
                    editTextMaxBedrag.setVisibility(View.GONE);
                    textViewMaxBedrag.setVisibility(View.GONE);
                }
            }
        });

        Button end = getView().findViewById(R.id.button_finance_einde);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(useMax.isChecked()) {
                    String bedragString = editTextMaxBedrag.getText().toString();
                    if(bedragString.trim().equals("")) {
                        editTextMaxBedrag.setError(getResources().getString(R.string.max_bedrag_error));
                    } else {
                        int bedrag = Integer.parseInt(bedragString);
                        mCallback.onKindrekeningSelected(bedrag);
                    }
                } else {
                    mCallback.onKindrekeningSelected(-1);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_finance_kindrekening,container,false);
    }
}
