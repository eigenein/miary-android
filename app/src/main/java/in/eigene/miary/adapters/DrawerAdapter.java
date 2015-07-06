package in.eigene.miary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.eigene.miary.Application;
import in.eigene.miary.R;
import in.eigene.miary.activities.AboutActivity;
import in.eigene.miary.activities.BaseActivity;
import in.eigene.miary.activities.FeedbackActivity;
import in.eigene.miary.activities.SettingsActivity;
import in.eigene.miary.helpers.ActivityHelper;
import in.eigene.miary.helpers.lang.Consumer;

/**
 * Used to display navigation drawer items.
 */
public class DrawerAdapter extends ArrayAdapter<Item> {

    private static final Item[] ITEMS = {
            new DividerItem(),
            new MarginItem(),
            new IconTitleItem(R.drawable.ic_inbox_grey600_24dp, R.string.drawer_item_diary, null),
            new IconTitleItem(R.drawable.ic_star_grey600_24dp, R.string.drawer_item_starred, null),
            new IconTitleItem(R.drawable.ic_drafts_grey600_24dp, R.string.drawer_item_drafts, null),
            new MarginItem(),
            new DividerItem(),
            new MarginItem(),
            new IconTitleItem(R.drawable.ic_settings_grey600_24dp, R.string.settings, SettingsActivity.class),
            new IconTitleItem(R.drawable.ic_help_grey600_24dp, R.string.activity_feedback, FeedbackActivity.class),
            new IconTitleItem(R.drawable.ic_info_grey600_24dp, R.string.activity_about, AboutActivity.class),
    };

    public DrawerAdapter(final Context context) {
        super(context, 0, ITEMS);
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(final int position) {
        return ITEMS[position].getViewType();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Item item = ITEMS[position];
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

    public void onClick(final Context context, final int position) {
        ITEMS[position].onClick(context);
    }
}

/**
 * Base navigation drawer item. Inflates the layout.
 */
abstract class Item {

    private final int viewType;
    private final int resourceId;

    public Item(final int viewType, final int resourceId) {
        this.viewType = viewType;
        this.resourceId = resourceId;
    }

    public int getViewType() {
        return viewType;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void onClick(final Context context) {
        // Do nothing.
    }

    public ViewHolder createViewHolder(final View convertView) {
        return new ViewHolder();
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
        super(0, R.layout.divider);
    }
}

/**
 * Navigation drawer margin. Used with dividers.
 */
class MarginItem extends Item {

    public MarginItem() {
        super(1, R.layout.drawer_margin);
    }
}

/**
 * Displays icon, title and (optionally) counter.
 */
class IconTitleItem extends Item {

    private final int iconResourceId;
    private final int titleResourceId;
    private final Consumer<Context> onClickConsumer;

    public IconTitleItem(final int iconResourceId, final int titleResourceId, final Class<? extends BaseActivity> activityClass) {
        this(iconResourceId, titleResourceId, new Consumer<Context>() {
            @Override
            public void accept(final Context context) {
                ActivityHelper.start(context, activityClass);
            }
        });
    }

    private IconTitleItem(final int iconResourceId, final int titleResourceId, final Consumer<Context> onClickConsumer) {
        super(2, R.layout.drawer_item);
        this.iconResourceId = iconResourceId;
        this.titleResourceId = titleResourceId;
        this.onClickConsumer = onClickConsumer;
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
    public void onClick(final Context context) {
        onClickConsumer.accept(context);
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
