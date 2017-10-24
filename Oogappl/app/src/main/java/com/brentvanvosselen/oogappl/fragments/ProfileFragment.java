package com.brentvanvosselen.oogappl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.activities.MainActivity;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 04/10/2017.
 */

public class ProfileFragment extends Fragment {

    //declaration fo the api-interface
    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);


    TextView vTextViewEmail, vTextViewFirstname, vTextViewLastname, vTextViewAddress, vTextViewTelephone, vTextViewWork;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.profile);

        //getting the textviews
        final View content = getView();
        vTextViewEmail = content.findViewById(R.id.textview_profile_email);
        vTextViewFirstname = content.findViewById(R.id.textview_profile_firstname);
        vTextViewLastname = content.findViewById(R.id.textview_profile_lastname);
        vTextViewAddress = content.findViewById(R.id.textview_profile_address);
        vTextViewTelephone = content.findViewById(R.id.textview_profile_telephone);
        vTextViewWork = content.findViewById(R.id.textview_profile_work);

        //get the user from the api-server
        Call call = apiInterface.getParentByEmail(((MainActivity) getActivity()).getUserEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent parent = (Parent) response.body();
                    vTextViewEmail.setText(parent.getEmail());
                    vTextViewFirstname.setText(parent.getFirstname());
                    vTextViewLastname.setText(parent.getLastname());
                    vTextViewAddress.setText(parent.getAddressStreet() + " " + parent.getAddressNumber() + "\n" + parent.getAddressPostalcode() + " " + parent.getAddressCity());
                    vTextViewTelephone.setText(parent.getTelephoneNumber());
                    vTextViewWork.setText(parent.getWorkName() + "\n" + parent.getWorkNumber());

                }else{
                    Toast.makeText(getContext(), "Getting user information failed", Toast.LENGTH_SHORT).show();
                    Log.i("USER","FAIL: "+ response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API EVENT", t.getMessage());
                call.cancel();
            }
         });

        //go to editfragment when the user clicks on the edit button
        Button vButtonEdit = content.findViewById(R.id.button_profile_edit);
        vButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment editFragment = new ProfileEditFragment();
                if (editFragment != null){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main,editFragment);
                    ft.commit();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }
}
