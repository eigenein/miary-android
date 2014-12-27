package in.eigene.miary.activities;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;

public class AboutActivity extends BaseActivity {

    public static void start(final Context context) {
        context.startActivity(new Intent().setClass(context, AboutActivity.class));
    }

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
