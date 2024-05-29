package com.openclassrooms.go4lunch.view.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.openclassrooms.go4lunch.R;

/**
 * Fragment to display Settings fragment extending Preferences Fragment component
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
