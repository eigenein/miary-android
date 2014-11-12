package in.eigene.miary.fragments;

import android.content.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.fragments.base.*;
import in.eigene.miary.helpers.*;

public class FeedFragment extends BaseFragment implements FeedAdapter.OnDataChangedListener {

    private FeedAdapter feedAdapter;

    private RecyclerView feedView;
    private View feedEmptyView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        feedAdapter = new FeedAdapter();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);
        feedView = (RecyclerView)view.findViewById(R.id.feed_view);
        feedView.setHasFixedSize(true); // improve performance
        feedView.setLayoutManager(new GridLayoutManager(
                getActivity(), getResources().getInteger(R.integer.feed_columns)));
        feedView.setAdapter(feedAdapter);
        feedEmptyView = view.findViewById(R.id.feed_empty_view);
        feedEmptyView.setOnClickListener(new NewNoteClickListener(getFeedAdapter()));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_feed_change_sort_order:
                final FeedAdapter.SortingOrder order = feedAdapter.swapSortingOrder().refresh(this).getSortingOrder();
                if (order == FeedAdapter.SortingOrder.DESCENDING) {
                    Toast.makeText(getActivity(), R.string.feed_set_descending, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.feed_set_ascending, Toast.LENGTH_SHORT).show();
                }
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
     * Fixes feed view padding on different toolbar sizes.
     */
    public void fixFeedViewPadding(final Context context) {
        final TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, value, true)) {
            final int actionBarSize = TypedValue.complexToDimensionPixelSize(value.data, context.getResources().getDisplayMetrics());
            final int feedItemMargin = context.getResources().getDimensionPixelSize(R.dimen.feed_item_margin);
            feedView.setPadding(0, actionBarSize + feedItemMargin, 0, 0);
        }
    }

    public FeedAdapter getFeedAdapter() {
        return feedAdapter;
    }

    public void refresh() {
        feedAdapter.refresh(this);
    }
}
