package in.eigene.miary.adapters.viewholders;

import android.content.*;
import android.net.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.adapters.items.*;
import in.eigene.miary.helpers.*;

public class RateViewHolder extends FeedAdapter.ViewHolder {

    public static final String KEY_RATE_ITEM_SHOWN = "rate_item_shown";

    private final TextView title;
    private final TextView text;
    private final Button negative;
    private final Button positive;

    private State state;

    public RateViewHolder(final View itemView) {
        super(itemView);

        title = (TextView)itemView.findViewById(R.id.rate_item_title);
        text = (TextView)itemView.findViewById(R.id.rate_item_text);
        negative = (Button)itemView.findViewById(R.id.rate_item_negative);
        positive = (Button)itemView.findViewById(R.id.rate_item_positive);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!state.isClicked()) {
                    state.onClicked(view, false);
                }
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!state.isClicked()) {
                    state.onClicked(view, true);
                }
            }
        });
    }

    @Override
    public void bind(final FeedAdapter.Item item) {
        final RateItem rateItem = (RateItem)item;
        switch (rateItem.type) {
            case ENJOYING:
                state = new EnjoyingState();
                break;
            case RATING:
                state = new RatingState();
                break;
            case FEEDBACK:
                state = new FeedbackState();
                break;
        }
        state.bind(rateItem);
    }

    private void setRateItemShown() {
        PreferenceManager.getDefaultSharedPreferences(itemView.getContext())
                .edit()
                .putBoolean(KEY_RATE_ITEM_SHOWN, true)
                .commit();
    }

    abstract class State {

        protected RateItem item;
        protected boolean clicked;

        public void bind(final RateItem item) {
            this.item = item;
        }

        public void onClicked(final View view, final boolean positive) {
            clicked = true;
        }

        public boolean isClicked() {
            return clicked;
        }
    }

    class EnjoyingState extends State {

        @Override
        public void bind(final RateItem item) {
            super.bind(item);
            title.setVisibility(View.VISIBLE);
            title.setText(R.string.rate_item_welcome);
            text.setText(R.string.rate_item_enjoying);
            negative.setText(R.string.not_really_button);
            positive.setText(R.string.yes_button);
        }

        @Override
        public void onClicked(final View view, final boolean positive) {
            super.onClicked(view, positive);
            if (positive) {
                item.type = RateItem.Type.RATING;
            } else {
                item.type = RateItem.Type.FEEDBACK;
            }
            item.notifyItemChanged();
            ParseHelper.trackEvent("enjoying", "positive", Boolean.toString(positive));
        }
    }

    class RatingState extends State {

        @Override
        public void bind(final RateItem item) {
            super.bind(item);
            title.setVisibility(View.GONE);
            text.setText(R.string.rate_item_rating);
            negative.setText(R.string.no_button);
            positive.setText(R.string.yes_button);
        }

        @Override
        public void onClicked(final View view, final boolean positive) {
            super.onClicked(view, positive);
            if (positive) {
                final Context context = view.getContext();
                final int flags =
                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
                try {
                    context.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + context.getPackageName()))
                            .addFlags(flags));
                } catch (final ActivityNotFoundException e) {
                    context.startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()))
                            .addFlags(flags));
                }
            }
            setRateItemShown();
            item.remove();
            ParseHelper.trackEvent("rate", "positive", Boolean.toString(positive));
        }
    }

    class FeedbackState extends State {

        @Override
        public void bind(final RateItem item) {
            super.bind(item);
            title.setVisibility(View.GONE);
            text.setText(R.string.rate_item_feedback);
            negative.setText(R.string.no_button);
            positive.setText(R.string.yes_button);
        }

        @Override
        public void onClicked(final View view, final boolean positive) {
            super.onClicked(view, positive);
            if (positive) {
                ActivityHelper.start(view.getContext(), FeedbackActivity.class);
            }
            setRateItemShown();
            item.remove();
            ParseHelper.trackEvent("feedback", "positive", Boolean.toString(positive));
        }
    }
}
