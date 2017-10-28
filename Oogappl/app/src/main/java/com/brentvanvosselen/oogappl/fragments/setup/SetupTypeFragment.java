package com.brentvanvosselen.oogappl.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brentvanvosselen.oogappl.R;

/**
 * Created by brentvanvosselen on 28/10/2017.
 */

public class SetupTypeFragment extends Fragment {

    public interface OnTypeSelected{
        public void onTypeSelect(char type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button vButtonMother = getView().findViewById(R.id.button_setup_mother);
        vButtonMother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnTypeSelected mCallback = (OnTypeSelected)getActivity();
                mCallback.onTypeSelect('M');
            }
        });

        Button vButtonFather = getView().findViewById(R.id.button_setup_father);
        vButtonFather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnTypeSelected mCallback = (OnTypeSelected)getActivity();
                mCallback.onTypeSelect('F');
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_type, container, false);
    }
}
