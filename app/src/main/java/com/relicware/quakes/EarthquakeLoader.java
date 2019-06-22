package com.relicware.quakes;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    private ArrayList<Earthquake> earthquakes;

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public void deliverResult(ArrayList<Earthquake> data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mUrl == null) return;

        // check cache time too
        if (earthquakes == null) forceLoad();
        else {
            Log.w(LOG_TAG, "delivering cached result");
            deliverResult(earthquakes);
        }
    }

    public ArrayList<Earthquake> earthquakesFromJson(String json) {
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject serverResponse = new JSONObject(json);
            JSONArray features = serverResponse.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                long startTime = System.nanoTime();

                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                String magnitude = properties.getString("mag");
                String place = properties.getString("place");
                String stamp = properties.getString("time");

                Earthquake earthquake = new Earthquake(place, stamp, magnitude);
                earthquakes.add(earthquake);

                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
                Log.d("Function time", "Function Time: " + duration);
            }
        } catch (Exception e) {
            Log.v("Earthquake List", "Failed to use JSON [ " + e + " ]");
        }
        return earthquakes;
    }

    public ArrayList<Earthquake> fetchEarthquakesFromNetwork(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return earthquakesFromJson(response.body().string());
        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<Earthquake> loadInBackground() {
        Log.w(LOG_TAG, "Fetching data from network");

        try {
            // Perform the network request, parse the response, and extract a list of earthquakes.
            earthquakes = fetchEarthquakesFromNetwork(mUrl);
        } catch (Exception e) { earthquakes = null; Log.d(LOG_TAG, "Network Error: Can't get list of earthquakes form server"); }
        return earthquakes;
    }
}