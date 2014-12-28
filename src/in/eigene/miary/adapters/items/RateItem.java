package in.eigene.miary.adapters.items;

import in.eigene.miary.*;
import in.eigene.miary.adapters.*;

public class RateItem extends FeedAdapter.Item {

    public static final int POSITION = 0;

    public Type type = Type.ENJOYING;

    private final FeedAdapter adapter;

    public RateItem(final FeedAdapter adapter) {
        super(R.layout.feed_item_rate);
        this.adapter = adapter;
    }

    public void notifyItemChanged() {
        adapter.notifyItemChanged(POSITION);
    }

    public void remove() {
        adapter.removeItem(POSITION);
    }

    public static enum Type {
        ENJOYING,
        RATING,
        FEEDBACK
    }
}
