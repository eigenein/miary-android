package in.eigene.miary.adapters;

import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.items.*;
import in.eigene.miary.adapters.viewholders.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.lang.*;

import java.util.*;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String LOG_TAG = FeedAdapter.class.getName();

    private final boolean rateItemShown;

    private Mode mode = Mode.DIARY;
    private SortingOrder sortingOrder = SortingOrder.DESCENDING;

    private List<Item> items = new ArrayList<Item>();

    public FeedAdapter(final boolean rateItemShown) {
        this.rateItemShown = rateItemShown;
    }

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

    public void removeItem(final int position) {
        items.remove(position);
        notifyItemRemoved(position);
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
                items = new ArrayList<Item>();
                items.addAll(Util.map(notes, new Function<Note, Item>() {
                    @Override
                    public Item apply(final Note note) {
                        return new NoteItem(note);
                    }
                }));
                if (!rateItemShown && (notes.size() >= 10)) {
                    // Ask user to rate app or give us some feedback.
                    items.add(RateItem.POSITION, new RateItem(FeedAdapter.this));
                }
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
