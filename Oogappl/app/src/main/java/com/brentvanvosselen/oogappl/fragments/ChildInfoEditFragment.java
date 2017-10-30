package com.brentvanvosselen.oogappl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.Info;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.Category;

public class ChildInfoEditFragment extends Fragment {

    private Category category;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            Log.i("ChildEdit", "Got category");
            category = ObjectSerializer.deserialize2(bundle.getString("category"));
        }

        TextView catName = getView().findViewById(R.id.textView_childinfoedit_catName);
        catName.setText(category.getName());

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
            ConstraintLayout item = (ConstraintLayout) inflater.inflate(R.layout.childinfoedit_item, null);
            TextView name = item.findViewById(R.id.textView_childinfoedit_itemName);
            name.setText(i.getName());
            TextView value = item.findViewById(R.id.editText_childinfoedit_itemValue);
            value.setText(i.getValue());

            ImageButton remove = item.findViewById(R.id.imageButton_childinfoedit_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    category.remove(i);
                    initInfo();
                }
            });

            items.addView(item);
        }
    }
}
