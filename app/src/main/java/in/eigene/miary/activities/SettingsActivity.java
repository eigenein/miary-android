package in.eigene.miary.activities;

import android.os.Bundle;

import in.eigene.miary.R;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        initializeToolbar();
    }
}
