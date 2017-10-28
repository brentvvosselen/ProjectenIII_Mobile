package com.brentvanvosselen.oogappl.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.fragments.setup.SetupChildrenFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupOtherParentFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupTypeFragment;

public class SetupActivity extends AppCompatActivity implements SetupTypeFragment.OnTypeSelected,SetupOtherParentFragment.OnParentNextSelected {

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

}
