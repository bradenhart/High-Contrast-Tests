package com.bradenhart.hcdemoui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 5/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.base_toolbar);
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

//        initDrawerToggle();
        initNavigationView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // THIS IS YOUR DRAWER/HAMBURGER BUTTON
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationView() {

        navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    ////.......

                }
                drawerLayout.closeDrawer(GravityCompat.START); // CLOSE DRAWER
                return true;
            }
        });

    }

    private void initDrawerToggle() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.base_drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                // TODO Auto-generated method stub
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//
//            /** Called when a drawer has settled in a completely closed state. */
//            @Override
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
////                getSupportActionBar().setTitle("hello");
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
////                getSupportActionBar().setTitle("hi");
//            }

//        };
//
//        drawerLayout.setDrawerListener(drawerToggle);
//        drawerToggle.syncState();
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }

    protected String setToolbarTitle() {
        return getResources().getString(R.string.app_title);
    }

    protected boolean useToolbar() {
        return true;
    }

}
