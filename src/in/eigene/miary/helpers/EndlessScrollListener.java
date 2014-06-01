package in.eigene.miary.helpers;

import android.util.*;
import android.widget.*;

/**
 * Enables endless scrolling for a list view.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private final int THRESHOLD = 5;

    public static interface Listener {
        void onScrolledToEnd();
    }

    private static String LOG_TAG = EndlessScrollListener.class.getSimpleName();

    private final Listener listener;

    public EndlessScrollListener(final Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        if (scrollState != SCROLL_STATE_IDLE) {
            return;
        }
        if (view.getLastVisiblePosition() < view.getCount() - 1 - THRESHOLD) {
            return;
        }
        Log.i(LOG_TAG, "Scrolled to the end.");
        listener.onScrolledToEnd();
    }

    @Override
    public void onScroll(
            final AbsListView view,
            final int firstVisibleItem,
            final int visibleItemCount,
            final int totalItemCount) {
        // Do nothing.
    }
}
