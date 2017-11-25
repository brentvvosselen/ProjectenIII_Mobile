package com.brentvanvosselen.oogappl.fragments.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.activities.MainActivity;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
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
    private SharedPreferences sharedPreferences;

    TextView vTextViewEmail, vTextViewFirstname, vTextViewAddress, vTextViewTelephone, vTextViewWork, vTextViewType;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.profile);

        //getting the textviews
        final View content = getView();
        vTextViewEmail = content.findViewById(R.id.textview_profile_email);
        vTextViewFirstname = content.findViewById(R.id.textview_profile_firstname);
        vTextViewAddress = content.findViewById(R.id.textview_profile_address);
        vTextViewTelephone = content.findViewById(R.id.textview_profile_telephone);
        vTextViewWork = content.findViewById(R.id.textview_profile_work);
        vTextViewType = content.findViewById(R.id.textview_profile_type);


        //get the user from the api-server
        Call call = apiInterface.getParentByEmail("bearer "+ sharedPreferences.getString("token",null),((MainActivity) getActivity()).getUserEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent parent = (Parent) response.body();
                    vTextViewEmail.setText((parent.getEmail() == null)?"":parent.getEmail());
                    vTextViewFirstname.setText(parent.getFirstname() + " " + parent.getLastname());
                    if((parent.getAddressStreet() == null  || parent.getAddressStreet().isEmpty()) && (parent.getAddressNumber() == null || parent.getAddressNumber().isEmpty()) && (parent.getAddressPostalcode() == null || parent.getAddressPostalcode().isEmpty()) && (parent.getAddressCity() == null || parent.getAddressCity().isEmpty())){
                        vTextViewAddress.setVisibility(View.GONE);
                    }
                    vTextViewAddress.setText(parent.getAddressStreet() + " " + parent.getAddressNumber() + "\n" + parent.getAddressPostalcode() + " " + parent.getAddressCity());
                    if(parent.getTelephoneNumber() == null || parent.getTelephoneNumber().isEmpty()){
                        vTextViewTelephone.setVisibility(View.GONE);
                    }else{

                        vTextViewTelephone.setText("Telefoonnummer: " + parent.getTelephoneNumber());
                    }
                    if((parent.getWorkName() == null || parent.getWorkName().isEmpty()) && (parent.getWorkNumber() == null || parent.getWorkNumber().isEmpty())){
                        vTextViewWork.setVisibility(View.GONE);
                    }
                    vTextViewWork.setText("Werkgegevens: " + parent.getWorkName() + "\n" + parent.getWorkNumber());
                    if(parent.getType() != null){
                        switch (parent.getType()){
                            case "M": vTextViewType.setText(R.string.mother);
                                break;
                            case "F": vTextViewType.setText(R.string.father);
                                break;
                            default:
                                vTextViewType.setText("");
                        }
                    }else{
                        vTextViewType.setText("");
                    }

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
        /*Button vButtonEdit = content.findViewById(R.id.button_profile_edit);
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
        });*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit){
            Fragment editFragment = new ProfileEditFragment();
            if (editFragment != null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main,editFragment);
                ft.commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
