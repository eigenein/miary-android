package in.eigene.miary.adapters.viewholders;

import android.content.*;
import android.support.v7.widget.*;
import android.text.format.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.adapters.items.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.helpers.*;

import java.util.*;

public class NoteViewHolder extends FeedAdapter.ViewHolder implements View.OnClickListener {

    private final CardView layout;
    private final TextView title;
    private final TextView text;
    private final TextView creationDate;

    private Note note;

    public NoteViewHolder(final View itemView) {
        super(itemView);

        final Context context = itemView.getContext();

        layout = (CardView)itemView.findViewById(R.id.feed_item_layout);
        title = (TextView)itemView.findViewById(R.id.feed_item_title);
        title.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_BOLD));
        text = (TextView)itemView.findViewById(R.id.feed_item_text);
        text.setTypeface(TypefaceCache.get(context, TypefaceCache.ROBOTO_SLAB_REGULAR));
        creationDate = (TextView)itemView.findViewById(R.id.feed_item_creation_date);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bind(final FeedAdapter.Item item) {
        bind(((NoteItem)item).note);
    }

    public void bind(final Note note) {
        this.note = note;

        final Context context = itemView.getContext();

        final NoteColorHelper color = NoteColorHelper.fromIndex(context, note.getColor());
        // Set layout style.
        layout.setCardBackgroundColor(color.primaryColor);
        // Set title text and visibility.
        title.setText(note.getTitle());
        title.setTextColor(color.foregroundColor);
        title.setVisibility(!note.getTitle().isEmpty() ? View.VISIBLE : View.GONE);
        // Set text.
        text.setText(note.getText());
        text.setTextColor(color.foregroundColor);
        // Set creation date text and style.
        creationDate.setTextColor(color.secondaryColor);
        creationDate.setText(getRelativeDateTimeString(context, note.getCustomDate()));
    }

    @Override
    public void onClick(final View view) {
        NoteActivity.start(itemView.getContext(), note, false);
    }

    /**
     * The standard method does not use flags correctly.
     */
    private static CharSequence getRelativeDateTimeString(final Context context, final Date date) {
        final long now = System.currentTimeMillis();
        final long time = date.getTime();
        final long duration = Math.abs(now - time);

        if (duration < DateUtils.DAY_IN_MILLIS) {
            return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS, 0);
        } else {
            return DateUtils.formatDateTime(context, time,
                    DateUtils.FORMAT_SHOW_DATE |
                    DateUtils.FORMAT_ABBREV_MONTH |
                    DateUtils.FORMAT_SHOW_TIME |
                    DateUtils.FORMAT_SHOW_WEEKDAY |
                    DateUtils.FORMAT_ABBREV_WEEKDAY);
        }
    }
}