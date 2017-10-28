package com.brentvanvosselen.oogappl.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.ChildSetupItem;
import com.brentvanvosselen.oogappl.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.SetupValues;
import com.brentvanvosselen.oogappl.RestClient.User;
import com.brentvanvosselen.oogappl.fragments.setup.SetupChildrenFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupOtherParentFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupTypeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupActivity extends AppCompatActivity implements SetupTypeFragment.OnTypeSelected,SetupOtherParentFragment.OnParentNextSelected, SetupChildrenFragment.OnEndSelected {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private char type;
    private String otherEmail, otherFirstname, otherLastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //start type fragment
        Fragment typeFragment = new SetupTypeFragment();
        displayScreen(typeFragment,R.id.content_setup);

    }


    @Override
    public void onTypeSelect(char type) {
        this.type = type;

        //start other parent fragment
        Fragment otherParentFragment = new SetupOtherParentFragment();
        displayScreen(otherParentFragment,R.id.content_setup);
    }


    @Override
    public void onNextSelect(String email, String firstname, String lastname) {
        this.otherEmail = email;
        this.otherFirstname = firstname;
        this.otherLastname = lastname;

        //start children fragment
        Fragment childrenFragment = new SetupChildrenFragment();
        displayScreen(childrenFragment,R.id.content_setup);
    }

    /***
     * displays the selected fragment on the selected id
     * @param fragment the new fragment you want to display
     * @param id what you want to replace
     */
    private void displayScreen(Fragment fragment, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id,fragment);
        ft.commit();
    }

    @Override
    public void onEndSetup(List<Child> children) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

        //convert children to array
        Child[] childrenArr = new Child[children.size()];
        childrenArr = children.toArray(childrenArr);
        //create the setup values object
        SetupValues values = new SetupValues(currentUser.getEmail(),String.valueOf(this.type),this.otherEmail,this.otherFirstname, this.otherLastname, childrenArr);

        Call call = apiInterface.completeSetup(values);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("API event", response.message());
                if(response.isSuccessful()){
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"complete setup failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                Toast.makeText(getApplicationContext(),"complete setup failed", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }
}
