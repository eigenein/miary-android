package in.eigene.miary.activities;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import com.parse.ParseException;
import in.eigene.miary.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;

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

        registerForContextMenu(findViewById(R.id.about_version));
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view,final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.developer, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_developer_clear_shared_preferences:
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
                return true;
            case R.id.menu_item_developer_delete_all_notes:
                Note.unpinAllInBackground(new DeleteCallback() {
                    @Override
                    public void done(final ParseException e) {
                        InternalRuntimeException.throwForException("could not unpin all notes", e);
                        Toast.makeText(AboutActivity.this, "Done", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

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
