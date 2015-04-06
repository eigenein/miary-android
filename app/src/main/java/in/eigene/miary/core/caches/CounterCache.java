package in.eigene.miary.core.caches;

import in.eigene.miary.core.persistence.Note;
import in.eigene.miary.helpers.lang.Consumer;
import in.eigene.miary.helpers.lang.Function;

/**
 * Stores note counters.
 */
public class CounterCache {

    public static Counter DIARY_COUNTER = new Counter(Note.Section.DIARY);
    public static Counter STARRED_COUNTER = new Counter(Note.Section.STARRED);
    public static Counter DRAFT_COUNTER = new Counter(Note.Section.DRAFTS);

    /**
     * Cached counter.
     */
    public static class Counter implements Function<Consumer<Integer>, Integer> {

        private final Note.Section section;
        private int cachedValue;

        public Counter(final Note.Section section) {
            this.section = section;
        }

        @Override
        public Integer apply(final Consumer<Integer> consumer) {
            // TODO.
            return cachedValue;
        }
    }
}
