package in.eigene.miary.core.caches;

import com.parse.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.lang.*;

/**
 * Stores note counters.
 */
public class CounterCache {

    public static Counter NOTE_COUNTER = new Counter(getQueryPrefix().whereEqualTo(Note.KEY_DRAFT, false));
    public static Counter STARRED_COUNTER = new Counter(getQueryPrefix().whereEqualTo(Note.KEY_STARRED, true));
    public static Counter DRAFT_COUNTER = new Counter(getQueryPrefix().whereEqualTo(Note.KEY_DRAFT, true));

    /**
     * Cached counter.
     */
    public static class Counter implements Function<Consumer<Integer>, Integer> {

        private final ParseQuery query;
        private int cachedValue;

        public Counter(final ParseQuery query) {
            this.query = query;
        }

        @Override
        public Integer apply(final Consumer<Integer> consumer) {
            query.countInBackground(new CountCallback() {
                @Override
                public void done(final int count, final ParseException e) {
                    InternalRuntimeException.throwForException("Could not get count", e);
                    cachedValue = count;
                    consumer.accept(cachedValue);
                }
            });
            return cachedValue;
        }
    }

    private static ParseQuery<Note> getQueryPrefix() {
        return ParseQuery.getQuery(Note.class).fromLocalDatastore();
    }
}
