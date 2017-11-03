package com.brentvanvosselen.oogappl.activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaDayFragment;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaFragment;
import com.brentvanvosselen.oogappl.fragments.main.ChildInfoFragment;
import com.brentvanvosselen.oogappl.fragments.main.FinanceFragment;
import com.brentvanvosselen.oogappl.fragments.main.HomeFragment;
import com.brentvanvosselen.oogappl.fragments.main.ProfileFragment;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AgendaFragment.OnCalendarItemSelected {

    // private Boolean loggedIn = false;
    private User currentUser;
    private Parent parent;

    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        currentUser = intent.getParcelableExtra("currentUser");

        if(currentUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }else{
            //navigate to the home fragment
            displaySelectedScreen(R.id.nav_home);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Use the headerView to get the items from the header in the navigation drawer
        View headerView = navigationView.getHeaderView(0);

        //When the user clicks on the image in the navigation drawer, navigate to the profile fragment
        final ImageView vImageViewProfile = headerView.findViewById(R.id.profile_imageview);
        vImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySelectedScreen(R.id.profile_imageview);
            }
        });

        //show name and type in navigation bar if currentUser != null
        if(currentUser!=null){
            final TextView vTextViewProfileName = headerView.findViewById(R.id.profile_name);
            final TextView vTextViewProfileType = headerView.findViewById(R.id.profile_type);
            Call call = apiInterface.getParentByEmail(currentUser.getEmail());
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()){
                        Parent p = (Parent)response.body();
                        vTextViewProfileName.setText(p.getFirstname() + " " + p.getLastname());
                        if(p.getType() != null){
                            switch (p.getType()){
                                case "M": vTextViewProfileType.setText(R.string.mother);
                                    break;
                                case "F": vTextViewProfileType.setText(R.string.father);
                                    break;
                                default:
                                    vTextViewProfileType.setText("");
                            }
                        }
                    }else{
                        Log.i("API event", "not succesful to get user");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.i("API event", "Fail to get user");
                }
            });
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CURRENT_FRAGMENT");

        if (fragment instanceof ChildInfoFragment) {
            ((ChildInfoFragment) fragment).onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * This method is used to show the desired fragment (Screen) based on the id
     * @param id This is the layout id
     */

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch(id){
            case R.id.profile_imageview:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_childInfo:
                fragment = new ChildInfoFragment();
                break;
            case R.id.nav_agenda:
                fragment = new AgendaFragment();
                break;
            case R.id.nav_finance:
                fragment = new FinanceFragment();
                break;
        }

        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment, "CURRENT_FRAGMENT");
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }
    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }


    public String getUserEmail(){
        return currentUser.getEmail();
    }

    @Override
    public void onDateSelected(Date date) {
        Fragment dayFragment = AgendaDayFragment.newInstance(date);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,dayFragment);
        ft.commit();
    }
}
