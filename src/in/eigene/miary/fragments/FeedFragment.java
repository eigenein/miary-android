package in.eigene.miary.fragments;

import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.fragments.base.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.lang.*;

import java.util.*;

public class FeedFragment
        extends BaseFragment
        implements AdapterView.OnItemClickListener, EndlessScrollListener.Listener {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

    private static final int PAGE_SIZE = 10; // for endless scrolling

    private boolean drafts;
    private boolean starredOnly;

    private ListView feedListView;
    private EndlessScrollListener scrollListener;

    private View feedEmptyView;

    public FeedFragment setDrafts(final boolean drafts) {
        this.drafts = drafts;
        return this;
    }

    public FeedFragment setStarredOnly(final boolean starredOnly) {
        this.starredOnly = starredOnly;
        return this;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);
        feedListView = (ListView)view.findViewById(R.id.feed_list_view);
        feedListView.setOnItemClickListener(this);
        scrollListener = new EndlessScrollListener(FeedFragment.this);
        feedListView.setOnScrollListener(scrollListener);
        feedEmptyView = view.findViewById(R.id.feed_empty_view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_settings:
                SettingsActivity.start(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh(true);
        // Check if the end of page is reached.
        scrollListener.onScrollStateChanged(feedListView, EndlessScrollListener.SCROLL_STATE_IDLE);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Note note = (Note)parent.getItemAtPosition(position);
        NoteActivity.start(getActivity(), note, false);
    }

    @Override
    public void onScrolledToEnd() {
        final FeedItemsAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        final Note lastNote = getLastNote(adapter);
        if (lastNote == null) {
            return;
        }
        queryFeedItems(lastNote.getCustomDate(), null, new Consumer<List<Note>>() {
            @Override
            public void accept(final List<Note> notes) {
                adapter.getNotes().addAll(notes);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Refreshes feed by either initializing adapter or changing data set.
     */
    public void refresh(final boolean reuseAdapter) {
        Log.i(LOG_TAG, "Refresh: reuse adapter: " + reuseAdapter);

        // Remember last note creation time.
        Date lastNoteCreationDate = null;
        final FeedItemsAdapter adapter = reuseAdapter ? getAdapter() : null;
        if (adapter != null) {
            final Note lastNote = getLastNote(adapter);
            if (lastNote != null) {
                lastNoteCreationDate = lastNote.getCustomDate();
            }
        }

        queryFeedItems(null, lastNoteCreationDate, new Consumer<List<Note>>() {
            @Override
            public void accept(final List<Note> notes) {
                if (adapter != null) {
                    adapter.getNotes().clear();
                    adapter.getNotes().addAll(notes);
                    adapter.notifyDataSetChanged();
                } else if (isAdded()) {
                    feedListView.setAdapter(new FeedItemsAdapter(getActivity(), notes));
                }
                switchViews();
            }
        });
    }

    /**
     * Gets feed items adapter.
     */
    private FeedItemsAdapter getAdapter() {
        return (FeedItemsAdapter)feedListView.getAdapter();
    }

    /**
     * Gets last feed item.
     */
    private Note getLastNote(final FeedItemsAdapter adapter) {
        final List<Note> notes = adapter.getNotes();
        if (notes.size() != 0) {
            return notes.get(notes.size() - 1);
        }
        return null;
    }

    /**
     * Queries feed items.
     */
    private void queryFeedItems(
            final Date fromCreationDate,
            final Date toCreationDate,
            final Consumer<List<Note>> action) {
        Log.i(LOG_TAG, "Querying notes from " + fromCreationDate + " to " + toCreationDate);
        // Initialize query.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        // Paging.
        if (fromCreationDate != null) {
            query.whereLessThan(Note.KEY_CUSTOM_DATE, fromCreationDate);
        }
        // Limiting.
        if (toCreationDate == null) {
            query.setLimit(PAGE_SIZE);
        } else {
            query.whereGreaterThanOrEqualTo(Note.KEY_CUSTOM_DATE, toCreationDate);
        }
        // Drafts and Starred.
        if (starredOnly) {
            query.whereEqualTo(Note.KEY_STARRED, true);
        } else {
            query.whereEqualTo(Note.KEY_DRAFT, drafts);
        }
        // Ordering.
        query.orderByDescending(Note.KEY_CUSTOM_DATE);

        query.findInBackground(new FindCallback<Note>() {

            @Override
            public void done(final List<Note> notes, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find notes.", e);
                Log.i(LOG_TAG, "Found notes: " + notes.size());
                action.accept(notes);
            }
        });
    }

    /**
     * Checks if feed is empty and switches views.
     */
    private void switchViews() {
        if (isAdded()) {
            final boolean isEmpty = getAdapter().getNotes().isEmpty();
            feedEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            feedListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }
}
