package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradenhart on 12/12/15.
 */
public class TestBaseActivity extends AppCompatActivity {

    public static boolean isLaunch;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private View headerView;
    private Integer activityId = null;
    private Map<Integer, Context> map = new HashMap<>();

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_test_base, null);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.test_container);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.test_toolbar);
        if (useToolbar()) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(setToolbarTitle());
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            //
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        } else {
            toolbar.setVisibility(View.GONE);
        }

        TextView headerBar = (TextView) findViewById(R.id.test_header_tab);

        if (!useHeaderBar()) {
            headerBar.setVisibility(View.GONE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.test_fab);

        if (useFab()) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            // get position/gravity
            lp.gravity = setFabGravity();
            // set color
            fab.setColorNormalResId(setFabColor());
            // set icon/src
            fab.setImageResource(R.drawable.ic_filter_list_white_24dp);
        } else {
            fab.setVisibility(View.GONE);
        }

        initNavigationView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationView() {

        navView = (NavigationView) findViewById(R.id.test_navigation_view);

        headerView = navView.getHeaderView(0);
        headerView.setClickable(true);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navDrawerIsOpen()) {
                    closeNavDrawer();
                }
            }
        });

    }

    private boolean navDrawerIsOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void closeNavDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    protected String setToolbarTitle() {
        return getResources().getString(R.string.app_title);
    }

    protected boolean useToolbar() {
        return true;
    }

    protected boolean useHeaderBar() {
        return false;
    }

    protected boolean useFab() {
        return false;
    }

    protected int setFabGravity() {
        return Gravity.BOTTOM | Gravity.END | Gravity.RIGHT;
    }

    protected int setFabColor() {
        return R.color.colorAccent;
//        return R.color.colorAccent;
    }

    protected int setFabIcon() {
        return R.drawable.blank_fab_bg;
    }

    @Override
    public void onBackPressed() {

    }

    public void startActivity(Context context, Class<?> theClass) {
        Intent intent = new Intent(context, theClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
