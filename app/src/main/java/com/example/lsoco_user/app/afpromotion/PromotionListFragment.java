package com.example.lsoco_user.app.afpromotion;


import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fragment holding a TableView that will display the promotions
 */
public class PromotionListFragment extends Fragment {

    private static final String LOG_TAG        = PromotionListFragment.class.getSimpleName();
    private static final String CACHE_FILENAME = "jsonCacheFile";
    private PromotionAdapter mAdapter;
    private Promotion[]      promotions;
    private ListView         mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);
        mListView = (ListView) view.findViewById(R.id.listview_promotions);
        createOrUpdateAdapter();

        // check if there is cache
        boolean hasCache = jsonWasCached();
        if (hasCache) {
            // load from cache
            String stringJson = loadJsonFromCache(CACHE_FILENAME);
            // parse
            promotions = parseJson(stringJson);
            // load data
            populateListView();
            Log.v(LOG_TAG, "loaded from cache");
        } else {
            // todo test if online
            // download data
        FeedDownloader downloader = new FeedDownloader();
        downloader.execute();
        }
        return view;
    }

    /**
     * Tests if the json has been cached locally
     *
     * @return True if the file has been cached
     */
    private boolean jsonWasCached() {
        return true;
    }

    /**
     * Saves the json to some local file
     *
     * @param filename The name of the file
     * @param json     The json
     */
    private void saveJsonToCache(String filename, String json) {
        File file = new File(getActivity().getCacheDir(), filename);
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
            Log.v(LOG_TAG, "json cached successfully!");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    /**
     * Load json from a local file
     *
     * @param filename The name of the local file
     * @return The contents of the local file (the json)
     */
    private String loadJsonFromCache(String filename) {
        //Get the text file
        File file = new File(getActivity().getCacheDir(), filename);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return text.toString();
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
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return promotionsArray;
    }


    private void createOrUpdateAdapter() {
        if (mAdapter == null) {
            mAdapter = new PromotionAdapter(getActivity());
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(promotions);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, "clicked " + position);
                PromotionDetailFragment fragment = new PromotionDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("selected_item", promotions[position]);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frag_holder, fragment, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Populates the list view with the promotion
     */
    private void populateListView() {
        createOrUpdateAdapter();
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
            promotions = promotionsArray;
            populateListView();
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
                // cache
                saveJsonToCache(CACHE_FILENAME, jsonPromotions);
                // test
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
    }

    /**
     * ArrayAdapter for Promotion objects
     */
    private class PromotionAdapter extends ArrayAdapter<Promotion> {

        private Context context;

        public PromotionAdapter(Context context) {
            super(context, R.layout.promo_item);
            this.context = context;
        }

        @Override
        public int getCount() {
            if(promotions != null) {
                return promotions.length;
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.promo_item, parent, false);
            if (promotions != null && getCount() > 0) {
                TextView textView = (TextView) view.findViewById(R.id.textView_title);
                textView.setText(promotions[position].getTitle());
                // set the image
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                Picasso.with(context)
                        .load(promotions[position].getImage())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imageView);
            }
            return view;
        }
    }
}
