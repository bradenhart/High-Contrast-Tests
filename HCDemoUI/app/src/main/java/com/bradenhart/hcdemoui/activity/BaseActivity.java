package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.Utils;
import com.melnykov.fab.FloatingActionButton;


/**
 * Created by bradenhart on 5/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private FloatingActionButton fab;
    private FrameLayout container;
    private View headerView;
    private Integer activityId = null;

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        container = (FrameLayout) drawerLayout.findViewById(R.id.base_container);
        getLayoutInflater().inflate(layoutResID, container, true);
        super.setContentView(drawerLayout);

        // set up toolbar
        initToolbar();

        // set up header bar
        initHeaderBar();

        // set up floating action bar
        initFab();

        // set up the navigationview
        initNavigationView();

        // handle scrolling behavior
        if (useScrollingBehavior()) {
            toggleScrollBehavior(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (useOptionsMenu()) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_base, menu);
            MenuItem update = menu.findItem(R.id.action_update);
            if (useUpdateButton()) {
                update.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            } else {
                update.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        return useOptionsMenu();
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

    protected boolean useOptionsMenu() {
        return true;
    }

    /**
     * Initialise the Toolbar.
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.base_toolbar);

        if (toolbar != null) {
            if (useToolbar()) {
                // replace the default actionbar with our toolbar
                setSupportActionBar(toolbar);
                // disable the title that would appear in the actionbar
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                // show the desired title in the toolbar instead of the actionbar
                toolbar.setTitle(setToolbarTitle());
                // will show the back arrow/caret and make it clickable. will not return home unless parent activity is specified
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                // shows logo/icon with caret/arrow if passed true. will not show logo/icon if passed false
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                // set the navigation drawer icon to the hamburger icon
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            } else {
                // hide the toolbar as it isn't being used
                toolbar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Initialise the header bar (textview below the toolbar).
     */
    private void initHeaderBar() {
        TextView headerBar = (TextView) findViewById(R.id.base_header_bar);

        if (headerBar != null) {
            if (!useHeaderBar()) {
                // not using header bar so hide it
                headerBar.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Initialise the Floating Action Bar.
     */
    private void initFab() {
         fab = (FloatingActionButton) findViewById(R.id.base_fab);

        if (fab != null) {
            if (useFab()) {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                // get position/gravity
                lp.gravity = setFabGravity();
                // set color
                fab.setColorNormalResId(setFabColor());
                // set icon/src
                fab.setImageResource(setFabIcon());
                // set proper margins
                int[] margins = setFabMargins();
                lp.setMargins(margins[0], margins[1], margins[2], margins[3]);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    public void toggleScrollBehavior(boolean on) {
        CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) container.getLayoutParams();
        if (on) {
            clp.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        } else {
            clp.setBehavior(null);
        }
        container.requestLayout();
    }

    /**
     * Initialise the NavigationView.
     */
    private void initNavigationView() {

        navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_new_challenge:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            startActivity(ChallengeActivity.class);
                        }
                        break;
                    case R.id.nav_challenges:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            startActivity(AllChallengesActivity.class);
                        }
                        break;
                    case R.id.nav_settings:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            startActivity(GameSettingsActivity.class);
                        }
                        break;
                    case R.id.nav_help_feedback:
                        if (!menuItem.isChecked()) {
                            activityId = id;
                            startActivity(HelpFeedbackActivity.class);
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

    /**
     * Checks whether the navigation drawer is open.
     * Returns true if the drawerLayout object is not null (has been initialised),
     * AND if the drawerLayout is open from the left of the screen.
     *
     * @return boolean is the navigation drawer open - true/false
     */
    private boolean navDrawerIsOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Closes the navigation drawer back to the left of the screen.
     */
    private void closeNavDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Returns the toolbar title.
     * Defaults to the app's title.
     *
     * @return String the title the toolbar will display
     */
    protected String setToolbarTitle() {
        return getResources().getString(R.string.app_title);
    }

    /**
     * Determines whether the toolbar will be used.
     * Returns true by default as most screens will use the base toolbar.
     * Some may not show a toolbar and some may need a custom version.
     *
     * @return boolean toolbar is used - true/false
     */
    protected boolean useToolbar() {
        return true;
    }

    /**
     * Determines whether the header bar (a textview below the toolbar) will be used.
     *
     * @return boolean the header bar is used - true/false
     */
    protected boolean useHeaderBar() {
        return false;
    }

    /**
     * Determines whether the floating action button from the base activity will
     * be used.
     *
     * @return boolean the floating action button is used - true/false
     */
    protected boolean useFab() {
        return false;
    }

    /**
     * Returns the value(s) of gravity that each activity prefers to assign to
     * the floating action button IF the floating action button is used.
     *
     * @return int the gravity value for the floating action button
     */
    protected int setFabGravity() {
        return Gravity.BOTTOM | Gravity.END | Gravity.RIGHT;
    }


    /**
     * Returns the id for the color resource that will be used to set the
     * floating action button's color.
     *
     * @return int the color resource id for the floating action button color
     */
    protected int setFabColor() {
        return R.color.colorAccent;
    }

    /**
     * Returns the id for the drawable resource that will be used to set the
     * floating action button's icon.
     *
     * @return int the drawable resource id for the floating action button icon
     */
    protected int setFabIcon() {
        return R.drawable.blank_fab_bg;
    }

    /**
     * Returns an array of int that holds the four margin values (left, top, right, bottom)
     * for the floating action button.
     * This allows each child activity to change where the floating action button will be
     * on the screen if they choose to use it.
     * Would work in conjunction with setFabGravity() method to place the button on the screen.
     *
     * @return int[] the array of margin values
     */
    protected int[] setFabMargins() {
        return new int[] {16, 16, 16, 16};
    }

    private int[] convertDpArrayToPixel(int[] array) {
        int s = array.length;
        int[] newArray = new int[s];
        int i = 0;
        for (i = 0; i < s; i++) {
            newArray[i] = Utils.getPixelsFromDP(array[i], getApplicationContext());
        }
        return newArray;
    }

    /**
     * Determines whether a view will have a scrolling behavior assigned
     * to it.
     * If true, the current scrolling behavior will cause the AppBarLayout
     * or Toolbar to scroll up off the screen when the view is scrolled down,
     * and will return when the view is scrolled back up.
     * If false, no scrolling behavior will be assigned.
     *
     * @return boolean scrolling behavior is used - true/false
     */
    protected boolean useScrollingBehavior() {
        return false;
    }

    protected boolean useUpdateButton() {
        return false;
    }

    /**
     * When a child class calls this method, the menu item in the NavigationView
     * with the given id will be checked. This method is called inside the onCreate
     * method of a child class to ensure that it's menu item is checked when it is
     * the current activity/fragment on the screen.
     *
     * @param id the id for the menu item in menu_drawer.xml
     */
    public void updateCheckedDrawerItem(int id) {
        Menu menu = navView.getMenu();
        MenuItem item = menu.findItem(id);
        item.setChecked(true);
        activityId = id;
    }

    /**
     * Starts the activity specified by the Class type parameter.
     *
     * @param theClass the class that is being started
     */
    public void startActivity(Class<?> theClass) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, theClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (navDrawerIsOpen()) {
            closeNavDrawer();
            return;
        }

        if (activityId != null) {
            if (activityId == R.id.nav_new_challenge) {
                // two back presses and exit
            } else {
                startActivity(ChallengeActivity.class);
            }
        }

    }

}
