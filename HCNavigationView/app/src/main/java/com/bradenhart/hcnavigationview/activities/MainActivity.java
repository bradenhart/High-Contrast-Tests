package com.bradenhart.hcnavigationview.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.R;

public class MainActivity extends BaseActivity {

    /*private final String KEY_SELECTED_ID = "selectedId";
    private final String FIRST_LOOK = "first_look";
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private MenuItem mMenuItem = null;
*/
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        /*title = (TextView) findViewById(R.id.test_snackbar);
        title.setClickable(true);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mDrawerLayout, "Test", Snackbar.LENGTH_LONG)
                        .setAction("Click me", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        });*/

    }

    /*@Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_ID, mSelectedId);
    }*/
}
