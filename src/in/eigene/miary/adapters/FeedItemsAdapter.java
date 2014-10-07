package in.eigene.miary.adapters;

import android.app.*;
import android.text.format.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.helpers.*;

import java.util.*;

/**
 * Used to display a list of notes.
 */
public class FeedItemsAdapter extends ArrayAdapter<Note> {

    private final Activity context;

    private final List<Note> notes;

    public FeedItemsAdapter(final Activity context, final List<Note> notes) {
        super(context, R.layout.feed_item, notes);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            final LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.feed_item, parent, false);
            viewHolder = new ViewHolder(view);
            viewHolder.title.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_BOLD));
            viewHolder.text.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_REGULAR));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        final Note note = notes.get(position);
        final StyleHolder styleHolder = StyleHolders.get(note.getColor());
        // Set layout style.
        viewHolder.layout.setBackgroundResource(styleHolder.feedItemDrawableId);
        // Set title text and visibility.
        viewHolder.title.setText(note.getTitle());
        viewHolder.title.setVisibility(!note.getTitle().isEmpty() ? View.VISIBLE : View.GONE);
        // Set text.
        viewHolder.text.setText(note.getText());
        // Set creation date text and style.
        viewHolder.creationDate.setTextColor(context.getResources().getColor(styleHolder.feedItemFooterColorId));
        viewHolder.creationDate.setText(DateUtils.getRelativeDateTimeString(
                context,
                note.getCustomDate().getTime(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                0));

        return view;
    }

    public List<Note> getNotes() {
        return notes;
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
