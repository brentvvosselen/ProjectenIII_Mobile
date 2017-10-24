package com.brentvanvosselen.oogappl.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

<<<<<<< HEAD:Oogappl/app/src/main/java/com/brentvanvosselen/oogappl/ChildInfoFragment.java
import com.brentvanvosselen.oogappl.RestClient.Parent;
=======
import com.brentvanvosselen.oogappl.R;
>>>>>>> 5c2763961514b7189a77de83786fac6833eb8c41:Oogappl/app/src/main/java/com/brentvanvosselen/oogappl/fragments/ChildInfoFragment.java

public class ChildInfoFragment extends Fragment {

    private Parent parent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.childinfo);

        initFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_childinfo,container,false);
    }

    private void initFragment() {

    }
}
