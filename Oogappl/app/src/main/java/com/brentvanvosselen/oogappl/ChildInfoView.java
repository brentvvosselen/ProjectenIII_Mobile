package com.brentvanvosselen.oogappl;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.ChildinfoCategory;
import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Info;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;

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
            ArrayAdapter<String> childAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getChildNames());
            childPicker.setAdapter(childAdapter);

            linearLayout.addView(childPicker);

            final LinearLayout content = new LinearLayout(getContext());
            linearLayout.addView(content);

            childPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedChild = i;
                    updateContent(content, false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            updateContent(content, false);
        }

        this.addView(linearLayout);
    }

    private void updateContent(LinearLayout linearLayout, boolean editable) {
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final int index = selectedChild;

        List<ChildinfoCategory> cats = children[index].getCategory();

        if (cats == null) {
            Log.i("CATEGORY", "IS EMPTY");
        } else {
            for (ChildinfoCategory c : cats) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.leftMargin = 16;
                params.rightMargin = 16;
                params.bottomMargin = 24;

                CardView card = new CardView(getContext());
                card.setElevation(4);

                updateCard(c, card, false);

                linearLayout.addView(card, params);
            }
        }

        /*
        Button buttonAdd = new Button(getContext());
        buttonAdd.setText("Add info");
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(getContext(), ChildInfoPopUp.class);
                intent.putExtra("childId", index);
                intent.putStringArrayListExtra("catNames", children[index].getCategoryNames());
                intent.putExtra("child", ObjectSerializer.serialize2(children[index]));
                getContext().startActivity(intent);
            }
        });
        linearLayout.addView(buttonAdd);
        */
    }

    private void updateCard(final ChildinfoCategory c, final CardView card, final boolean editable) {
        card.removeAllViews();
        LinearLayout cardLinear = new LinearLayout(getContext());
        cardLinear.setOrientation(LinearLayout.VERTICAL);
        cardLinear.setPadding(8, 8, 8, 8);

        TextView catName = new TextView(getContext());
        catName.setText(c.getName());
        catName.setTextSize(18);
        catName.setGravity(Gravity.CENTER_HORIZONTAL);
        catName.setTextColor(getResources().getColor(R.color.blue_mid));
        catName.setPadding(0, 32, 0, 0);
        cardLinear.addView(catName);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params2.leftMargin = 16;
        params2.rightMargin = 16;

        for (Info info : c.getInfo()) {
            ChildInfoItem item = new ChildInfoItem(getContext(), info.getName(), info.getValue(), editable, c);
            item.setPadding(16, 16, 16, 16);
            cardLinear.addView(item, params2);
        }

        Button buttonEdit = new Button(getContext());
        if(editable) {
            buttonEdit.setText("Save");
        } else {
            buttonEdit.setText("Edit");
        }

        buttonEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCard(c, card, !editable);

                if(editable) {
                    saveChanges();
                }
            }
        });

        cardLinear.addView(buttonEdit);

        card.addView(cardLinear);
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

    private void saveChanges() {
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
}