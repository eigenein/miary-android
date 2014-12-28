package in.eigene.miary.adapters.viewholders;

import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.adapters.items.*;

public class RateViewHolder extends FeedAdapter.ViewHolder {

    private final TextView title;
    private final TextView text;
    private final Button negative;
    private final Button positive;

    public RateViewHolder(final View itemView) {
        super(itemView);

        title = (TextView)itemView.findViewById(R.id.rate_item_title);
        text = (TextView)itemView.findViewById(R.id.rate_item_text);
        negative = (Button)itemView.findViewById(R.id.rate_item_negative);
        positive = (Button)itemView.findViewById(R.id.rate_item_positive);
    }

    @Override
    public void bind(final FeedAdapter.Item item) {
        bind(((RateItem)item).type);
    }

    private void bind(final RateItem.Type type) {
        // TODO.
    }
}
