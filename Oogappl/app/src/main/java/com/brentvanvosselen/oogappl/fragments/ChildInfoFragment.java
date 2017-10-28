package com.brentvanvosselen.oogappl.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.ChildInfoView;
import com.brentvanvosselen.oogappl.ObjectSerializer;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.User;
import com.brentvanvosselen.oogappl.activities.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoFragment extends Fragment {

    private Parent parent;
    private View view;


    public ChildInfoFragment(){
        setHasOptionsMenu(true);
    }
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
        this.view = inflater.inflate(R.layout.fragment_childinfo, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onResume() {
        super.onResume();
        initFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_child,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_child){
            //create an alert dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            //inflate custom dialog
            final View mView = inflater.inflate(R.layout.dialog_add_child,null);

            final EditText vEdittextGender = mView.findViewById(R.id.edittext_add_child_gender);
            final EditText vEdittextFirstname = mView.findViewById(R.id.edittext_add_child_firstname);
            final EditText vEdittextLastname = mView.findViewById(R.id.edittext_add_child_lastname);
            final EditText vEdittextBirthdate = mView.findViewById(R.id.edittext_add_child_birthdate);
            //set the custom dialog to the alertDialogBuilder and add 2 buttons
            builder.setView(mView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            //create new child
                            final Child child = new Child(vEdittextFirstname.getText().toString(),vEdittextLastname.getText().toString(),vEdittextGender.getText().toString(),Integer.parseInt(vEdittextBirthdate.getText().toString()),true);
                            //get user from localstorage
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
                            User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
                            //create call to save child
                            Call callUser = RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
                            callUser.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    if(response.isSuccessful()){
                                        Parent parent = (Parent) response.body();
                                        Call callChild = RetrofitClient.getClient().create(APIInterface.class).addChild(parent.getId(),child);
                                        callChild.enqueue(new Callback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                if(response.isSuccessful()){
                                                    Toast.makeText(getContext(),"New child created", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(),"Not saved", Toast.LENGTH_SHORT).show();
                                                }
                                                dialogInterface.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call call, Throwable t) {
                                                Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getContext(),"Not saved", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {
                                    Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFragment() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));
        Call call =  RetrofitClient.getClient().create(APIInterface.class).getParentByEmail(currentUser.getEmail());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    parent = (Parent) response.body();
                    ChildInfoView childInfoView = view.findViewById(R.id.childInfo);
                    if(parent.getChildren() == null ) {
                        Log.i("Children", "no children...");
                    }
                    childInfoView.setVariables(parent);
                } else {
                    Toast.makeText(getContext(), "Call failed", Toast.LENGTH_SHORT).show();
                    Log.i("LOGIN", "FAIL: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
}
