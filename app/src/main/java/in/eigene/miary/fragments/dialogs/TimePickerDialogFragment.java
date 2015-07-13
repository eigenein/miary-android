package in.eigene.miary.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import in.eigene.miary.R;
import in.eigene.miary.fragments.base.BaseDialogFragment;

public class TimePickerDialogFragment extends BaseDialogFragment {

    public interface Listener {

        void onPositiveButtonClicked(final int hour, final int minute);
    }

    private int title;
    private int hour;
    private int minute;

    private TimePicker timePicker;

    private Listener listener;

    public TimePickerDialogFragment setTitle(final int title) {
        this.title = title;
        return this;
    }

    public TimePickerDialogFragment setTime(final int hour, final int minute) {
        this.hour = hour;
        this.minute = minute;
        return this;
    }

    public TimePickerDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_dialog_timepicker, null);

        timePicker = (TimePicker)view.findViewById(R.id.dialog_reminder_time);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));

        refresh(hour, minute);

        builder.setView(view);
        builder.setTitle(title);
        builder.setCancelable(true);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                listener.onPositiveButtonClicked(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Refreshes view with the specified hour and minute.
     */
    private void refresh(final int hour, final int minute) {
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
    }
}
