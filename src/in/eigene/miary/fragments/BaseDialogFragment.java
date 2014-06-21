package in.eigene.miary.fragments;

import android.app.*;

public class BaseDialogFragment extends DialogFragment {

    /**
     * Shows the dialog with the default tag.
     */
    public void show(final FragmentManager manager) {
        show(manager, this.getClass().getSimpleName());
    }
}
