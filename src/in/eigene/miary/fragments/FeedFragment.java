package in.eigene.miary.fragments;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.util.*;

public class FeedFragment
        extends Fragment
        implements AdapterView.OnItemClickListener, EndlessScrollListener.Listener {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

    private static final int PAGE_SIZE = 10; // for endless scrolling

    /**
     * Whether to show drafts only.
     */
    private final boolean drafts;
    /**
     * Whether to show starred notes only.
     */
    private final boolean starredOnly;

    private ListView feedListView;
    private EndlessScrollListener scrollListener;

    private View feedEmptyView;

    public FeedFragment(final boolean drafts, final boolean starredOnly) {
        this.drafts = drafts;
        this.starredOnly = starredOnly;
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
        inflater.inflate(R.menu.feed, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshFeed();
        // Check if the end of page is reached.
        scrollListener.onScrollStateChanged(feedListView, EndlessScrollListener.SCROLL_STATE_IDLE);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Note note = (Note)parent.getItemAtPosition(position);
        NoteActivity.start(getActivity(), note);
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
        queryFeedItems(lastNote.getCreationDate(), null, new Action<List<Note>>() {
            @Override
            public void done(final List<Note> notes) {
                adapter.getNotes().addAll(notes);
                adapter.notifyDataSetChanged();
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
     * Refreshes feed by either initializing adapter or changing data set.
     */
    private void refreshFeed() {
        // Remember last note creation time.
        Date lastNoteCreationDate = null;
        final FeedItemsAdapter adapter = getAdapter();
        if (adapter != null) {
            final Note lastNote = getLastNote(adapter);
            if (lastNote != null) {
                lastNoteCreationDate = lastNote.getCreationDate();
            }
        }

        queryFeedItems(null, lastNoteCreationDate, new Action<List<Note>>() {
            @Override
            public void done(final List<Note> notes) {
                if (!isAdded()) {
                    // https://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to-activity
                    return;
                }
                if (adapter != null) {
                    adapter.getNotes().clear();
                    adapter.getNotes().addAll(notes);
                    adapter.notifyDataSetChanged();
                } else {
                    feedListView.setAdapter(new FeedItemsAdapter(getActivity(), notes));
                }
                switchViews();
            }
        });
    }

    /**
     * Queries feed items.
     */
    private void queryFeedItems(
            final Date fromCreationDate,
            final Date toCreationDate,
            final Action<List<Note>> action) {
        Log.i(LOG_TAG, "Querying notes from " + fromCreationDate + " to " + toCreationDate);
        // Initialize query.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        // Paging.
        if (fromCreationDate != null) {
            query.whereLessThan(Note.KEY_CREATION_DATE, fromCreationDate);
        }
        // Limiting.
        if (toCreationDate == null) {
            query.setLimit(PAGE_SIZE);
        } else {
            query.whereGreaterThanOrEqualTo(Note.KEY_CREATION_DATE, toCreationDate);
        }
        // Drafts and Starred.
        query.whereEqualTo(Note.KEY_DRAFT, drafts);
        if (starredOnly) {
            query.whereEqualTo(Note.KEY_STARRED, true);
        }
        // Ordering.
        query.orderByDescending(Note.KEY_CREATION_DATE);

        query.findInBackground(new FindCallback<Note>() {

            @Override
            public void done(final List<Note> notes, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find notes.", e);
                Log.i(LOG_TAG, "Found notes: " + notes.size());
                action.done(notes);
            }
        });
    }

    /**
     * Checks if feed is empty and switches views.
     */
    private void switchViews() {
        final boolean isEmpty = getAdapter().getNotes().isEmpty();
        feedEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        feedListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
