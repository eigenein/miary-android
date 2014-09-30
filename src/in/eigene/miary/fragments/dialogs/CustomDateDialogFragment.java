package in.eigene.miary.fragments.dialogs;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.format.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.base.*;

import java.util.*;

/**
 * Used to set custom note date and time.
 */
public class CustomDateDialogFragment extends BaseDialogFragment {

    public interface Listener {
        void onPositiveButtonClicked(final Date date);
    }

    private Listener listener;

    private Date creationDate;
    private Date customDate;

    private TimePicker timePicker;
    private DatePicker datePicker;

    public CustomDateDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    public CustomDateDialogFragment setCreationDate(final Date date) {
        this.creationDate = date;
        return this;
    }

    public CustomDateDialogFragment setCustomDate(final Date date) {
        this.customDate = date;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_dialog_custom_date, null);

        timePicker = (TimePicker)view.findViewById(R.id.dialog_custom_time);
        datePicker = (DatePicker)view.findViewById(R.id.dialog_custom_date);

        refresh(customDate);

        builder.setView(view);
        builder.setTitle(R.string.dialog_custom_date_title);
        builder.setCancelable(true);

        // Do not dismiss dialog on Reset.
        // http://stackoverflow.com/a/10661281/359730
        builder.setNeutralButton(R.string.note_reset_custom_date, null);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final Calendar calendar = Calendar.getInstance();
                calendar.set(
                        datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                listener.onPositiveButtonClicked(calendar.getTime());
            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog dialog = (AlertDialog)getDialog();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                refresh(creationDate);
            }
        });
    }

    /**
     * Refreshes view with the specified Date instance.
     */
    private void refresh(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        // Fix mode. http://stackoverflow.com/a/16261359/359730
        timePicker.setIs24HourView(DateFormat.is24HourFormat(getActivity()));

        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
