package in.eigene.miary.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import in.eigene.miary.R;
import in.eigene.miary.activities.AboutActivity;
import in.eigene.miary.activities.FeedbackActivity;
import in.eigene.miary.activities.SettingsActivity;
import in.eigene.miary.helpers.ActivityHelper;
import in.eigene.miary.persistence.Note;

/**
 * Used to display navigation drawer items.
 */
public class DrawerAdapter extends ArrayAdapter<Item> {

    private static final SparseIntArray RESOURCE_ID_VIEW_TYPE = new SparseIntArray();

    static {
        RESOURCE_ID_VIEW_TYPE.append(R.layout.divider, 0);
        RESOURCE_ID_VIEW_TYPE.append(R.layout.drawer_margin_item, 1);
        RESOURCE_ID_VIEW_TYPE.append(R.layout.drawer_counter_item, 2);
    }

    /**
     * Initializes drawer items.
     */
    public DrawerAdapter(final Activity activity, final SectionClickListener sectionClickListener) {
        super(activity, 0, new Item[]{
                new DividerItem(),
                new MarginItem(),
                new CounterItem(R.drawable.ic_inbox_grey600_24dp, R.string.drawer_item_diary, new Runnable() {
                    @Override
                    public void run() {
                        sectionClickListener.onSectionClick(Note.Section.DIARY);
                    }
                }),
                new CounterItem(R.drawable.ic_star_grey600_24dp, R.string.drawer_item_starred, new Runnable() {
                    @Override
                    public void run() {
                        sectionClickListener.onSectionClick(Note.Section.STARRED);
                    }
                }),
                new CounterItem(R.drawable.ic_drafts_grey600_24dp, R.string.drawer_item_drafts, new Runnable() {
                    @Override
                    public void run() {
                        sectionClickListener.onSectionClick(Note.Section.DRAFTS);
                    }
                }),
                new MarginItem(),
                new DividerItem(),
                new MarginItem(),
                new CounterItem(R.drawable.ic_settings_grey600_24dp, R.string.settings, new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, SettingsActivity.class);
                    }
                }),
                new CounterItem(R.drawable.ic_help_grey600_24dp, R.string.activity_feedback, new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, FeedbackActivity.class);
                    }
                }),
                new CounterItem(R.drawable.ic_info_grey600_24dp, R.string.activity_about, new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, AboutActivity.class);
                    }
                }),
        });
    }

    @Override
    public int getViewTypeCount() {
        return RESOURCE_ID_VIEW_TYPE.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return RESOURCE_ID_VIEW_TYPE.get(getItem(position).getResourceId());
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Item item = getItem(position);
        final Item.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(item.getResourceId(), parent, false);
            viewHolder = item.createViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Item.ViewHolder)convertView.getTag();
        }
        item.bind(viewHolder);
        return convertView;
    }

    /**
     * Updates navigation drawer data.
     */
    public void triggerUpdateData() {
        new UpdateDataAsyncTask().execute();
    }

    public void onClick(final int position) {
        getItem(position).onClick();
    }

    public interface SectionClickListener {

        void onSectionClick(final Note.Section section);
    }

    /**
     * Drawer items data.
     */
    private class Data {

        public final HashMap<Note.Section, Integer> sectionCounters = new HashMap<>();
    }

    /**
     * Updates drawer items data.
     */
    private class UpdateDataAsyncTask extends AsyncTask<Void, Void, Data> {

        private final String[] projection = { "COUNT(*)" };

        @Override
        protected Data doInBackground(final Void... params) {
            final ContentResolver contentResolver = getContext().getContentResolver();
            final Data data = new Data();
            for (final Note.Section section : Note.Section.values()) {
                final Cursor cursor = contentResolver.query(
                        Note.Contract.CONTENT_URI, projection, section.getSelection(), null, null);
                cursor.moveToFirst();
                data.sectionCounters.put(section, cursor.getInt(0));
                cursor.close();
            }
            return data;
        }

        @Override
        protected void onPostExecute(final Data data) {
            ((CounterItem)getItem(2)).setCounterValue(data.sectionCounters.get(Note.Section.DIARY));
            ((CounterItem)getItem(3)).setCounterValue(data.sectionCounters.get(Note.Section.STARRED));
            ((CounterItem)getItem(4)).setCounterValue(data.sectionCounters.get(Note.Section.DRAFTS));
            notifyDataSetChanged();
        }
    }
}

/**
 * Base navigation drawer item. Inflates the layout.
 */
abstract class Item {

    private static final ViewHolder VIEW_HOLDER = new ViewHolder();

    private final int resourceId;

    public Item(final int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void onClick() {
        // Do nothing.
    }

    public ViewHolder createViewHolder(final View convertView) {
        return VIEW_HOLDER;
    }

    public void bind(final ViewHolder viewHolder) {
        // Do nothing.
    }

    protected static class ViewHolder {
        // Nothing.
    }
}

/**
 * Navigation drawer section divider.
 */
class DividerItem extends Item {

    public DividerItem() {
        super(R.layout.divider);
    }
}

/**
 * Navigation drawer margin. Used with dividers.
 */
class MarginItem extends Item {

    public MarginItem() {
        super(R.layout.drawer_margin_item);
    }
}

/**
 * Displays icon, title and (optionally) counter.
 */
class CounterItem extends Item {

    private final int iconResourceId;
    private final int titleResourceId;
    private final Runnable runnable;
    private int counterValue = 0;

    public CounterItem(
            final int iconResourceId,
            final int titleResourceId,
            final Runnable runnable) {
        super(R.layout.drawer_counter_item);
        this.iconResourceId = iconResourceId;
        this.titleResourceId = titleResourceId;
        this.runnable = runnable;
    }

    @Override
    public Item.ViewHolder createViewHolder(final View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    public void bind(final Item.ViewHolder viewHolder) {
        final ViewHolder simpleItemViewHolder = (ViewHolder)viewHolder;
        simpleItemViewHolder.icon.setImageResource(iconResourceId);
        simpleItemViewHolder.title.setText(titleResourceId);
        simpleItemViewHolder.counter.setVisibility(counterValue != 0 ? View.VISIBLE : View.GONE);
        simpleItemViewHolder.counter.setText(Integer.toString(counterValue));
    }

    @Override
    public void onClick() {
        runnable.run();
    }

    public void setCounterValue(final int counterValue) {
        this.counterValue = counterValue;
    }

    private static class ViewHolder extends Item.ViewHolder {

        public final ImageView icon;
        public final TextView title;
        public final TextView counter;

        public ViewHolder(final View convertView) {
            icon = (ImageView)convertView.findViewById(R.id.drawer_item_icon);
            title = (TextView)convertView.findViewById(R.id.drawer_item_title);
            counter = (TextView)convertView.findViewById(R.id.drawer_item_counter);
        }
    }
}
