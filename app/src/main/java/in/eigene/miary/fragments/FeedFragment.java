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
import in.eigene.miary.adapters.*;
import in.eigene.miary.adapters.viewholders.*;
import in.eigene.miary.fragments.base.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.sync.*;

public class FeedFragment extends BaseFragment implements FeedAdapter.OnDataChangedListener {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

    private final BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            swipeRefresh.setRefreshing(false);
            refresh();
        }
    };

    private SharedPreferences preferences;
    private FeedAdapter feedAdapter;

    private MenuItem singleColumnMenuItem;
    private MenuItem multiColumnMenuItem;

    private RecyclerView feedView;
    private View feedEmptyView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        feedAdapter = new FeedAdapter();
        feedAdapter.setRateItemShown(preferences.getBoolean(RateViewHolder.KEY_RATE_ITEM_SHOWN, false));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedView = (RecyclerView)view.findViewById(R.id.feed_view);
        feedView.setHasFixedSize(true); // improve performance
        updateLayoutManager();
        feedView.setAdapter(feedAdapter);

        feedEmptyView = view.findViewById(R.id.feed_empty_view);
        feedEmptyView.setOnClickListener(new NewNoteClickListener(getFeedAdapter()));

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
        singleColumnMenuItem = menu.findItem(R.id.menu_item_feed_set_single_column);
        multiColumnMenuItem = menu.findItem(R.id.menu_item_feed_set_multi_column);
        updateMenu();
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
                final boolean reverse = !preferences.getBoolean(getString(R.string.prefkey_feed_reverse), false);
                // Show toast.
                if (!reverse) {
                    Toast.makeText(getActivity(), R.string.feed_set_descending, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.feed_set_ascending, Toast.LENGTH_SHORT).show();
                }
                // Save current sorting order.
                preferences.edit().putBoolean(getString(R.string.prefkey_feed_reverse), reverse).apply();
                updateLayoutManager();
                return true;
            case R.id.menu_item_feed_set_single_column:
                preferences.edit().putBoolean(getString(R.string.prefkey_multi_column), false).apply();
                updateMenu();
                updateLayoutManager();
                return true;
            case R.id.menu_item_feed_set_multi_column:
                preferences.edit().putBoolean(getString(R.string.prefkey_multi_column), true).apply();
                updateMenu();
                updateLayoutManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDataChanged() {
        final int count = feedAdapter.getItemCount();
        if (count != 0) {
            feedEmptyView.setVisibility(View.GONE);
            feedView.setVisibility(View.VISIBLE);
        } else {
            feedView.setVisibility(View.GONE);
            feedEmptyView.setVisibility(View.VISIBLE);
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

    public FeedAdapter getFeedAdapter() {
        return feedAdapter;
    }

    public void refresh() {
        feedAdapter.refresh(this);
    }

    private void updateLayoutManager() {
        final boolean multiColumn = preferences.getBoolean(getString(R.string.prefkey_multi_column), false);
        final boolean reverse = preferences.getBoolean(getString(R.string.prefkey_feed_reverse), false);

        if (multiColumn) {
            final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                    getResources().getInteger(R.integer.feed_multi_column_columns),
                    StaggeredGridLayoutManager.VERTICAL
            );
            layoutManager.setReverseLayout(reverse);
            feedView.setLayoutManager(layoutManager);
        } else {
            feedView.setLayoutManager(new LinearLayoutManager(
                    getActivity(), LinearLayoutManager.VERTICAL, reverse));
        }
    }

    private void updateMenu() {
        final boolean multiColumn = preferences.getBoolean(getString(R.string.prefkey_multi_column), false);
        singleColumnMenuItem.setVisible(multiColumn);
        multiColumnMenuItem.setVisible(!multiColumn);
    }
}
