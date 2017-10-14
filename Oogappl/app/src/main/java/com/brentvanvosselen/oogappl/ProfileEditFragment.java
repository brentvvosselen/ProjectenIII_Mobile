package com.brentvanvosselen.oogappl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 14/10/2017.
 */

public class ProfileEditFragment extends Fragment {

    //declare the API-interface
    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    //declare the editTexts
    private EditText vEditTextStreet, vEditTextNumber, vEditTextPostalcode, vEditTextCity, vEditTextTelNumber, vEditTextWorkName, vEditTextWorkTelNumber;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.edit);

        //get the content
        View content = getView();

        //get the edittexts from view
        vEditTextStreet = content.findViewById(R.id.edittext_profile_edit_address_street);
        vEditTextNumber = content.findViewById(R.id.edittext_profile_edit_address_number);
        vEditTextPostalcode = content.findViewById(R.id.edittext_profile_edit_address_postalcode);
        vEditTextCity = content.findViewById(R.id.edittext_profile_edit_address_city);
        vEditTextTelNumber = content.findViewById(R.id.edittext_profile_edit_telephone);
        vEditTextWorkName = content.findViewById(R.id.edittext_profile_edit_work_name);
        vEditTextWorkTelNumber = content.findViewById(R.id.edittext_profile_edit_work_number);

        fillTextFields();

        //save button
        Button vButtonSave = content.findViewById(R.id.button_profile_edit_save);
        vButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the profile
                updateProfile();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit,container,false);
    }


    /**
     * Fills the edittexts with the current data of the userprofile
     */
    private void fillTextFields(){

        Call call = apiInterface.getParentByEmail(((MainActivity)getActivity()).getUserEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent parent = (Parent)response.body();
                    vEditTextStreet.setText(parent.getAddressStreet());
                    vEditTextNumber.setText(parent.getAddressNumber());
                    vEditTextPostalcode.setText(parent.getAddressPostalcode());
                    vEditTextCity.setText(parent.getAddressCity());
                    vEditTextTelNumber.setText(parent.getTelephoneNumber());
                    vEditTextWorkName.setText(parent.getWorkName());
                    vEditTextWorkTelNumber.setText(parent.getWorkNumber());
                }else{
                    Toast.makeText(getContext(),"Could not load profile information",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API EVENT",t.getMessage());
                call.cancel();
            }
        });
    }


    /**
     *  updates the user profile
     */
    private void updateProfile(){

        Call call = apiInterface.getParentByEmail(((MainActivity) getActivity()).getUserEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent parent =  (Parent) response.body();
                    //fill values from edittexts
                    parent.setAddressStreet(vEditTextStreet.getText().toString());
                    parent.setAddressNumber(vEditTextNumber.getText().toString());
                    parent.setAddressPostalcode(vEditTextPostalcode.getText().toString());
                    parent.setAddressCity(vEditTextCity.getText().toString());
                    parent.setTelephoneNumber(vEditTextTelNumber.getText().toString());
                    parent.setWorkName(vEditTextWorkName.getText().toString());
                    parent.setWorkNumber(vEditTextWorkTelNumber.getText().toString());

                    //save changes
                    Call callSave = apiInterface.saveProfile(parent);
                    callSave.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getContext(),"The profile is updated",Toast.LENGTH_SHORT).show();
                                Log.i("API-EVENT", "updated profile SUCESSFUL");
                                //go back to profile
                                Fragment fragment = new ProfileFragment();
                                if(fragment != null){
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_main,fragment);
                                    ft.commit();
                                }
                            }else{
                                Toast.makeText(getContext(),"The profile is not updated, Something went wrong.",Toast.LENGTH_SHORT).show();
                                Log.i("API-EVENT", "updated profile FAILED");
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Log.i("API-EVENT", t.getMessage());
                            call.cancel();
                        }
                    });
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

    }

}
