package com.example.lsoco_user.app.afpromotion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Shows the details of a promotion
 */
public class PromotionDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_fragment, container, false);
        Promotion selectedItem = getArguments().getParcelable("selected_item");
        ((TextView)view).setText(selectedItem.toStringDetails());

        return view;
    }
}
