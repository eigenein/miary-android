package in.eigene.miary.core.caches;

import com.parse.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.queries.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.lang.*;

/**
 * Stores note counters.
 */
public class CounterCache {

    public static Counter DIARY_COUNTER = new Counter(DiaryQueryModifier.INSTANCE);
    public static Counter STARRED_COUNTER = new Counter(StarredQueryModifier.INSTANCE);
    public static Counter DRAFT_COUNTER = new Counter(DraftsQueryModifier.INSTANCE);

    /**
     * Cached counter.
     */
    public static class Counter implements Function<Consumer<Integer>, Integer> {

        private final QueryModifier queryModifier;
        private int cachedValue;

        public Counter(final QueryModifier queryModifier) {
            this.queryModifier = queryModifier;
        }

        @Override
        public Integer apply(final Consumer<Integer> consumer) {
            queryModifier.apply(FeedAdapter.getQueryPrefix()).countInBackground(new CountCallback() {
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
}
