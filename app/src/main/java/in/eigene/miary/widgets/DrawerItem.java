package in.eigene.miary.widgets;

import android.view.*;
import android.widget.*;
import in.eigene.miary.*;

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
