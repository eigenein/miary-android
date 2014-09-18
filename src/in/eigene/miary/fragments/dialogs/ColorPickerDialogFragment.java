package in.eigene.miary.fragments.dialogs;

import android.app.*;
import android.os.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.fragments.base.*;

import java.util.*;

public class ColorPickerDialogFragment extends BaseDialogFragment {

    public interface DialogListener {

        public void colorChosen(final int color);
    }

    private static final HashMap<Integer, Integer> VIEW_ID_TO_COLOR = new HashMap<Integer, Integer>();

    static {
        VIEW_ID_TO_COLOR.put(R.id.choose_color_white, Note.COLOR_WHITE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_red, Note.COLOR_RED);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_orange, Note.COLOR_ORANGE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_yellow, Note.COLOR_YELLOW);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_gray, Note.COLOR_GRAY);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_green, Note.COLOR_GREEN);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_blue, Note.COLOR_BLUE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_violet, Note.COLOR_VIOLET);
    }

    private final DialogListener dialogListener;

    public ColorPickerDialogFragment(final DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_choose_color, null);
        builder.setView(view);
        builder.setTitle(R.string.dialog_colorpicker_title);
        setListeners(view);
        return builder.create();
    }

    private void setListeners(final View view) {
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialogListener.colorChosen(VIEW_ID_TO_COLOR.get(view.getId()));
                dismiss();
            }
        };

        view.findViewById(R.id.choose_color_white).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_red).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_orange).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_yellow).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_gray).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_green).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_blue).setOnClickListener(listener);
        view.findViewById(R.id.choose_color_violet).setOnClickListener(listener);
    }
}
