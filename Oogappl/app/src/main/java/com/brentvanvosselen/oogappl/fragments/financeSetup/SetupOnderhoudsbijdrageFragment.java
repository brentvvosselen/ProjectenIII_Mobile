package com.brentvanvosselen.oogappl.fragments.financeSetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.OnderhoudsbijdrageType;

public class SetupOnderhoudsbijdrageFragment extends Fragment{

    public interface OnOnderhoudsbijdrageSelect {
        void onOnderhoudsbijdrageSelect(OnderhoudsbijdrageType type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final OnOnderhoudsbijdrageSelect mCallback = (OnOnderhoudsbijdrageSelect) getActivity();

        Button onderhoudsplichtigde = getView().findViewById(R.id.button_onderhoudsplichtigde);
        onderhoudsplichtigde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onOnderhoudsbijdrageSelect(OnderhoudsbijdrageType.PLICHTIG);
            }
        });

        Button onderhoudsgerechtigde = getView().findViewById(R.id.button_onderhoudsgerechtigde);
        onderhoudsgerechtigde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onOnderhoudsbijdrageSelect(OnderhoudsbijdrageType.GERECHTIGDE);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_finance_onderhoudsbijdrage,container,false);
    }
}
