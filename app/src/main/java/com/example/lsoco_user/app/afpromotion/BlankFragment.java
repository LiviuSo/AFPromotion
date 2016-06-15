package com.example.lsoco_user.app.afpromotion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Fragment with some text launched when the app is run for the first time
 * and there is no connection
 */
public class BlankFragment extends Fragment {

    private static final String LOG_TAG = BlankFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        LinearLayout ll = (LinearLayout)view.findViewById(R.id.frag_blank_ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "clicked");
                // test if connection
                if (ConnectionUtil.isConnected(getActivity())) {
                    // swap with PromotionListFragment
                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_frag_holder, new PromotionListFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        return view;
    }
}
