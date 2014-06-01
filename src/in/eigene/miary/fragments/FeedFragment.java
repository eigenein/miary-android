package in.eigene.miary.fragments;

import android.os.*;
import android.support.v4.app.*;
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

public class FeedFragment extends Fragment implements AdapterView.OnItemClickListener, EndlessScrollListener.Listener {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

    private static final int PAGE_SIZE = 10; // for endless scrolling

    private ListView feedListView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.feed_fragment, container, false);
        feedListView = (ListView)view.findViewById(R.id.feed_list_view);
        feedListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFeed();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Note note = (Note)parent.getItemAtPosition(position);
        // This fragment is always attached to feed activity.
        final FeedActivity feedActivity = (FeedActivity)getActivity();
        feedActivity.startNoteActivity(note);
    }

    @Override
    public void onScrolledToEnd() {
        final FeedItemsAdapter adapter = getAdapter();

        final List<Note> existingNotes = adapter.getNotes();
        if (existingNotes.size() == 0) {
            Log.w(LOG_TAG, "Scrolled to the end of an empty list.");
            return;
        }

        final Note lastNote = existingNotes.get(existingNotes.size() - 1);
        queryFeedItems(lastNote.getCreationDate(), null, new Action<List<Note>>() {
            @Override
            public void done(final List<Note> notes) {
                existingNotes.addAll(notes);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private FeedItemsAdapter getAdapter() {
        return (FeedItemsAdapter)feedListView.getAdapter();
    }

    /**
     * Initializes feed adapter.
     */
    private void initializeFeed() {
        // Remember scroll location.
        final int scrollIndex = feedListView.getFirstVisiblePosition();
        final View topView = feedListView.getChildAt(0);
        final int scrollTop = (topView != null) ? topView.getTop() : 0;
        // Remember last note creation time.
        Date lastNoteCreationTime = null;
        final FeedItemsAdapter adapter = getAdapter();
        if (adapter != null) {
            final List<Note> notes = adapter.getNotes();
            if (notes.size() != 0) {
                lastNoteCreationTime = notes.get(notes.size() - 1).getCreationDate();
            }
        }

        queryFeedItems(null, lastNoteCreationTime, new Action<List<Note>>() {
            @Override
            public void done(final List<Note> notes) {
                feedListView.setAdapter(new FeedItemsAdapter(getActivity(), notes));
                // Restore scroll position.
                feedListView.setSelectionFromTop(scrollIndex, scrollTop);
                feedListView.setOnScrollListener(new EndlessScrollListener(FeedFragment.this));
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
        Log.i(LOG_TAG, "Querying notes from " + fromCreationDate + " down to " + toCreationDate);

        // TODO: limiting, sorting and infinite scrolling.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        if (fromCreationDate != null) {
            query.whereLessThan(Note.KEY_CREATION_DATE, fromCreationDate);
        }
        query.orderByDescending(Note.KEY_CREATION_DATE);
        if (toCreationDate == null) {
            query.setLimit(PAGE_SIZE);
        } else {
            query.whereGreaterThanOrEqualTo(Note.KEY_CREATION_DATE, toCreationDate);
        }
        query.findInBackground(new FindCallback<Note>() {

            @Override
            public void done(final List<Note> notes, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find notes.", e);
                Log.i(LOG_TAG, "Found notes: " + notes.size());
                action.done(notes);
            }
        });
    }
}
