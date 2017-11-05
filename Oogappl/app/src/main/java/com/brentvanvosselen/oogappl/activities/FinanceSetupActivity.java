package com.brentvanvosselen.oogappl.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.FinancialType;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupFinancialFragment;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupKindrekeningFragment;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupOnderhoudsbijdrageFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupTypeFragment;


public class FinanceSetupActivity extends AppCompatActivity implements
    SetupFinancialFragment.OnFinancialSelected {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private FinancialType financialType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //start type fragment
        Fragment setupFinancialFragment = new SetupFinancialFragment();
        displayScreen(setupFinancialFragment, R.id.content_setup);
    }

    private void displayScreen(Fragment fragment, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id,fragment);
        ft.commit();
    }

    @Override
    public void onFinancialSelected(FinancialType type) {
        this.financialType = type;

        Fragment nextFragment = null;

        if(type == FinancialType.ONDERHOUDSBIJDRAGE) {
            nextFragment = new SetupOnderhoudsbijdrageFragment();
        } else if (type == FinancialType.KINDREKENING) {
            nextFragment = new SetupKindrekeningFragment();
        }

        displayScreen(nextFragment, R.id.content_setup);

        sendInfo();
    }

    private void sendInfo() {

    }
}
