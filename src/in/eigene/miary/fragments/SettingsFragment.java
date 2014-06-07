package in.eigene.miary.fragments;

import android.os.*;
import android.preference.*;
import in.eigene.miary.*;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(R.string.prefkey_export).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        return true;
                    }
        });
    }

    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }
}
