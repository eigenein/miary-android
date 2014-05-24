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

    private final Activity context;

    private final List<Note> notes;

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

        public final TextView title;
        public final TextView text;
        public final TextView creationDate;

        public ViewHolder(final View view) {
            title = (TextView)view.findViewById(R.id.feed_item_title);
            text = (TextView)view.findViewById(R.id.feed_item_text);
            creationDate = (TextView)view.findViewById(R.id.feed_item_creation_date);
        }
    }
}
