package in.eigene.miary.core.caches;

import in.eigene.miary.core.queries.*;
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
            // TODO.
            return cachedValue;
        }
    }
}
