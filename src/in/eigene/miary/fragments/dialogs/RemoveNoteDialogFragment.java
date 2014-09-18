package in.eigene.miary.fragments.dialogs;

import android.app.*;
import android.content.*;
import android.os.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.base.*;

public class RemoveNoteDialogFragment extends BaseDialogFragment {

    public interface Listener {

        void onPositiveButtonClicked();
    }

    private final Listener listener;

    public RemoveNoteDialogFragment(final Listener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_remove_note_title)
                .setMessage(R.string.dialog_remove_note_message)
                .setPositiveButton(R.string.button_remove, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        listener.onPositiveButtonClicked();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        getDialog().cancel();
                    }
                })
                .create();
    }
}
