package com.brentvanvosselen.oogappl.fragments.financeSetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.FinancialType;

public class SetupFinancialFragment extends Fragment {

    public interface OnFinancialSelected {
        void onFinancialSelected(FinancialType type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final OnFinancialSelected mCallback = (OnFinancialSelected) getActivity();

        Button kindrekening = getView().findViewById(R.id.button_finance_kindrekening);
        kindrekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFinancialSelected(FinancialType.KINDREKENING);
            }
        });

        Button onderhoudsbijdrage = getView().findViewById(R.id.button_finance_onderhoudersbijdrage);
        onderhoudsbijdrage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFinancialSelected(FinancialType.ONDERHOUDSBIJDRAGE);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_finance,container,false);
    }
}
