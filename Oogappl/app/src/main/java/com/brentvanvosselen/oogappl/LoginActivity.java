package com.brentvanvosselen.oogappl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment login_fragment = new LoginFragment();

        if (login_fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_login, login_fragment, "CURRENT_FRAGMENT");
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CURRENT_FRAGMENT");

        if(fragment instanceof RegisterFragment) {
            ((RegisterFragment) fragment).onBackPressed();
        } else if (fragment instanceof LoginFragment) {
            ((LoginFragment) fragment).onBackPressed();
        }
    }
}
