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

import java.util.*;

public class FeedFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

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
    public void onStart() {
        super.onStart();
        updateFeed();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed, menu);
    }

    /**
     * Update the feed with notes.
     */
    private void updateFeed() {
        // TODO: limiting, sorting and infinite scrolling.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        query.orderByDescending(Note.CREATION_DATE_KEY);
        query.findInBackground(new FindCallback<Note>() {
            @Override
            public void done(final List<Note> notes, final ParseException e) {
                if (e != null) {
                    throw new InternalRuntimeException("Failed to find notes.", e);
                }
                Log.i(LOG_TAG, "Found notes: " + notes.size());
                feedListView.setAdapter(new FeedItemAdapter(getActivity(), notes));
            }
        });
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Note note = (Note)parent.getItemAtPosition(position);
        // This fragment is always attached to feed activity.
        final FeedActivity feedActivity = (FeedActivity)getActivity();
        feedActivity.startNoteActivity(note);
    }
}
