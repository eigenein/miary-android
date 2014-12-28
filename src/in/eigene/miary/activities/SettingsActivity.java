package in.eigene.miary.activities;

import android.os.*;
import in.eigene.miary.*;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        initializeToolbar();
    }
}
