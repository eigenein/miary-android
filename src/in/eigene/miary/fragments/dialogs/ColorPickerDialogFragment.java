package in.eigene.miary.fragments.dialogs;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.fragments.base.*;

public class ColorPickerDialogFragment extends BaseDialogFragment {

    public interface Listener {

        public void colorChosen(final int color);
    }

    private static final SparseIntArray VIEW_ID_TO_COLOR = new SparseIntArray();

    static {
        VIEW_ID_TO_COLOR.put(R.id.choose_color_white, LocalNote.COLOR_WHITE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_red, LocalNote.COLOR_RED);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_orange, LocalNote.COLOR_ORANGE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_yellow, LocalNote.COLOR_YELLOW);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_gray, LocalNote.COLOR_GRAY);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_green, LocalNote.COLOR_GREEN);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_blue, LocalNote.COLOR_BLUE);
        VIEW_ID_TO_COLOR.put(R.id.choose_color_violet, LocalNote.COLOR_PURPLE);
    }

    private Listener listener;
    private int color;

    public ColorPickerDialogFragment setListener(final Listener listener) {
        this.listener = listener;
        return this;
    }

    public ColorPickerDialogFragment setActiveColor(final int color) {
        this.color = color;
        return this;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_choose_color, null);
        builder.setView(view);
        builder.setTitle(R.string.dialog_colorpicker_title);
        setListeners(view);
        selectActiveColor(view);
        return builder.create();
    }

    private void selectActiveColor(final View view) {
        int colorIndex = VIEW_ID_TO_COLOR.indexOfValue(color);

        final ImageView activeColorView = (ImageView)view.findViewById(VIEW_ID_TO_COLOR.keyAt(colorIndex));
        activeColorView.setScaleType(ImageView.ScaleType.CENTER);
        activeColorView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
    }

    private void setListeners(final View view) {
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ColorPickerDialogFragment.this.listener.colorChosen(VIEW_ID_TO_COLOR.get(view.getId()));
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
