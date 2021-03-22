package com.mahmoudkhalil.quakereport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    RecyclerView earthquakeRecyclerView;
    QuakeListAdapter quakeListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        earthquakeRecyclerView = findViewById(R.id.list);

        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthQuake>> {

        @Override
        protected List<EarthQuake> doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String minMagnitude = sharedPreferences.getString(
                    getString(R.string.settings_min_mag_key),
                    getString(R.string.settings_min_mag_default));

            String orderBy = sharedPreferences.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default));

            Uri baseUri = Uri.parse(USGS_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendQueryParameter("format", "geojson");
            uriBuilder.appendQueryParameter("limit", "100");
            uriBuilder.appendQueryParameter("minmag", minMagnitude);
            uriBuilder.appendQueryParameter("orderby", orderBy);

            List<EarthQuake> earthQuakes = QuakeUtils.fetchEarthquakeData(uriBuilder.toString());
            return earthQuakes;
        }

        @Override
        protected void onPostExecute(final List<EarthQuake> earthQuakes) {

            TextView textView = findViewById(R.id.empty_view);
            if (!isNetworkConnected()) {
                textView.setText(R.string.no_internet);
            }
            else if(earthQuakes.isEmpty() || earthQuakes == null) {
                textView.setText(R.string.no_earthquakes);
            }
            // Create a new {@link ArrayAdapter} of earthquakes
            quakeListAdapter = new QuakeListAdapter(earthQuakes, MainActivity.this);

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeRecyclerView.setAdapter(quakeListAdapter);


            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            quakeListAdapter.setOnItemClickListener(new QuakeListAdapter.onItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String url = earthQuakes.get(position).getUrl();
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(urlIntent);
                }
            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}