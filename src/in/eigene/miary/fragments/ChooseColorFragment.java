package in.eigene.miary.fragments;

import android.app.*;
import android.os.*;
import android.support.v4.app.DialogFragment;
import android.view.*;
import in.eigene.miary.*;

public class ChooseColorFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // http://stackoverflow.com/a/13862509/359730
        final ContextThemeWrapper context = new ContextThemeWrapper(
                getActivity(), R.style.Miary_Theme_Dialog);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.choose_color_fragment, null);
        builder.setView(view);
        builder.setMessage(R.string.choose_color_message);
        return builder.create();
    }
}
