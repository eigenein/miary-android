package in.eigene.miary.widgets;

import android.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;

public class DrawerToggle extends ActionBarDrawerToggle {

    private DrawerLayout.DrawerListener successor;

    public DrawerToggle(
            final Activity activity,
            final DrawerLayout drawerLayout,
            final Toolbar toolbar,
            final int openDrawerContentDescRes,
            final int closeDrawerContentDescRes,
            final DrawerLayout.DrawerListener successor) {
        super(
                activity,
                drawerLayout,
                toolbar,
                openDrawerContentDescRes,
                closeDrawerContentDescRes
        );
        this.successor = successor;
    }

    @Override
    public void onDrawerClosed(final View drawerView) {
        super.onDrawerClosed(drawerView);
        if (successor != null) {
            successor.onDrawerClosed(drawerView);
        }
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        super.onDrawerOpened(drawerView);
        if (successor != null) {
            successor.onDrawerOpened(drawerView);
        }
    }

    @Override
    public void onDrawerSlide(final View drawerView, final float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if (successor != null) {
            successor.onDrawerSlide(drawerView, slideOffset);
        }
    }

    @Override
    public void onDrawerStateChanged(final int newState) {
        super.onDrawerStateChanged(newState);
        if (successor != null) {
            successor.onDrawerStateChanged(newState);
        }
    }
}