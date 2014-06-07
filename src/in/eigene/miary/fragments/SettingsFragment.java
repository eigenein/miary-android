package in.eigene.miary.fragments;

import android.os.*;
import android.preference.*;
import in.eigene.miary.*;
import in.eigene.miary.core.export.*;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(R.string.prefkey_export).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        // TODO: Check if external storage is available.
                        Exporter.start(getActivity());
                        return true;
                    }
        });
    }

    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }
}
