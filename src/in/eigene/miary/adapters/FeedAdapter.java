package in.eigene.miary.adapters;

import android.content.*;
import android.support.v7.widget.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.util.*;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String LOG_TAG = FeedAdapter.class.getName();

    private Mode mode = Mode.DIARY;
    private SortingOrder sortingOrder = SortingOrder.DESCENDING;

    private List<Note> notes = new ArrayList<Note>();

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(LOG_TAG, "Bind note " + position);
        viewHolder.bindNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Mode getMode() {
        return mode;
    }

    public FeedAdapter setMode(final Mode mode) {
        this.mode = mode;
        return this;
    }

    public SortingOrder getSortingOrder() {
        return sortingOrder;
    }

    public FeedAdapter setSortingOrder(final SortingOrder sortingOrder) {
        this.sortingOrder = sortingOrder;
        return this;
    }

    public FeedAdapter swapSortingOrder() {
        sortingOrder = sortingOrder.opposite();
        return this;
    }

    public FeedAdapter refresh(final OnDataChangedListener listener) {
        Log.d(LOG_TAG, "refresh");

        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        switch (mode) {
            case DIARY:
                query.whereEqualTo(Note.KEY_DRAFT, false);
                break;
            case STARRED:
                query.whereEqualTo(Note.KEY_STARRED, true);
                break;
            case DRAFTS:
                query.whereEqualTo(Note.KEY_DRAFT, true);
                break;
        }
        switch (sortingOrder) {
            case DESCENDING:
                query.orderByDescending(Note.KEY_CUSTOM_DATE);
                break;
            case ASCENDING:
                query.orderByAscending(Note.KEY_CUSTOM_DATE);
                break;
        }
        query.findInBackground(new FindCallback<Note>() {

            @Override
            public void done(final List<Note> notes, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find notes.", e);
                Log.i(LOG_TAG, "Found notes: " + notes.size());
                FeedAdapter.this.notes = notes;
                listener.onDataChanged();
                notifyDataSetChanged();
            }
        });

        return this;
    }

    public static enum Mode {
        DIARY,
        STARRED,
        DRAFTS
    }

    public static enum SortingOrder {
        ASCENDING,
        DESCENDING;

        private SortingOrder opposite;

        static {
            ASCENDING.opposite = DESCENDING;
            DESCENDING.opposite = ASCENDING;
        }

        public SortingOrder opposite() {
            return opposite;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final CardView layout;
        public final TextView title;
        public final TextView text;
        public final TextView creationDate;

        private Note note;

        public ViewHolder(final View view) {
            super(view);

            final Context context = view.getContext();

            layout = (CardView)view.findViewById(R.id.feed_item_layout);
            title = (TextView)view.findViewById(R.id.feed_item_title);
            title.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_BOLD));
            text = (TextView)view.findViewById(R.id.feed_item_text);
            text.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_REGULAR));
            creationDate = (TextView)view.findViewById(R.id.feed_item_creation_date);
            view.setOnClickListener(this);
        }

        public void bindNote(final Note note) {
            this.note = note;

            final Context context = itemView.getContext();

            final NoteColorHelper color = NoteColorHelper.fromIndex(context, note.getColor());
            // Set layout style.
            layout.setCardBackgroundColor(color.primaryColor);
            // Set title text and visibility.
            title.setText(note.getTitle());
            title.setTextColor(color.foregroundColor);
            title.setVisibility(!note.getTitle().isEmpty() ? View.VISIBLE : View.GONE);
            // Set text.
            text.setText(note.getText());
            text.setTextColor(color.foregroundColor);
            // Set creation date text and style.
            creationDate.setTextColor(color.secondaryColor);
            creationDate.setText(DateUtils.getRelativeDateTimeString(
                    context,
                    note.getCustomDate().getTime(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS,
                    0));
        }

        @Override
        public void onClick(final View view) {
            NoteActivity.start(itemView.getContext(), note, false);
        }
    }

    public interface OnDataChangedListener {

        public void onDataChanged();
    }
}
