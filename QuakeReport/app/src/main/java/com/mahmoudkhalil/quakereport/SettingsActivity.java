package com.mahmoudkhalil.quakereport;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference((getString(R.string.settings_order_by_key)));
            bindPreferenceSummaryToValue(orderBy);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_mag_key));
            bindPreferenceSummaryToValue(minMagnitude);
        }

        private void bindPreferenceSummaryToValue(Preference minMagnitude) {
            minMagnitude.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(minMagnitude.getContext());
            String preferenceString = sharedPreferences.getString(minMagnitude.getKey(), "");
            onPreferenceChange(minMagnitude, preferenceString);
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if(prefIndex >= 0) {
                    CharSequence [] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}