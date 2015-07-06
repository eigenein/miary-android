package in.eigene.miary.adapters;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.eigene.miary.R;
import in.eigene.miary.activities.AboutActivity;
import in.eigene.miary.activities.BaseActivity;
import in.eigene.miary.activities.FeedbackActivity;
import in.eigene.miary.activities.SettingsActivity;
import in.eigene.miary.helpers.ActivityHelper;
import in.eigene.miary.helpers.lang.Consumer;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.widgets.Drawer;

/**
 * Used to display navigation drawer items.
 */
public class DrawerAdapter extends ArrayAdapter<Item> {

    private static final SparseIntArray RESOURCE_ID_VIEW_TYPE = new SparseIntArray();

    /**
     * Initializes resource ID to view type mapping.
     */
    static {
        RESOURCE_ID_VIEW_TYPE.append(R.layout.divider, 0);
        RESOURCE_ID_VIEW_TYPE.append(R.layout.drawer_margin, 1);
        RESOURCE_ID_VIEW_TYPE.append(R.layout.drawer_counter_item, 2);
    }

    /**
     * Initializes drawer items.
     */
    public DrawerAdapter(final Context context, final SectionClickListener sectionClickListener) {
        super(context, 0, new Item[] {
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
                        ActivityHelper.start(context, SettingsActivity.class);
                    }
                }),
                new CounterItem(R.drawable.ic_help_grey600_24dp, R.string.activity_feedback, new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(context, FeedbackActivity.class);
                    }
                }),
                new CounterItem(R.drawable.ic_info_grey600_24dp, R.string.activity_about, new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(context, AboutActivity.class);
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

    public void onClick(final int position) {
        getItem(position).onClick();
    }

    /**
     * Chooses diary section.
     */
    public interface SectionClickListener {

        void onSectionClick(final Note.Section section);
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
        super(R.layout.drawer_margin);
    }
}

/**
 * Displays icon, title and (optionally) counter.
 */
class CounterItem extends Item {

    private final int iconResourceId;
    private final int titleResourceId;
    private final Runnable runnable;

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
    }

    @Override
    public void onClick() {
        runnable.run();
    }

    private static class ViewHolder extends Item.ViewHolder {

        public final ImageView icon;
        public final TextView title;

        public ViewHolder(final View convertView) {
            icon = (ImageView)convertView.findViewById(R.id.drawer_item_icon);
            title = (TextView)convertView.findViewById(R.id.drawer_item_title);
        }
    }
}
