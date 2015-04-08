package in.eigene.miary.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import in.eigene.miary.R;
import in.eigene.miary.fragments.base.BaseDialogFragment;

public class PinDialogFragment extends BaseDialogFragment {

    public interface Listener {

        void onPositiveButtonClicked(final String pin);
        void onCancelled();
    }

    private int title;

    private Listener listener;

    public PinDialogFragment setTitle(final int title) {
        this.title = title;
        return this;
    }

    public PinDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_dialog_pin, null);
        builder.setView(view);
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                listener.onPositiveButtonClicked(
                        ((EditText)view.findViewById(R.id.dialog_pin_edit)).getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                getDialog().cancel();
            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        listener.onCancelled();
    }
}
