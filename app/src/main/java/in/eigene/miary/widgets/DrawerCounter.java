package in.eigene.miary.widgets;

import android.view.View;
import android.widget.TextView;

import in.eigene.miary.R;

/**
 * Represents counter in the navigation drawer.
 */
public class DrawerCounter extends DrawerItem {

    private final TextView counterValueView;

    public DrawerCounter(
            final View drawerView,
            final int itemViewId,
            final int iconResourceId,
            final int titleResourceId,
            final View.OnClickListener listener
    ) {
        super(drawerView, itemViewId, iconResourceId, titleResourceId, listener);
        counterValueView = (TextView)itemView.findViewById(R.id.drawer_item_counter);
    }

    public void setValue(final int value) {
        counterValueView.setText(Integer.toString(value));
        counterValueView.setVisibility(value == 0 ? View.GONE : View.VISIBLE);
    }
}
