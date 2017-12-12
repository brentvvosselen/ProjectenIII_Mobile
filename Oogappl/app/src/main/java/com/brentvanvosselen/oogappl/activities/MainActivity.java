package com.brentvanvosselen.oogappl.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaEditItemFragment;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaDayFragment;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaFragment;
import com.brentvanvosselen.oogappl.fragments.calendar.AgendaItemFragment;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerAddFragment;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerDayFragment;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerFragment;
import com.brentvanvosselen.oogappl.fragments.heenenweer.HeenEnWeerItemEditFragment;
import com.brentvanvosselen.oogappl.fragments.main.ChildInfoFragment;
import com.brentvanvosselen.oogappl.fragments.finance.FinanceFragment;
import com.brentvanvosselen.oogappl.fragments.main.HomeFragment;
import com.brentvanvosselen.oogappl.fragments.main.ProfileFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AgendaFragment.OnCalendarItemSelected, HeenEnWeerFragment.OnHeenEnWeerAction{

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

        final SharedPreferences sharedPrefs = this.getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
        // sharedPrefs.edit().remove("currentUser").commit();

        String serialized = sharedPrefs.getString("currentUser", null);

        if(serialized != null) {
            currentUser = ObjectSerializer.deserialize2(serialized);
        }

        if(currentUser == null) {
            goToLogin();
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
        final CircularImageView vImageViewProfile = headerView.findViewById(R.id.profile_imageview);

        vImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySelectedScreen(R.id.profile_imageview);
            }
        });

        ImageButton logout = headerView.findViewById(R.id.imageButton_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefs.edit().remove("currentUser").commit();
                goToLogin();
            }
        });

        //show name and type in navigation bar if currentUser != null
        if(currentUser!=null){
            final TextView vTextViewProfileName = headerView.findViewById(R.id.profile_name);
            final TextView vTextViewProfileType = headerView.findViewById(R.id.profile_type);
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);
            Call call = apiInterface.getParentByEmail("bearer " + sharedPreferences.getString("token",null), currentUser.getEmail());
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.isSuccessful()){
                        Parent p = (Parent)response.body();
                        vTextViewProfileName.setText(p.getFirstname() + " " + p.getLastname());

                        //byte[] decodedString = Base64.decode(p.getPicture().getValue(),Base64.DEFAULT);
                        //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                        //vImageViewProfile.setImageBitmap(decodedByte);
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
                    Log.i("API event", getResources().getString(R.string.geen_verbinding));
                    sharedPrefs.edit().remove("currentUser").commit();
                    goToLogin();
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
            case R.id.nav_heenenweer:
                fragment = new HeenEnWeerFragment();
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
        ft.addToBackStack("view_day");
        ft.replace(R.id.content_main,dayFragment);
        ft.commit();
    }

    @Override
    public void onItemSelected(String id) {
        Fragment agendaItemFragment = AgendaItemFragment.newInstance(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("view_event");
        ft.replace(R.id.content_main,agendaItemFragment);
        ft.commit();
    }

    @Override
    public void onAddItemSelected() {
        Fragment agendaAddItemFragment = new AgendaEditItemFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,agendaAddItemFragment);
        ft.addToBackStack("add_event");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onItemEdit(String eventId) {
        Fragment agendaEditItemFragment = AgendaEditItemFragment.newInstance(eventId);
        FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, agendaEditItemFragment);
        ft.addToBackStack("edit_event");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onItemDeleted() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        fm.popBackStack();
        displaySelectedScreen(R.id.nav_agenda);
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void showDay(String id) {
        Fragment heenEnWeerDayFragment = HeenEnWeerDayFragment.newInstance(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,heenEnWeerDayFragment);
        ft.addToBackStack("heenenweer_day");
        ft.commit();
    }

    @Override
    public void onEditItem(HeenEnWeerItem item) {
        Fragment heenenweerEditItemFragment = HeenEnWeerItemEditFragment.newInstance(item);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,heenenweerEditItemFragment);
        ft.addToBackStack("heenenweer_edit_item");
        ft.commit();
    }

    @Override
    public void onAddItem(String dayid) {
        Fragment heenenweerAddItemFragment = HeenEnWeerItemEditFragment.newInstance(dayid);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,heenenweerAddItemFragment);
        ft.addToBackStack("heenenweer_add_item");
        ft.commit();
    }

    @Override
    public void onAddDay() {
        Fragment heenenweerAddDayFragment = new HeenEnWeerAddFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main,heenenweerAddDayFragment);
        ft.addToBackStack("heenenweer_add");
        ft.commit();
    }
}
