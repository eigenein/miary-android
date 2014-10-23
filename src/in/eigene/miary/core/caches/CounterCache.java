package in.eigene.miary.core.caches;

import com.parse.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.lang.*;

public class CounterCache {

    private static int starredCount;
    private static int draftCount;

    public static int getStarredCount() {
        return starredCount;
    }

    public static int getDraftCount() {
        return draftCount;
    }

    /**
     * Invalidates cache.
     */
    public static void invalidate(final Consumer<Object> callback) {
        getQueryPrefix().whereEqualTo(Note.KEY_STARRED, true).countInBackground(new CountCallback() {
            @Override
            public void done(final int count, final ParseException e) {
                InternalRuntimeException.throwForException("Could not get starred count", e);
                starredCount = count;

                getQueryPrefix().whereEqualTo(Note.KEY_DRAFT, true).countInBackground(new CountCallback() {
                    @Override
                    public void done(final int count, final ParseException e) {
                        InternalRuntimeException.throwForException("Could not get draft count.", e);
                        draftCount = count;

                        callback.accept(null);
                    }
                });
            }
        });
    }

    private static ParseQuery<Note> getQueryPrefix() {
        return ParseQuery.getQuery(Note.class).fromLocalDatastore();
    }
}
