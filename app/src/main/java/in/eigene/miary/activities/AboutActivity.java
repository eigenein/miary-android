package in.eigene.miary.activities;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

import com.parse.*;

import in.eigene.miary.*;
import in.eigene.miary.core.persistence.*;

/**
 * About application.
 */
public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        initializeToolbar();

        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.about_version)).setText(getResources().getString(
                    R.string.about_version, packageInfo.versionName, packageInfo.versionCode));
        } catch (final PackageManager.NameNotFoundException e) {
            // Do nothing.
        }

        findViewById(R.id.about_vkontakte_text).setOnClickListener(
                new StartUriOnClickListener(Uri.parse("http://vk.com/miaryapp")));
        findViewById(R.id.about_facebook_text).setOnClickListener(
                new StartUriOnClickListener(Uri.parse("http://www.facebook.com/miaryapp")));
        findViewById(R.id.about_google_plus_text).setOnClickListener(
                new StartUriOnClickListener(Uri.parse("http://plus.google.com/communities/105005072306337762911")));

        final TextView versionView = (TextView)findViewById(R.id.about_version);
        registerForContextMenu(versionView);
        versionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Copy version, build and installation ID to the clipboard.
                final ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(
                        getString(R.string.app_name),
                        versionView.getText() + "\n" + ParseInstallation.getCurrentInstallation().getInstallationId()
                ));
                Toast.makeText(AboutActivity.this, R.string.toast_copied, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view,final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.developer, menu);
    }

    /**
     * Developer features.
     */
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_developer_clear_shared_preferences:
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
                return true;

            case R.id.menu_item_developer_delete_all_notes:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("The operation is irreversible.")
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                getContentResolver().delete(Note.Contract.CONTENT_URI, null, null);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return true;

            case R.id.menu_item_developer_add_sample_notes:
                final ContentResolver contentResolver = getContentResolver();
                final String[] texts = getResources().getStringArray(R.array.sample_texts);
                int color = 5;
                for (final String text : texts) {
                    Note.getEmpty().setText(text).setColor(color++ % 8).insert(contentResolver);
                }
                return true;

            case R.id.menu_item_developer_log_out:
                ParseUser.logOut();
                return true;

            case R.id.menu_item_developer_disable_flag_secure:
                BaseActivity.disableSecureFlag = true;
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Starts URI view activity on click.
     */
    private class StartUriOnClickListener implements View.OnClickListener {

        private final Uri uri;

        public StartUriOnClickListener(final Uri uri) {
            this.uri = uri;
        }

        @Override
        public void onClick(final View view) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browserIntent);
        }
    }
}
