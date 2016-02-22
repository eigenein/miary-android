package in.eigene.miary.fragments;

import android.accounts.Account;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import in.eigene.miary.R;
import in.eigene.miary.adapters.NotesAdapter;
import in.eigene.miary.fragments.base.BaseFragment;
import in.eigene.miary.helpers.AccountManagerHelper;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.sync.SyncAdapter;

public class FeedFragment
        extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    /**
     * Hides swipe refresh when sync is finished.
     */
    private final BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            swipeRefresh.setRefreshing(false);
        }
    };

    /**
     * Notes cursor adapter.
     */
    private final NotesAdapter notesAdapter = new NotesAdapter();

    private SharedPreferences preferences;

    // Feed parameters.

    private Note.Section section = Note.Section.DIARY;
    private Note.SortOrder sortOrder;

    // Views.

    private RecyclerView feedView;
    private ImageView emptyImageView;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        preferences = PreferenceHelper.get(getActivity());
        sortOrder = Note.SortOrder.valueOf(preferences.getString(PreferenceHelper.KEY_SORT_ORDER, Note.SortOrder.NEWEST_FIRST.name()));

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedView = (RecyclerView)view.findViewById(R.id.feed_view);
        feedView.setHasFixedSize(true); // improve performance
        updateLayoutManager();
        feedView.setAdapter(notesAdapter);

        emptyImageView = (ImageView)view.findViewById(R.id.feed_empty_view);

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
        swipeRefresh.setEnabled(false); // TODO: temporarily disabled to not confuse users.

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final boolean multiColumn = preferences.getBoolean(PreferenceHelper.KEY_MULTI_COLUMN, true);
        menu.findItem(R.id.menu_item_feed_set_single_column).setVisible(multiColumn);
        menu.findItem(R.id.menu_item_feed_set_multi_column).setVisible(!multiColumn);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(syncFinishedReceiver, new IntentFilter(SyncAdapter.SYNC_FINISHED_EVENT_NAME));

        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
                setSortOrder(sortOrder.getOpposite());
                switch (sortOrder) {
                    case NEWEST_FIRST:
                        Toast.makeText(getActivity(), R.string.feed_newest_first, Toast.LENGTH_SHORT).show();
                        break;
                    case OLDEST_FIRST:
                        Toast.makeText(getActivity(), R.string.feed_oldest_first, Toast.LENGTH_SHORT).show();
                        break;
                }
                PreferenceHelper.edit(getActivity()).putString(PreferenceHelper.KEY_SORT_ORDER, sortOrder.name()).apply();
                Tracking.sendEvent(Tracking.Category.VIEW, Tracking.Action.SET_SORTING_ORDER, sortOrder.toString());
                return true;

            case R.id.menu_item_feed_set_single_column:
                preferences.edit().putBoolean(PreferenceHelper.KEY_MULTI_COLUMN, false).apply();
                invalidateOptionsMenu();
                updateLayoutManager();
                Tracking.sendEvent(Tracking.Category.VIEW, Tracking.Action.SET_LAYOUT, "Single Column");
                return true;

            case R.id.menu_item_feed_set_multi_column:
                preferences.edit().putBoolean(PreferenceHelper.KEY_MULTI_COLUMN, true).apply();
                invalidateOptionsMenu();
                updateLayoutManager();
                Tracking.sendEvent(Tracking.Category.VIEW, Tracking.Action.SET_LAYOUT, "Multi-column");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new CursorLoader(
                getActivity(),
                Note.Contract.CONTENT_URI,
                Note.PROJECTION,
                section.getSelection(),
                null,
                sortOrder.getSortOrder()
        );
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if ((cursor != null) && (cursor.getCount() != 0)) {
            notesAdapter.setCursor(cursor);
            emptyImageView.setVisibility(View.GONE);
            feedView.setVisibility(View.VISIBLE);
        } else {
            feedView.setVisibility(View.GONE);
            emptyImageView.setImageResource(section.getImageResourceId());
            emptyImageView.setVisibility(View.VISIBLE);
            notesAdapter.setCursor(null);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        onLoadFinished(loader, null);
    }

    /**
     * Gets currently displayed section.
     */
    public Note.Section getSection() {
        return section;
    }

    public void setSection(final Note.Section section) {
        this.section = section;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Sets note sort order.
     */
    private void setSortOrder(final Note.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Updates feed layout manager.
     */
    private void updateLayoutManager() {
        final RecyclerView.LayoutManager layoutManager;
        if (preferences.getBoolean(PreferenceHelper.KEY_MULTI_COLUMN, true)) {
            layoutManager = new StaggeredGridLayoutManager(
                    getResources().getInteger(R.integer.feed_multi_column_columns),
                    StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        feedView.setLayoutManager(layoutManager);
    }
}
