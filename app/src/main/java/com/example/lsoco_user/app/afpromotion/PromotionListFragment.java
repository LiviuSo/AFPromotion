package com.example.lsoco_user.app.afpromotion;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment holding a TableView that will display the promotions
 */
public class PromotionListFragment extends Fragment {

    private static final String LOG_TAG = PromotionListFragment.class.getSimpleName();
    private ArrayAdapter<String> mAdapter;
    private ArrayList<Promotion> promotions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);
        ListView mListView = (ListView) view.findViewById(R.id.listview_promotions);

        // create and set the adapter
        ArrayList<String> data = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, "clicked " + position);
                PromotionDetailFragment fragment = new PromotionDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_item", promotions.get(position));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frag_holder, fragment, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // download data
        FeedDownloader downloader = new FeedDownloader();
        downloader.execute();

        return view;
    }

    /**
     * Downloads the feed
     */
    private class FeedDownloader extends AsyncTask<Void, Void, Promotion[]> {

        private final String LOG_TAG    = FeedDownloader.class.getSimpleName();
        private final String STRING_URL = "https://www.abercrombie.com/anf/nativeapp/Feeds/promotions.json";

        @Override
        protected Promotion[] doInBackground(Void... params) {
            String jsonString = downloadFeed();
            if (jsonString != null) {
                return parseJson(jsonString);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Promotion[] promotionsArray) {
            ArrayList<String> promotionsStringList = new ArrayList<>();
            for (Promotion promotion : promotionsArray) {
                promotionsStringList.add(promotion.toString());
                promotions.add(promotion);
            }
            mAdapter.clear();
            mAdapter.addAll(promotionsStringList);
        }

        /**
         * Helper downloads the feed json
         *
         * @return json as string
         */
        private String downloadFeed() {
            // b/e
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // will hold the feed as json
            String jsonPromotions = null;

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
                    return null;
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
                    return null;
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
            return jsonPromotions;
        }

        /**
         * Parses the json and return an array of Promotion objects
         *
         * @param stringJson The json
         * @return The array of objects (Promotion)
         */
        private Promotion[] parseJson(String stringJson) {
            Promotion[] promotionsArray = null;
            try {
                JSONObject feed = new JSONObject(stringJson);
                JSONArray feedPromotions = feed.getJSONArray("promotions");
                int numPromos = feedPromotions.length();
                promotionsArray = new Promotion[numPromos];  // create the array that will hold the promotionsArray
                for (int i = 0; i < numPromos; i++) {
                    promotionsArray[i] = new Promotion();
                    JSONObject promotion = feedPromotions.getJSONObject(i);
                    promotionsArray[i].setTitle(promotion.getString("title"));
                    promotionsArray[i].setImage(promotion.getString("image"));
                    promotionsArray[i].setDescription(promotion.getString("description"));
                    if (promotion.has("footer")) {
                        promotionsArray[i].setFooter(promotion.getString("footer"));
                    }
                    JSONObject feedPromotionButton;
                    // parse the 'button' content differently since in first promotion "button" is an object
                    // whereas in the second an array
                    try { // we first try to get "button" as a JSONObject
                        feedPromotionButton = promotion.getJSONObject("button");
                    } catch (JSONException je) {
                        feedPromotionButton = promotion.getJSONArray("button").getJSONObject(0);
                    }

                    Promotion.PromotionButton button = new Promotion.PromotionButton();
                    button.setTitle(feedPromotionButton.getString("title"));
                    button.setTarget(feedPromotionButton.getString("target"));
                    promotionsArray[i].setButton(button);
                }

                // test
                for (Promotion promotion : promotionsArray) {
                    Log.v(LOG_TAG, promotion.toString());
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return promotionsArray;
        }
    }
}
