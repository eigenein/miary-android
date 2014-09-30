package in.eigene.miary.fragments.base;

import android.app.*;
import android.os.*;

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateDialog(savedInstanceState);
    }

    // https://code.google.com/p/android/issues/detail?id=17423#c23
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Shows the dialog with the default tag.
     */
    public void show(final FragmentManager manager) {
        show(manager, this.getClass().getSimpleName());
    }
}
