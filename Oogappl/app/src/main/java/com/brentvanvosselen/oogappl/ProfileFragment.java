package com.brentvanvosselen.oogappl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by brentvanvosselen on 04/10/2017.
 */

public class ProfileFragment extends Fragment {
    TextView vTextViewEmail, vTextViewFirstname, vTextViewLastname, vTextViewAddress, vTextViewTelephone, vTextViewWork;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.profile);

        final View content = getView();
        vTextViewEmail = content.findViewById(R.id.textview_profile_email);
        vTextViewFirstname = content.findViewById(R.id.textview_profile_firstname);
        vTextViewLastname = content.findViewById(R.id.textview_profile_lastname);
        vTextViewAddress = content.findViewById(R.id.textview_profile_address);
        vTextViewTelephone = content.findViewById(R.id.textview_profile_telephone);
        vTextViewWork = content.findViewById(R.id.textview_profile_work);




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }
}
