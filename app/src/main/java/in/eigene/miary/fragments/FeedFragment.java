package in.eigene.miary.fragments;

import android.accounts.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.fragments.base.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.sync.*;

public class FeedFragment extends BaseFragment {

    private static final String KEY_FEED_SORTING_ORDER_NAME = "feed_sorting_order_name";

    private final BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            swipeRefresh.setRefreshing(false);
            refresh();
        }
    };

    private final NotesAdapter notesAdapter = new NotesAdapter();

    private RecyclerView feedView;
    private View feedEmptyView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedView = (RecyclerView)view.findViewById(R.id.feed_view);
        feedView.setHasFixedSize(true); // improve performance
        feedView.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.feed_columns),
                StaggeredGridLayoutManager.VERTICAL
        ));
        feedView.setAdapter(notesAdapter);

        feedEmptyView = view.findViewById(R.id.feed_empty_view);
        feedEmptyView.setOnClickListener(new NewNoteClickListener());

        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.feed_refresh);
        swipeRefresh.setColorSchemeResources(R.color.blue_500, R.color.light_green_500, R.color.yellow_500, R.color.red_500);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Account account = AccountManagerHelper.getAccount(getActivity());
                if (account != null) {
                    final Bundle extras = new Bundle();
                    extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                    extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    ContentResolver.requestSync(account, SyncAdapter.AUTHORITY, extras);
                } else {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
        getActivity().registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_EVENT_NAME));
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(syncFinishedReceiver);
        super.onPause();
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_feed_change_sort_order:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fixes top padding on different toolbar sizes.
     */
    public void fixTopPadding(final Context context) {
        final TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, value, true)) {
            final int actionBarSize = TypedValue.complexToDimensionPixelSize(value.data, context.getResources().getDisplayMetrics());
            final int feedItemMargin = context.getResources().getDimensionPixelSize(R.dimen.feed_item_external_margin);
            final int padding = actionBarSize + feedItemMargin;
            feedView.setPadding(0, padding, 0, 0);
            swipeRefresh.setProgressViewOffset(false, 0, padding);
        }
    }

    public NotesAdapter getNotesAdapter() {
        return notesAdapter;
    }

    public void refresh() {
        // TODO: notesAdapter.refresh(this);
    }
}
