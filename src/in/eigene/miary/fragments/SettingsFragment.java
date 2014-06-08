package in.eigene.miary.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
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
                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            ExportAsyncTask.start(getActivity());
                        } else {
                            Toast.makeText(getActivity(), R.string.toast_storage_unready, Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
        });

        findPreference(R.string.prefkey_substitution_table).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_replacement_table_title);
                        builder.setItems(Substitutions.REPRS, null);
                        builder.setCancelable(true);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    }
                }
        );
    }

    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }
}
