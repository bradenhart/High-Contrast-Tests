package com.bradenhart.hcnavigationview.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bradenhart.hcnavigationview.Constants;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.fragments.SettingsFragment;
import com.bradenhart.hcnavigationview.fragments.SocialFragment;
import com.bradenhart.hcnavigationview.fragments.WelcomeFragment;
import com.github.mrengineer13.snackbar.SnackBar;

import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 30/06/15.
 */
public class WelcomeActivity extends AppCompatActivity {

    private final String LOGTAG = "Welcome Activity";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;

    private DrawerLayout rootLayout;
    private FrameLayout frameLayout;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment fragment;
    private Toolbar titleBar;
    private int accentColor, whiteColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        spEdit = sharedPreferences.edit();

        accentColor = getResources().getColor(R.color.accent);

        rootLayout = (DrawerLayout) findViewById(R.id.welcome_screen_root);
        rootLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        frameLayout = (FrameLayout) findViewById(R.id.welcome_container);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        titleBar = (Toolbar) findViewById(R.id.title_bar);
//        titleBar.setTitle("Profile Setup");

        fragment = new WelcomeFragment();
        manager.beginTransaction().replace(R.id.welcome_container, fragment).commit();

    }

    @Override
    public void onBackPressed() {

        // may need to fill in relevant info when a fragment is shown on back navigation
//        Snackbar.make(frameLayout, "Cancel setup and quit?", Snackbar.LENGTH_LONG)
//                .setAction("Confirm", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        spEdit.remove(KEY_USER_NAME);
//                        Intent goHome = new Intent(Intent.ACTION_MAIN);
//                        goHome.addCategory(Intent.CATEGORY_HOME);
//                        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(goHome);
//                    }
//                }).show();

        new SnackBar.Builder(this)
                .withOnClickListener(new SnackBar.OnMessageClickListener() {
                    @Override
                    public void onMessageClick(Parcelable parcelable) {
                        spEdit.remove(KEY_USER_NAME);
                        Intent goHome = new Intent(Intent.ACTION_MAIN);
                        goHome.addCategory(Intent.CATEGORY_HOME);
                        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goHome);
                    }
                })
                .withMessage("Cancel setup and quit the app?")
                .withActionMessage("Confirm")
                .withTextColorId(R.color.accent)
                //.withTextColorId(getResources().getColor(R.color.white))
                .withBackgroundColorId(R.color.com_facebook_blue)
                .show();

//      super.onBackPressed();
    }

}
