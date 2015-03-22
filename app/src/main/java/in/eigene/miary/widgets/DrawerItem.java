package in.eigene.miary.widgets;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.eigene.miary.R;

public class DrawerItem {

    protected final View itemView;

    public DrawerItem(
            final View drawerView,
            final int itemViewId,
            final int iconResourceId,
            final int titleResourceId,
            final View.OnClickListener listener
    ) {
        itemView = drawerView.findViewById(itemViewId);
        ((TextView)itemView.findViewById(R.id.drawer_item_title)).setText(titleResourceId);
        ((ImageView)itemView.findViewById(R.id.drawer_item_icon)).setImageResource(iconResourceId);
        itemView.setOnClickListener(listener);
    }
}
