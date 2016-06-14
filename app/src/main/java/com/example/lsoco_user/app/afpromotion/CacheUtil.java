package com.example.lsoco_user.app.afpromotion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Util for caching json into a private local file
 */
public class CacheUtil {

    private static final String SHARED_PREF_CACHED_KEY = "cache_file_name";
    private static final String CACHE_FILENAME         = "jsonCacheFile";
    private static final String LOG_TAG                = CacheUtil.class.getSimpleName();

    /**
     * Tests if the json has been cached locally
     *
     * @return True if the file has been cached
     */
    public static boolean wasJsonCached(Activity activity) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.contains(SHARED_PREF_CACHED_KEY);
    }

    /**
     * Saves the name of the cache file in the SharePreferences when cached;
     * used to check later if there is cache
     */
    public static void markJsonWasCached(Activity activity) {
        SharedPreferences.Editor sharedPrefEditor = activity.getPreferences(Context.MODE_PRIVATE).edit();
        sharedPrefEditor.putString(SHARED_PREF_CACHED_KEY, CACHE_FILENAME);
        sharedPrefEditor.apply();
    }

    /**
     * Saves the json to some local file
     *
     * @param json     The json
     */
    public static void saveJsonToCache(Context context, String json) {
        File file = new File(context.getCacheDir(), CACHE_FILENAME);
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
     * @return The contents of the local file (the json)
     */
    public static String loadJsonFromCache(Context context) {
        //Get the text file
        File file = new File(context.getCacheDir(), CACHE_FILENAME);

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
}
