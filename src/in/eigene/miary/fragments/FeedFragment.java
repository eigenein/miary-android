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

        final FeedItemsAdapter adapter = getAdapter();
        if (adapter == null) {
            // Initially set the adapter.
            queryFeedItemsPage(null, new Action<List<Note>>() {
                @Override
                public void done(final List<Note> notes) {
                    feedListView.setAdapter(new FeedItemsAdapter(getActivity(), notes));
                    feedListView.setOnScrollListener(new EndlessScrollListener(FeedFragment.this));
                }
            });
        } else {
            // Items might be changed.
            adapter.notifyDataSetChanged();
        }
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
        queryFeedItemsPage(lastNote.getCreationDate(), new Action<List<Note>>() {
            @Override
            public void done(final List<Note> notes) {

                existingNotes.addAll(notes);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * https://stackoverflow.com/questions/4404177/classcastexception-with-listview-when-executing-notifydatasetchanged
     */
    private FeedItemsAdapter getAdapter() {
        final ListAdapter adapter = feedListView.getAdapter();
        if (adapter == null) {
            return null;
        }
        if (adapter instanceof FeedItemsAdapter) {
            return (FeedItemsAdapter)adapter;
        }
        return (FeedItemsAdapter)((HeaderViewListAdapter)adapter).getWrappedAdapter();
    }

    /**
     * Queries feed items and updates feed.
     */
    private void queryFeedItemsPage(final Date fromCreationDate, final Action<List<Note>> action) {
        Log.i(LOG_TAG, "Querying notes from " + fromCreationDate);

        // TODO: limiting, sorting and infinite scrolling.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        if (fromCreationDate != null) {
            query.whereLessThan(Note.KEY_CREATION_DATE, fromCreationDate);
        }
        query.orderByDescending(Note.KEY_CREATION_DATE);
        query.setLimit(PAGE_SIZE);
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
