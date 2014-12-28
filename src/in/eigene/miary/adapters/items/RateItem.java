package in.eigene.miary.adapters.items;

import in.eigene.miary.*;
import in.eigene.miary.adapters.*;

public class RateItem extends FeedAdapter.Item {

    public static enum Type {
        ENJOYING,
        RATING,
        FEEDBACK
    }

    public Type type = Type.ENJOYING;

    private final FeedAdapter adapter;

    public RateItem(final FeedAdapter adapter) {
        super(R.layout.feed_item_rate);
        this.adapter = adapter;
    }

    public void notifyChanged() {
        adapter.notifyItemChanged(0);
    }
}
