package com.bradenhart.hcnavigationview.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;
import com.bradenhart.hcnavigationview.fragments.*;
import static com.bradenhart.hcnavigationview.Constants.*;

import com.google.gson.Gson;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOGTAG = "BaseActivity";
    private Toolbar mToolbar;
    private NavigationView mNavDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId = -1;
    private boolean mUserSawDrawer = false;
    private MenuItem mMenuItem = null;
    private boolean madeChallenges = false;
    protected Map<String, String> challengesMap;

    protected FrameLayout frameLayout;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private View headerView;
    private CircleImageView profilePic;
    private TextView userNameTv;
    private String userName;
    private Menu navMenu;

    private final Gson gson  = new Gson();

    private FragmentManager manager;
    private Fragment fragment = null;
    private String currentScreen = null;

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        spEdit = sharedPreferences.edit();

        dbHandler = DatabaseHandler.getInstance(this);

        manager = getSupportFragmentManager();

        mNavDrawer = (NavigationView) findViewById(R.id.main_drawer);

        if (newChallengeRequested(getIntent().getExtras(), sharedPreferences)) {
            requestNewChallenge();
        } else {
            Intent setUpIntent = new Intent(BaseActivity.this, WelcomeActivity.class);
            startActivity(setUpIntent);
        }

        frameLayout = (FrameLayout) findViewById(R.id.main_content);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        setUpNavigationDrawer();

        if (!didUserSeeDrawer()) {
            showDrawer();
            hideDrawer(2000);
            markDrawerSeen();
        } else {
            hideDrawer(0);
        }

        //LayoutInflater mInflater =  (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        setUpHeaderView();



    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Clicked menu button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_help:
                break;
            case R.id.action_about:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void setUpNavigationDrawer() {
        mNavDrawer.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                updateUsername();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setUpHeaderView() {
        headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        profilePic = (CircleImageView) headerView.findViewById(R.id.header_profile_pic);
        showSavedProfilePicture();

        userNameTv = (TextView) headerView.findViewById(R.id.header_username_textview);
        userName = sharedPreferences.getString(KEY_USER_NAME, defaultName);
        userNameTv.setText(userName);
        mNavDrawer.addHeaderView(headerView);
    }

    private boolean didUserSeeDrawer() {
        mUserSawDrawer = sharedPreferences.getBoolean(KEY_FIRST_LOOK, false);
        return mUserSawDrawer;
    }

    private void markDrawerSeen() {
        mUserSawDrawer = true;
        spEdit.putBoolean(KEY_FIRST_LOOK, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer(long delay) {
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }, delay);
    }

    private void requestNewChallenge() {
        mMenuItem = mNavDrawer.getMenu().findItem(R.id.new_challenge);
        fragment = new NewChallengeFragment();
        manager.beginTransaction().replace(R.id.main_content, fragment).commit();
        currentScreen = screenNewChallenge;
    }

    private boolean newChallengeRequested(Bundle bundle, SharedPreferences sp) {
        if (bundle != null) {
            String request = bundle.getString(KEY_REQUEST_ACTION);
            if (request != null && request.equals(showNewChallenge)) return true;
        }
        if (sharedPreferences.contains(KEY_SETUP_STAGE)) {
            String stage = sharedPreferences.getString(KEY_SETUP_STAGE, setUpDefault);
            if (stage.equals(stageCompleted)) return true;
        }
        return false;
    }

    private void uncheckAllMenuItems() {
        navMenu = mNavDrawer.getMenu();
        int size = navMenu.size();
        for (int i = 0; i < size; i++) {
            if (navMenu.getItem(i).isChecked()) {
                navMenu.getItem(i).setChecked(false);
                Log.e("UNCHECKED", "unchecked item " + i);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        //if (mMenuItem != null) uncheckAllMenuItems();//mMenuItem.setChecked(false);
        if (mMenuItem.getItemId() == R.id.new_challenge && menuItem.getItemId() == R.id.new_challenge) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        mMenuItem = menuItem;
        mMenuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();
        spEdit.putInt(KEY_SELECTED_ID, mSelectedId).apply();
        navigate(mSelectedId);
        return true;
    }

    public void navigate(int mSelectedId) {
        Intent intent;

//        if (mSelectedId == -1) {
//            return;
//        }

        switch (mSelectedId) {
            case R.id.profile:
                Log.e(LOGTAG, "profile");
                fragment = new ProfileFragment();
                currentScreen = screenProfile;
                break;
            case R.id.new_challenge:
                Log.e(LOGTAG, "new challenge");
                if (currentScreen.equals(screenNewChallenge)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    Toast.makeText(this, "should close drawer", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestNewChallenge();
                    Toast.makeText(this, "something else", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "third time", Toast.LENGTH_SHORT).show();
                break;
            case R.id.challenges:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                manager.beginTransaction().remove(fragment).commit();
                currentScreen = screenChallenges;
                intent = new Intent(BaseActivity.this, ChallengesActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.completed:
                fragment = new CompletedFragment();
                currentScreen = screenCompleted;
                break;
            case R.id.achievements:
                fragment = new AchievementsFragment();
                currentScreen = screenAchievements;
                break;
            case R.id.settings:
                fragment = new AppSettingsFragment();
                currentScreen = screenSettings;
                break;
            case R.id.help_feedback:
                fragment = new HelpFeedbackFragment();
                currentScreen = screenHelpFeedback;
                break;
            default:
                break;
        }

        if (fragment != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            manager.beginTransaction().replace(R.id.main_content, fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_ID, mSelectedId);
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            if (mSelectedId != R.id.new_challenge) requestNewChallenge();
        }
    }

    private void showSavedProfilePicture() {
        byte[] array = dbHandler.retrieveByteArrayFromDb();
        if (array == null) {
            profilePic.setImageResource(defaultPic);
        } else {
            Bitmap bitmap = convertByteArrayToBitmap(array);
            if (bitmap == null) {
                profilePic.setImageResource(defaultPic);
            } else {
                profilePic.setImageBitmap(bitmap);
                bitmap.recycle();
            }
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    private void updateUsername() {
        String currentName = userNameTv.getText().toString();
        String storedName = sharedPreferences.getString(KEY_USER_NAME, defaultName);

        if (!storedName.equals(currentName)) userNameTv.setText(storedName);
    }

}
