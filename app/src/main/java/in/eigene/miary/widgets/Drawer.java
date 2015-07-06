package in.eigene.miary.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import in.eigene.miary.R;
import in.eigene.miary.adapters.DrawerAdapter;

public class Drawer {

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private final Activity activity;

    private final DrawerLayout layout;
    private final ActionBarDrawerToggle toggle;
    private final ListView view;

    public Drawer(final Activity activity, final Toolbar toolbar, final DrawerAdapter adapter) {
        this.activity = activity;

        // Initialize drawer itself.
        view = (ListView)activity.findViewById(R.id.drawer);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new ActionBarDrawerToggle(activity, layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(final View view) {
                adapter.triggerUpdateData();
            }
        };
        layout.setDrawerListener(toggle);

        // Initialize navigation list view.
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.onClick(position);
                    }
                }, 500L);
                layout.closeDrawer(Drawer.this.view);
            }
        });
        view.setAdapter(adapter);
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    public Context getContext() {
        return view.getContext();
    }

    /**
     * Shows drawer if it was not shown since application installed.
     */
    public void showForFirstTime() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (!preferences.getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.openDrawer(GravityCompat.START);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).apply();
                }
            }, 1000);
        }
    }
}
