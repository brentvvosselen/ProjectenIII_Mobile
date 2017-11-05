package com.brentvanvosselen.oogappl.fragments.financeSetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.OnderhoudsbijdrageType;

public class SetupOnderhoudsbijdragePercentageFragment extends Fragment {

    public interface OnOnderhoudsbijdragepercentageSelect {
        void onOnderhoudsbijdragepercentageSelec(int percentage);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final OnOnderhoudsbijdragepercentageSelect mCallback = (OnOnderhoudsbijdragepercentageSelect) getActivity();

        final EditText editTextPercentage = getView().findViewById(R.id.editText_bijdragepercentage);
        editTextPercentage.setText("50");

        editTextPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String percentage = editTextPercentage.getText().toString().trim();
                if(percentage.equals("")) {
                    editTextPercentage.setError(getResources().getString(R.string.percentage_error));
                } else {
                    int bedrag = Integer.parseInt(percentage);
                    if(bedrag < 0 || bedrag > 100) {
                        editTextPercentage.setError(getResources().getString(R.string.percentage_min_max));
                    }
                }
            }
        });

        Button end = getView().findViewById(R.id.button_finance_einde);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String percentage = editTextPercentage.getText().toString();
                mCallback.onOnderhoudsbijdragepercentageSelec(Integer.parseInt(percentage));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_finance_onderhoudsbijdrage_percentage,container,false);
    }
}
