package in.eigene.miary.adapters.viewholders;

import android.view.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.adapters.items.*;

public class RateViewHolder extends FeedAdapter.ViewHolder {

    public RateViewHolder(final View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final FeedAdapter.Item item) {
        bind((RateItem)item);
    }

    private void bind(final RateItem item) {
        // TODO.
    }
}
