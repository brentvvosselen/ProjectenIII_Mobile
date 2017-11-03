package com.brentvanvosselen.oogappl.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Info;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.models.Category;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildInfoEditFragment extends Fragment {

    private Child child;
    private Category category;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            Log.i("ChildEdit", "Got category");
            category = ObjectSerializer.deserialize2(bundle.getString("category"));
            child = ObjectSerializer.deserialize2(bundle.getString("child"));
        }

        TextView catName = getView().findViewById(R.id.textView_childinfoedit_catName);
        catName.setText(category.getName());

        ImageButton add = getView().findViewById(R.id.imageButton_childinfoedit_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        initInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_childinfo_edit, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initInfo() {
        LinearLayout items = getView().findViewById(R.id.linearLayout_childinfoedit_info);
        items.removeAllViews();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        for(final Info i : category.getInfo()) {
            CardView item = (CardView) inflater.inflate(R.layout.childinfoedit_item, null);
            TextView name = item.findViewById(R.id.textView_childinfoedit_itemName);
            name.setText(i.getName());
            TextView value = item.findViewById(R.id.editText_childinfoedit_itemValue);
            value.setText(i.getValue());

            ImageButton remove = item.findViewById(R.id.imageButton_childinfoedit_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    category.remove(i);
                    saveChanges();
                    initInfo();
                }
            });

            items.addView(item);
        }
    }

    private void saveChanges() {
        child.updateCategory(category);

        Call call = RetrofitClient.getClient().create(APIInterface.class).updateChild(child);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Log.i("SAVE", "Save succesful");
                } else {
                    Toast.makeText(getContext(), "Cannot find child", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItem() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View mView = getActivity().getLayoutInflater().inflate(R.layout.childinfoedit_item_add, null);

        final EditText name = mView.findViewById(R.id.editText_item_name);
        final EditText value = mView.findViewById(R.id.editText_item_waarde);

        builder.setView(mView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        category.addInfo(name.getText().toString(), value.getText().toString());
                        saveChanges();
                        initInfo();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }
}
