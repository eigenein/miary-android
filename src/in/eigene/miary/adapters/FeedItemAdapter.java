package in.eigene.miary.adapters;

import android.app.*;
import android.text.format.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;

import java.util.*;

/**
 * Used to display a list of notes.
 */
public class FeedItemAdapter extends ArrayAdapter<Note> {

    private static final HashMap<Integer, Integer> COLOR_TO_RESOURCE_ID =
            new HashMap<Integer, Integer>();

    private final Activity context;

    private final List<Note> notes;

    static {
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_WHITE, R.drawable.feed_item_white);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_BLUE, R.drawable.feed_item_blue);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_VIOLET, R.drawable.feed_item_violet);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_GREEN, R.drawable.feed_item_green);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_ORANGE, R.drawable.feed_item_orange);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_RED, R.drawable.feed_item_red);
    }

    public FeedItemAdapter(final Activity context, final List<Note> notes) {
        super(context, R.layout.feed_item, notes);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            final LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.feed_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        final Note note = notes.get(position);
        viewHolder.layout.setBackgroundResource(COLOR_TO_RESOURCE_ID.get(note.getColor()));
        viewHolder.title.setText(note.getTitle());
        viewHolder.text.setText(note.getText());
        viewHolder.creationDate.setText(DateUtils.getRelativeDateTimeString(
                context,
                note.getCreationDate().getTime(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                0));

        return view;
    }

    static class ViewHolder {

        public final LinearLayout layout;
        public final TextView title;
        public final TextView text;
        public final TextView creationDate;

        public ViewHolder(final View view) {
            layout = (LinearLayout)view.findViewById(R.id.feed_item_layout);
            title = (TextView)view.findViewById(R.id.feed_item_title);
            text = (TextView)view.findViewById(R.id.feed_item_text);
            creationDate = (TextView)view.findViewById(R.id.feed_item_creation_date);
        }
    }
}
