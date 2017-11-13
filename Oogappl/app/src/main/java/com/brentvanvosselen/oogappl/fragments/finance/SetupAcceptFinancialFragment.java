package com.brentvanvosselen.oogappl.fragments.finance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.FinInfo;

public class SetupAcceptFinancialFragment extends Fragment {

    private FinInfo info;

    public interface OnAcceptFinancial {
        void onAcceptFinancial(boolean accept);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final OnAcceptFinancial mCallback = (OnAcceptFinancial) getActivity();

        info = (FinInfo) getArguments().getSerializable("fintype");

        TextView type = getView().findViewById(R.id.textView_accept_type);
        type.setText(this.info.getType());

        TextView infoTitle = getView().findViewById(R.id.textView_accept_extra_info_title);
        TextView infoValue = getView().findViewById(R.id.textView_accept_extra_info_value);

        if(this.info.getType().equals("kindrekening")) {
            FinInfo.Kindrekening kindrekening = this.info.getKindrekening();
            infoTitle.setText(getResources().getString(R.string.max_bedrag) + ":");
            if(kindrekening == null) {
                Log.i("KINDREK", "NULL");
                infoValue.setText(getResources().getString(R.string.geen_max_bedrag));
            } else {
                int maxBedrag = kindrekening.getMaxBedrag();
                if(maxBedrag > 0) {
                    infoValue.setText(Double.toString(maxBedrag));
                }
            }

        } else if(this.info.getType().equals("onderhoudsbijdrage")) {
            FinInfo.Onderhoudsbijdrage onderhoudsbijdrage = this.info.getOnderhoudsbijdrage();
            infoTitle.setText(getResources().getString(R.string.bijdrage_percentage_kort) + ":");
            infoValue.setText(onderhoudsbijdrage.getPercentage());

        } else {
            Log.i("FINTYPE ACCEPT", "VERKEERD TYPE");
        }

        Button accept = getView().findViewById(R.id.button_finance_accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onAcceptFinancial(true);
            }
        });

        Button decline = getView().findViewById(R.id.button_finance_not_accept);

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onAcceptFinancial(false);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_finance_accept,container,false);
    }
}
