package in.eigene.miary.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import in.eigene.miary.R;
import in.eigene.miary.fragments.base.BaseDialogFragment;

public class PasscodeDialogFragment extends BaseDialogFragment {

    public interface Listener {

        void onPositiveButtonClicked(final String pin);
        void onCancelled();
    }

    private int title;

    private Listener listener;

    public PasscodeDialogFragment setTitle(final int title) {
        this.title = title;
        return this;
    }

    public PasscodeDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_passcode, null);
        final EditText passcodeEditText = (EditText)view.findViewById(R.id.dialog_passcode_edit);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        getDialog().cancel();
                    }
                })
                .setCancelable(true)
                .create();

        // Need to validate the field.
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final String passcode = passcodeEditText.getText().toString();
                        if (passcode.length() != 0) {
                            listener.onPositiveButtonClicked(passcode);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        listener.onCancelled();
    }
}
