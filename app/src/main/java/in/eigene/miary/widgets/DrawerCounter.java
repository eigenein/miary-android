package in.eigene.miary.widgets;

import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.helpers.lang.*;

/**
 * Represents counter in the navigation drawer.
 */
public class DrawerCounter<Integer> extends DrawerItem implements Consumer<Integer> {

    final private Function<Consumer<Integer>, Integer> valueFunction;
    final private TextView counterValueView;

    public DrawerCounter(
            final View drawerView,
            final int itemViewId,
            final int iconResourceId,
            final int titleResourceId,
            final View.OnClickListener listener,
            final Function<Consumer<Integer>, Integer> valueFunction
    ) {
        super(drawerView, itemViewId, iconResourceId, titleResourceId, listener);

        this.valueFunction = valueFunction;
        counterValueView = (TextView)itemView.findViewById(R.id.drawer_item_counter);
    }

    @Override
    public void accept(final Integer value) {
        counterValueView.setText(value.toString());
        counterValueView.setVisibility(value.equals(0) ? View.GONE : View.VISIBLE);
    }

    /**
     * Refreshes counter.
     */
    public void refresh() {
        accept(valueFunction.apply(this));
    }
}
