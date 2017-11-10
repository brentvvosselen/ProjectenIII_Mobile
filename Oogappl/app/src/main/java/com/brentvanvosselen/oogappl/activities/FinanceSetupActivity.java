package com.brentvanvosselen.oogappl.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.RestClient.models.FinInfo;
import com.brentvanvosselen.oogappl.RestClient.models.FinancialType;
import com.brentvanvosselen.oogappl.RestClient.models.Group;
import com.brentvanvosselen.oogappl.RestClient.models.OnderhoudsbijdrageType;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupFinancialFragment;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupKindrekeningFragment;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupOnderhoudsbijdrageFragment;
import com.brentvanvosselen.oogappl.fragments.financeSetup.SetupOnderhoudsbijdragePercentageFragment;
import com.brentvanvosselen.oogappl.fragments.setup.SetupTypeFragment;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FinanceSetupActivity extends AppCompatActivity implements
    SetupFinancialFragment.OnFinancialSelected,
    SetupKindrekeningFragment.OnKindrekeningSelected,
    SetupOnderhoudsbijdrageFragment.OnOnderhoudsbijdrageSelect,
    SetupOnderhoudsbijdragePercentageFragment.OnOnderhoudsbijdragepercentageSelect {

    private APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    private Parent parent;

    private FinancialType financialType;

    // Info kindrekening
    private int kindRekeningMaxBedrag;

    // Info onderhoudsbijdrage
    private OnderhoudsbijdrageType onderhoudsType;
    private int onderhoudsBijdragePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent intent = this.getIntent();
        parent = ObjectSerializer.deserialize2(intent.getStringExtra("parent"));

        //start type fragment
        Fragment setupFinancialFragment = new SetupFinancialFragment();
        displayScreen(setupFinancialFragment, R.id.content_setup, false);
    }

    private void displayScreen(Fragment fragment, int id, boolean addToBackstack){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id,fragment);
        if(addToBackstack) {
            ft.addToBackStack(fragment.getClass().getSimpleName());
        }
        ft.commit();
    }

    @Override
    public void onFinancialSelected(FinancialType type) {
        this.financialType = type;

        Fragment nextFragment = null;

        if(financialType == FinancialType.ONDERHOUDSBIJDRAGE) {
            nextFragment = new SetupOnderhoudsbijdrageFragment();
        } else if (financialType == FinancialType.KINDREKENING) {
            nextFragment = new SetupKindrekeningFragment();
        }

        displayScreen(nextFragment, R.id.content_setup, true);
    }

    @Override
    public void onKindrekeningSelected(int bedrag) {
        if(bedrag > 0) {
            this.kindRekeningMaxBedrag = bedrag;
        }

        sendInfo();

        goToMain();
    }

    @Override
    public void onOnderhoudsbijdrageSelect(OnderhoudsbijdrageType type) {
        onderhoudsType = type;

        Fragment setupFinancialFragment = new SetupOnderhoudsbijdragePercentageFragment();
        displayScreen(setupFinancialFragment, R.id.content_setup, true);
    }

    @Override
    public void onOnderhoudsbijdragepercentageSelec(int percentage) {
        this.onderhoudsBijdragePercentage = percentage;
        sendInfo();

        goToMain();
    }

    private void sendInfo() {
        FinInfo info;

        if(financialType == FinancialType.KINDREKENING) {
            info = new FinInfo(parent, kindRekeningMaxBedrag);
        } else {
            info = new FinInfo(parent, (onderhoudsType == OnderhoudsbijdrageType.GERECHTIGDE), onderhoudsBijdragePercentage);
        }

        parent.getGroup().setFinType(info);

        Log.i("GROUP", parent.getGroup().toString());

        Call call = apiInterface.addFinanceInfo(parent.getGroup());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    Log.i("SUCCESFULL", "" + response.body());
                } else {
                    Log.i("UNSUCCESFULL", "BAD RESPONSE");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("UNSUCCESFULL", "FAIL");
            }
        });
    }

    private void goToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
