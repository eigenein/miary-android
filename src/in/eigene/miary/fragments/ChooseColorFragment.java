package in.eigene.miary.fragments;

import android.app.*;
import android.os.*;
import android.support.v4.app.DialogFragment;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;

import java.util.*;

public class ChooseColorFragment extends DialogFragment {

    interface DialogListener {

        public void colorChosen(final int color);
    }

    private final DialogListener dialogListener;

    private final HashMap<Integer, Integer> idToColor = new HashMap<Integer, Integer>();

    public ChooseColorFragment(final DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // http://stackoverflow.com/a/13862509/359730
        final ContextThemeWrapper context = new ContextThemeWrapper(
                getActivity(), R.style.Miary_Theme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.choose_color_fragment, null);
        builder.setView(view);
        builder.setMessage(R.string.choose_color_message);
        setListeners(view);
        return builder.create();
    }

    private void setListeners(final View view) {
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialogListener.colorChosen(idToColor.get(view.getId()));
                dismiss();
            }
        };

        view.findViewById(R.id.choose_color_white).setOnClickListener(listener);
        idToColor.put(R.id.choose_color_white, NoteColor.WHITE);
        view.findViewById(R.id.choose_color_green).setOnClickListener(listener);
        idToColor.put(R.id.choose_color_green, NoteColor.GREEN);
    }
}
