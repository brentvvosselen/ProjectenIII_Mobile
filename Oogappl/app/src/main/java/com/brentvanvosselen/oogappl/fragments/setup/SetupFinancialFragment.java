package com.brentvanvosselen.oogappl.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private String[] types = {"Onderhoudsbijdrage", "Kindrekening"};

    public interface OnFinancialSelected {
        public void onFinancialSelected(FinancialType type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> finTypes = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, types);
        final Spinner finSpinner = getView().findViewById(R.id.spinner_financial_type);
        finSpinner.setAdapter(finTypes);

        Button pickFinancial = getView().findViewById(R.id.button_financial_type);
        pickFinancial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnFinancialSelected mCallback = (OnFinancialSelected) getActivity();
                String type = (String) finSpinner.getSelectedItem();

                if(type.equals(types[0])) {
                    mCallback.onFinancialSelected(FinancialType.ONDERHOUDSBIJDRAGE);
                } else if (type.equals(types[1])) {
                    mCallback.onFinancialSelected(FinancialType.ONDERHOUDSBIJDRAGE);
                } else {
                    Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_financial,container,false);
    }
}
