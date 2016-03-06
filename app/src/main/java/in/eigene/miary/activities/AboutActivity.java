package in.eigene.miary.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.eigene.miary.R;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;

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

        final TextView versionView = (TextView)findViewById(R.id.about_version);
        registerForContextMenu(versionView);
        versionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Copy version and build number to the clipboard.
                final ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), versionView.getText()));
                Toast.makeText(AboutActivity.this, R.string.toast_copied, Toast.LENGTH_SHORT).show();
            }
        });

        final ListView aboutLinksListView = (ListView)findViewById(R.id.about_links_list);
        aboutLinksListView.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.single_list_item,
                R.id.single_list_item_text_view,
                getResources().getStringArray(R.array.about_titles)));
        final String[] uris = getResources().getStringArray(R.array.about_links);
        aboutLinksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uris[position])));
                Tracking.clickAboutLink(uris[position]);
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
                PreferenceHelper.clear(this);
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

            case R.id.menu_item_developer_add_welcome_notes:
                final Resources resources = getResources();
                final ContentResolver contentResolver = getContentResolver();
                final String[] titles = resources.getStringArray(R.array.welcome_titles);
                final String[] texts = resources.getStringArray(R.array.welcome_texts);
                final int [] colors = resources.getIntArray(R.array.welcome_colors);
                for (int i = 0; i < texts.length; i++) {
                    Note.createEmpty()
                            .setTitle(titles[i])
                            .setText(texts[i])
                            .setColor(colors[i])
                            .insert(contentResolver);
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
