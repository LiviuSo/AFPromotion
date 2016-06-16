package com.example.lsoco_user.app.afpromotion.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lsoco_user.app.afpromotion.util.CacheUtil;
import com.example.lsoco_user.app.afpromotion.util.ConnectionUtil;
import com.example.lsoco_user.app.afpromotion.util.Constants;
import com.example.lsoco_user.app.afpromotion.model.Promotion;
import com.example.lsoco_user.app.afpromotion.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fragment holding a TableView that will display the promotions
 */
public class PromotionListFragment extends Fragment {

    private static final String LOG_TAG = PromotionListFragment.class.getSimpleName();
    private Promotion[]        promotions;
    private RecyclerView       mRecyclerView;
    private PromotionAdapterRV mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);

        // setup the SwipeRefresh listener
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateListView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // setup the RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_promo);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // fetch the data; check if there is cache
        boolean hasCache = CacheUtil.wasJsonCached(getActivity());
        if (hasCache) {
            // load from cache
            String stringJson = CacheUtil.loadJsonFromCache(getActivity());
            // parse
            promotions = parseJson(stringJson);
            // load data
            populateListView();
            Log.v(LOG_TAG, Constants.STRING_LOG_LOAD_CACHE);
        } else if (ConnectionUtil.isConnected(getActivity())) {
            // online, but no cache -> download data
            FeedDownloader downloader = new FeedDownloader();
            downloader.execute();
            Log.v(LOG_TAG, Constants.STRING_LOG_DOWNLOADED);
        } else {
            // never
            Log.e(LOG_TAG, Constants.STRING_LOG_NO_CACHE_CONN);
        }

        // specify an adapter (see also next example)
//        mRecyclerView.setAdapter(mAdapter);

        return view;
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

    /**
     * Populates the list view with the promotion
     */
    private void populateListView() {
        if (mAdapter == null) {
            mAdapter = new PromotionAdapterRV();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Downloads the feed
     */
    private class FeedDownloader extends AsyncTask<Void, Void, Promotion[]> {

        private final String LOG_TAG = FeedDownloader.class.getSimpleName();

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
                URL url = new URL(Constants.STRING_URL);
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
                    Log.v(LOG_TAG, Constants.STRING_LOG_BUFFER_EMPTY);
                    return null;
                }

                jsonPromotions = buffer.toString();
                // cache
                CacheUtil.saveJsonToCache(getActivity(), jsonPromotions);
                CacheUtil.markJsonWasCached(getActivity());
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
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
            return jsonPromotions;
        }
    }

    /**
     * RecyclerView Adapter to Promotion for Promotion objects
     */
    private class PromotionAdapterRV extends RecyclerView.Adapter<PromotionViewHolder> {

        @Override
        public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.promo_item, parent, false);
            return new PromotionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PromotionViewHolder holder, int position) {
            Promotion promotion = promotions[position];
            holder.bindData(promotion);
        }

        @Override
        public int getItemCount() {
            return promotions.length;
        }
    }

    /**
     * ViewHolder for Promotion objects
     */
    private class PromotionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView  textView;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView_title);
            itemView.setOnClickListener(this);
        }

        public void bindData(Promotion promotion) {
            textView.setText(promotion.getTitle());
            Picasso.with(getActivity())
                    .load(promotion.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);

        }

        @Override
        public void onClick(View v) {
            PromotionDetailFragment fragment = new PromotionDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.KEY_SELECTED_ITEM, promotions[getAdapterPosition()]);
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_frag_holder, fragment, null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
