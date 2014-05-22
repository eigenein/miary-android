package in.eigene.miary.adapters;

import android.content.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;

import java.util.*;

/**
 * Used to display a list of notes.
 */
public class FeedItemAdapter extends ArrayAdapter<Note> {

    private final Context context;

    public FeedItemAdapter(final Context context, final List<Note> objects) {
        super(context, R.layout.feed_item, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.feed_item, parent, false);
        // TODO: update values.
        return view;
    }
}
