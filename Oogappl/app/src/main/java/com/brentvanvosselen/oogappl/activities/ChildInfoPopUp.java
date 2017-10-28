package com.brentvanvosselen.oogappl.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoPopUp extends Activity {

    private int childIndex;
    private ArrayList<String> catNames;
    private Child child;

    private Spinner s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null) {
            intent.getIntExtra("childInfo", childIndex);
            catNames = intent.getStringArrayListExtra("catNames");
            child = ObjectSerializer.deserialize2(intent.getStringExtra("child"));
        }

        setContentView(R.layout.popup_childinfo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (heigth * .8));

        s = findViewById(R.id.spinner_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, catNames);
        s.setAdapter(adapter);

        Button buttonSave = this.findViewById(R.id.button_save_info);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int catIndex = s.getSelectedItemPosition();
                String name = ((EditText) findViewById(R.id.editText_childInfo_name)).getText().toString();
                String value = ((EditText) findViewById(R.id.editText_childInfo_value)).getText().toString();
                child.addInfo(catIndex, name, value);

                Call call =  RetrofitClient.getClient().create(APIInterface.class).saveChild(child);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if(response.isSuccessful()) {
                            Log.i("SAVE CHILD", "SUCCESFULL");
                        } else {
                            Log.i("SAVE CHILD", "UNSUCCESFULL");
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.i("SAVE CHILD", "SUCCESFULL");
                        call.cancel();
                    }
                });

                finish();
            }
        });

        Button buttonCancel = this.findViewById(R.id.button_cancel_info);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
