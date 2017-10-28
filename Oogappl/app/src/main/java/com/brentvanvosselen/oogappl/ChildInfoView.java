package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.Category;
import com.brentvanvosselen.oogappl.RestClient.Child;
import com.brentvanvosselen.oogappl.RestClient.Info;
import com.brentvanvosselen.oogappl.RestClient.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.activities.ChildInfoPopUp;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoView extends ScrollView {

    private Child[] children = new Child[0];
    private int selectedChild = 0;

    public ChildInfoView(Context context) {
        super(context);
    }

    public ChildInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView() {
        this.removeAllViews();

        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (this.children != null) {
            Spinner childPicker = new Spinner(getContext());
            ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, getChildNames());
            childPicker.setAdapter(childAdapter);

            linearLayout.addView(childPicker);

            final LinearLayout content = new LinearLayout(getContext());
            linearLayout.addView(content);

            childPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedChild = i;
                    updateContent(content);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            updateContent(content);
        }

        this.addView(linearLayout);
    }

    private void updateContent(LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final int index = selectedChild;

        Category[] cats = children[index].getCategory();

        if (cats == null) {
            Log.i("CATEGORY", "IS EMPTY");
        } else {
            for (Category c : cats) {
                TextView catName = new TextView(getContext());
                catName.setText(c.getName());
                linearLayout.addView(catName);

                for (Info info : c.getInfo()) {
                    ChildInfoItem item = new ChildInfoItem(getContext(), info.getName(), info.getValue(), true, c);
                    linearLayout.addView(item);
                }
            }

            Button buttonAdd = new Button(getContext());
            buttonAdd.setText("Add info");
            buttonAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ChildInfoPopUp.class);
                    intent.putExtra("childId", index);
                    intent.putStringArrayListExtra("catNames", children[index].getCategoryNames());
                    intent.putExtra("child", ObjectSerializer.serialize2(children[index]));
                    getContext().startActivity(intent);
                }
            });
            linearLayout.addView(buttonAdd);
        }

        Button buttonSave = new Button(getContext());
        buttonSave.setText("Save");
        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Child c : children) {
                    Call call = RetrofitClient.getClient().create(APIInterface.class).saveChild(c);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Save succesfull", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Save not succesfull", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                            call.cancel();
                        }
                    });
                }
            }
        });

        linearLayout.addView(buttonSave);
    }

    public void setVariables(Parent parent) {
        this.children = parent.getChildren();
        initView();
    }

    public String[] getChildNames() {
        String[] names = new String[children.length];

        for (int i = 0; i < this.children.length; i++) {
            String temp = this.children[i].getFirstname() + " " + this.children[i].getLastname();
            names[i] = temp;
        }

        return names;
    }
}