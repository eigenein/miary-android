package in.eigene.miary.helpers;

import android.text.Editable;

/**
 * Provides default implementation of text watcher.
 */
public class TextWatcher implements android.text.TextWatcher {

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        // Do nothing.
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        // Do nothing.
    }

    @Override
    public void afterTextChanged(final Editable s) {
        // Do nothing.
    }
}
