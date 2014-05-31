package in.eigene.miary.helpers;

import android.util.*;
import android.widget.*;

/**
 * Enables endless scrolling for a list view.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    public static interface Listener {
        void onScrolledToEnd();
    }

    private static String LOG_TAG = EndlessScrollListener.class.getSimpleName();

    private final Listener listener;

    private int lastItemIndex;

    public EndlessScrollListener(final Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        // Do nothing.
    }

    @Override
    public void onScroll(
            final AbsListView view,
            final int firstVisibleItem,
            final int visibleItemCount,
            final int totalItemCount) {
        // Get the last visible item index.
        final int lastItemIndex = firstVisibleItem + visibleItemCount;
        // Check if the last item is reached.
        if (lastItemIndex != totalItemCount) {
            return;
        }
        // Prevent double triggering.
        if (this.lastItemIndex == lastItemIndex) {
            return;
        }
        // Trigger event.
        this.lastItemIndex = lastItemIndex;
        Log.i(LOG_TAG, "Scrolled to the end.");
        listener.onScrolledToEnd();
    }
}
