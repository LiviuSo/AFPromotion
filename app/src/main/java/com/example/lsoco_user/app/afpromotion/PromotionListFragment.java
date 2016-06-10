package com.example.lsoco_user.app.afpromotion;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        mListView.setAdapter(mAdapter);

        // download data
        FeedDownloader downloader = new FeedDownloader();
        downloader.execute();

        return view;
    }

    /**
     * Downloads the feed
     */
    private class FeedDownloader extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG    = FeedDownloader.class.getSimpleName();
        private final String STRING_URL = "https://www.abercrombie.com/anf/nativeapp/Feeds/promotions.json";

        @Override
        protected Void doInBackground(Void... params) {
            downloadFeed();
            return null;
        }

        private void downloadFeed() {
            // b/e
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // will hold the feed as json
            String jsonPromotions;

            try {
                // create an url object
                URL url = new URL(STRING_URL);
                // open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return;
                }

                // create the buffered reader
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // read json line by line
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append('\n');
                }

                if (buffer.length() == 0) {
                    Log.v(LOG_TAG, "buffer empty");
                    return;
                }

                jsonPromotions = buffer.toString();
                Log.v(LOG_TAG, jsonPromotions);

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }
}
