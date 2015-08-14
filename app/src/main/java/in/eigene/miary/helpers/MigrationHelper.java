package in.eigene.miary.helpers;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.ParseException;

import in.eigene.miary.backup.inputs.LocalDatastoreRestoreInput;
import in.eigene.miary.backup.tasks.MigrateAsyncTask;
import in.eigene.miary.exceptions.InternalRuntimeException;

/**
 * Helps to migrate notes from Parse Local Datastore to SQLite.
 */
public class MigrationHelper {

    private static final String LOG_TAG = MigrationHelper.class.getSimpleName();

    public static void migrate(final Context context) {

        LocalDatastoreRestoreInput.getCount(new CountCallback() {
            @Override
            public void done(final int count, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to get count.", e);
                Log.i(LOG_TAG, count + " notes found.");
                if (count != 0) {
                    new MigrateAsyncTask(context).execute();
                } else {
                    Log.i(LOG_TAG, "Nothing to be migrated.");
                    PreferenceHelper.edit(context).putBoolean(PreferenceHelper.KEY_NOTES_MIGRATED, true).apply();
                }
            }
        });
    }
}
