package in.eigene.miary.fragments;

import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.fragments.base.*;

public class FeedFragment extends BaseFragment {

    private static final String LOG_TAG = FeedFragment.class.getSimpleName();

    private static final int PAGE_SIZE = 10; // for endless scrolling

    private FeedAdapter feedAdapter;

    private RecyclerView feedView;
    private View feedEmptyView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);
        feedView = (RecyclerView)view.findViewById(R.id.feed_view);
        feedView.setHasFixedSize(true); // improve performance
        feedView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedAdapter = new FeedAdapter();
        feedView.setAdapter(feedAdapter);
        feedEmptyView = view.findViewById(R.id.feed_empty_view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.feed_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        feedAdapter.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_feed_change_sort_order:
                final FeedAdapter.SortingOrder order = feedAdapter.swapSortingOrder().refresh().getSortingOrder();
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

    public FeedAdapter getFeedAdapter() {
        return feedAdapter;
    }
}
