package in.eigene.miary.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;

public class PinDialogFragment extends DialogFragment {

    public interface Listener {

        void onPositiveButtonClicked(final String pin);
        void onCancelled();
    }

    private int message;

    private Listener listener;

    public PinDialogFragment setMessage(final int message) {
        this.message = message;
        return this;
    }

    public PinDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_dialog_pin, null);
        builder.setView(view);
        builder.setMessage(message);
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
        return builder.create();
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        listener.onCancelled();
    }
}
