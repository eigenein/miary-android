package in.eigene.miary.adapters;

import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.items.*;
import in.eigene.miary.adapters.viewholders.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.core.queries.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.lang.*;

import java.util.*;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String LOG_TAG = FeedAdapter.class.getName();

    /**
     * Whether rate item was already shown to the user.
     */
    private boolean rateItemShown;

    private Mode mode = Mode.DIARY;

    private List<Item> items = new ArrayList<Item>();

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.feed_item_note:
                return new NoteViewHolder(view);
            case R.layout.feed_item_rate:
                return new RateViewHolder(view);
            default:
                throw new InternalRuntimeException("Unknown view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).viewType;
    }

    public Mode getMode() {
        return mode;
    }

    public FeedAdapter setMode(final Mode mode) {
        this.mode = mode;
        return this;
    }

    public void setRateItemShown(final boolean rateItemShown) {
        this.rateItemShown = rateItemShown;
    }

    public void removeItem(final int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public FeedAdapter refresh(final OnDataChangedListener listener) {
        Log.d(LOG_TAG, "refresh");

        final ParseQuery<Note> query = getQueryPrefix();
        switch (mode) {
            case DIARY:
                DiaryQueryModifier.INSTANCE.apply(query);
                break;
            case STARRED:
                StarredQueryModifier.INSTANCE.apply(query);
                break;
            case DRAFTS:
                DraftsQueryModifier.INSTANCE.apply(query);
                break;
        }
        query.findInBackground(new FindCallback<Note>() {

            @Override
            public void done(final List<Note> notes, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find notes.", e);
                items = Util.map(notes, new Function<Note, Item>() {
                    @Override
                    public Item apply(final Note note) {
                        return new NoteItem(note);
                    }
                });
                // Ask user to rate app or give us some feedback.
                if (!rateItemShown && (notes.size() >= 30)) {
                    items.add(RateItem.POSITION, new RateItem(FeedAdapter.this));
                }
                // Notify listeners.
                listener.onDataChanged();
                notifyDataSetChanged();
            }
        });

        return this;
    }

    /**
     * Gets common query prefix.
     */
    public static ParseQuery<Note> getQueryPrefix() {
        final ParseQuery<Note> oldQuery = ParseQuery.getQuery(Note.class)
                .whereDoesNotExist(Note.KEY_DELETED);
        final ParseQuery<Note> newQuery = ParseQuery.getQuery(Note.class)
                .whereEqualTo(Note.KEY_DELETED, false);
        final List<ParseQuery<Note>> queries = new ArrayList<ParseQuery<Note>>();
        queries.add(oldQuery);
        queries.add(newQuery);
        return ParseQuery.or(queries).fromLocalDatastore().orderByDescending(Note.KEY_CUSTOM_DATE);
    }

    public static enum Mode {
        DIARY,
        STARRED,
        DRAFTS
    }

    /**
     * Abstract feed item.
     */
    public abstract static class Item {

        public final int viewType;

        public Item(final int viewType) {
            this.viewType = viewType;
        }
    }

    /**
     * Abstract view holder.
     */
    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
        }

        public abstract void bind(final Item item);
    }

    public interface OnDataChangedListener {

        public void onDataChanged();
    }
}
