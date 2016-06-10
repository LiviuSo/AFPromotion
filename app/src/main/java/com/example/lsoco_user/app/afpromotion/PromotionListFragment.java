package com.example.lsoco_user.app.afpromotion;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Fragment holding a TableView that will display the promotions
 */
public class PromotionListFragment extends Fragment {

    private ListView             mListView;
    private ArrayAdapter<String> mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);
        mListView = (ListView) view.findViewById(R.id.listview_promotions);

        // mock data
        ArrayList<String> data = new ArrayList<>();
        data.add("promotion 1");
        data.add("promotion 2");
        data.add("promotion 3");

        // create and set the adapter
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
        mListView.setAdapter(mAdapter);

        return view;
    }

    /**
     * Downloads the feed
     */
    private class FeedDownloader {
        private String jsonPromotions;

        public void downloadFeed() {
            jsonPromotions = "";
        }
    }
}
