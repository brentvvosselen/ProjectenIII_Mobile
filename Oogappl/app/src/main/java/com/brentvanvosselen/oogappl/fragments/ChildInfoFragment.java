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
            //custom dialog
           /* final Dialog mDialog = new Dialog(getContext());
            mDialog.setContentView(R.layout.dialog_add_child);
            mDialog.setTitle("Add a child");
            */

            //set custom components

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View mView = inflater.inflate(R.layout.dialog_add_child,null);

            builder.setView(mView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

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
