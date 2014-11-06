package in.eigene.miary.activities;

import android.content.*;
import android.os.*;
import in.eigene.miary.*;

public class SettingsActivity extends BaseActivity {

    public static void start(final Context context) {
        context.startActivity(new Intent().setClass(context, SettingsActivity.class));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        initializeToolbar();
    }
}
