package in.eigene.miary.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.Date;

import in.eigene.miary.R;
import in.eigene.miary.activities.NoteActivity;
import in.eigene.miary.helpers.ColorHelper;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.TypefaceCache;
import in.eigene.miary.persistence.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final Activity activity;

    private Cursor cursor = null;
    private int idColumnIndex;
    private int lastAnimatedPosition;

    public NotesAdapter(final Activity activity) {
        this.activity = activity;
    }

    public void setCursor(final Cursor cursor) {
        final Cursor oldCursor = this.cursor;
        this.cursor = cursor;

        lastAnimatedPosition = -1;
        notifyDataSetChanged();

        if (oldCursor != null) {
            oldCursor.close();
        }
        if (cursor != null) {
            idColumnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new NoteViewHolder(activity, itemView);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {
        final Cursor cursor = this.cursor;
        if (!cursor.moveToPosition(position)) {
            throw new IndexOutOfBoundsException(String.format("%d of %d", cursor.getCount(), position));
        }
        runEnterAnimation(holder.itemView, position);
        holder.bind(Note.getByCursor(cursor));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(idColumnIndex);
        }
        return 0L;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.feed_item_note;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    private void runEnterAnimation(final View view, final int position) {
        if (position <= lastAnimatedPosition) {
            return;
        }
        lastAnimatedPosition = position;
        view.setTranslationY(activity.getWindow().getDecorView().getHeight());
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3f))
                .setDuration(700)
                .start();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final Activity activity;
        public final CardView layout;
        public final TextView title;
        public final TextView text;
        public final TextView creationDate;

        public Note note;

        public NoteViewHolder(final Activity activity, final View itemView) {
            super(itemView);

            this.activity = activity;

            layout = (CardView)itemView.findViewById(R.id.feed_item_layout);
            title = (TextView)itemView.findViewById(R.id.feed_item_title);
            title.setTypeface(TypefaceCache.get(activity, TypefaceCache.ROBOTO_CONDENSED_BOLD));
            text = (TextView)itemView.findViewById(R.id.feed_item_text);
            text.setTypeface(TypefaceCache.get(activity, TypefaceCache.ROBOTO_SLAB_REGULAR));
            creationDate = (TextView)itemView.findViewById(R.id.feed_item_creation_date);

            itemView.setOnClickListener(this);
        }

        public void bind(final Note note) {
            this.note = note;

            final Context context = itemView.getContext();

            final boolean isLight = ColorHelper.isLight(note.getColor());
            // Set layout style.
            layout.setCardBackgroundColor(note.getColor());
            // Set title text and visibility.
            title.setText(note.getTitle());
            title.setTextColor(ColorHelper.getTextColor(isLight));
            title.setVisibility(!note.getTitle().isEmpty() ? View.VISIBLE : View.GONE);
            // Set text.
            text.setText(note.getText());
            text.setTextColor(ColorHelper.getTextColor(isLight));
            text.setTextSize(Float.valueOf(PreferenceHelper.get(context).getString(
                    context.getString(R.string.prefkey_font_size), "16")));
            // Set creation date text and style.
            creationDate.setTextColor(ColorHelper.getHintColor(isLight));
            creationDate.setText(getRelativeDateTimeString(context, note.getCustomDate()));
        }

        @Override
        public void onClick(final View view) {
            NoteActivity.startForResult(
                    activity,
                    ContentUris.withAppendedId(Note.Contract.CONTENT_URI, note.getId()),
                    0);
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
}
