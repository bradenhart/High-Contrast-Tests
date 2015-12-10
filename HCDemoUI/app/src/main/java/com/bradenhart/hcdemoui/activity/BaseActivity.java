package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradenhart on 5/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    public static boolean isLaunch;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private View headerView;
    private Integer activityId = null;
    private Map<Integer, Context> map = new HashMap<>();

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

        navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.new_challenge:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            startActivity(getActivityContext(id), ChallengeActivity.class);
                        }
                        break;
                    case R.id.challenges:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            Log.e("MAP", map.toString());
                            startActivity(getApplicationContext(), AllChallengesActivity.class);
                            //startActivity(getActivityContext(activityId), AllChallengesActivity.class);
                        }
                        break;
                    case R.id.settings:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            // start activity
                        }
                        break;
                    case R.id.help_feedback:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            // start activity
                        }
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START); // CLOSE DRAWER
                return true;
            }
        });

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

    @Override
    public void onBackPressed() {
        if (navDrawerIsOpen()) {
            closeNavDrawer();
            return;
        }

        if (activityId != null) {
            if (activityId == R.id.new_challenge) {
                // two back presses and exit
            } else {
                startActivity(this, ChallengeActivity.class);
            }
        }

    }

    public void startActivity(Context context, Class<?> theClass) {
        Intent intent = new Intent(context, theClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void mapContext(int id, Context context) {
        map.put(id, context);
    }

    private Context getActivityContext(int id) {
        return map.get(id);
    }

}
