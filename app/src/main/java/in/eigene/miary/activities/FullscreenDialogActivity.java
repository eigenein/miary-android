package in.eigene.miary.activities;

import in.eigene.miary.R;

/**
 * Base class for full-screen dialogs.
 * http://www.google.com/design/spec/components/dialogs.html#dialogs-full-screen-dialogs
 */
public abstract class FullscreenDialogActivity extends BaseActivity {

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }
}
