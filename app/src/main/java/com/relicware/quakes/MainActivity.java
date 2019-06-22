package com.relicware.quakes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Earthquake>> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final EarthquakeAdapter mAdapter = new EarthquakeAdapter();
    private Toast toast;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private ConnectivityManager connectivityManager;
    private final String USGS_BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private final String EARTHQUAKE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.w(LOG_TAG, "Loader Created");
        findViewById(R.id.progress_indicator).setVisibility(View.VISIBLE);
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        Log.w(LOG_TAG, "Loading finished");
        findViewById(R.id.progress_indicator).setVisibility(View.GONE);
        if (earthquakes != null && !earthquakes.isEmpty()) {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            mAdapter.replaceDataset(earthquakes);
        } else {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            if (isConnected)
                ((TextView) findViewById(R.id.empty_view)).setText("No earthquakes to show");
            else
                ((TextView) findViewById(R.id.empty_view)).setText("Could not retrieve list from network");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        Log.w(LOG_TAG, "Loader Reset");
        findViewById(R.id.progress_indicator).setVisibility(View.GONE);
        // Loader reset, so we can clear out our existing data.
        mAdapter.clearItems();
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        if (isConnected)
            ((TextView) findViewById(R.id.empty_view)).setText("No earthquakes to show");
        else
            ((TextView) findViewById(R.id.empty_view)).setText("Could not retrieve list from network");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        mAdapter.setLayoutManager(new LinearLayoutManager(this));

        final RecyclerView list = findViewById(R.id.items_list);

        list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        list.setHasFixedSize(true);
        list.setLayoutManager(mAdapter.getLayoutManager());
        list.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((i, v) -> {
            if (toast != null) toast.cancel();
            toast = Toast.makeText(this, "you clicked number: " + mAdapter.getItem(i).toString(), Toast.LENGTH_SHORT);
            toast.show();
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }
}
