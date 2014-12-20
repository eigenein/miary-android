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

    private Listener listener;

    public RemoveNoteDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
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