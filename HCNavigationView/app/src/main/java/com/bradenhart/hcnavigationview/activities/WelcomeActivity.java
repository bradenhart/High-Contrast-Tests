package com.bradenhart.hcnavigationview.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bradenhart.hcnavigationview.Constants;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.fragments.SettingsFragment;
import com.bradenhart.hcnavigationview.fragments.SocialFragment;
import com.bradenhart.hcnavigationview.fragments.WelcomeFragment;
import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 30/06/15.
 */
public class WelcomeActivity extends AppCompatActivity {

    private final String LOGTAG = "Welcome Activity";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;

    private FrameLayout frameLayout;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        spEdit = sharedPreferences.edit();

        frameLayout = (FrameLayout) findViewById(R.id.welcome_container);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        if (!sharedPreferences.contains(KEY_SETUP_STAGE)) {
            fragment = new WelcomeFragment();
            spEdit.putString(KEY_SETUP_STAGE, stageWelcome).apply();
            transaction.add(R.id.welcome_container, fragment).commit();
            Log.e(LOGTAG, "sp doesn't contain KEY_SETUP_STAGE, starting welcome fragment");
        } else {
            showRelevantSetUpFragment();
        }

    }

    private void showRelevantSetUpFragment() {
        String stage = sharedPreferences.getString(KEY_SETUP_STAGE, setUpDefault);

        switch (stage) {
            case stageWelcome:
                fragment = new WelcomeFragment();
                manager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                Log.e(LOGTAG, "sp contains KEY_SETUP_STAGE, starting welcome fragment");
                break;
            case stageSocial:
                fragment = new SocialFragment();
                manager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                Log.e(LOGTAG, "sp contains KEY_SETUP_STAGE, starting social fragment");
                break;
            case stageSettings:
                fragment = new SettingsFragment();
                manager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                Log.e(LOGTAG, "sp contains KEY_SETUP_STAGE, starting settings fragment");
                break;
            case stageCompleted:
                Intent intent = new Intent(this, BaseActivity.class);
                startActivity(intent);
                Log.e(LOGTAG, "sp contains KEY_SETUP_STAGE, completed, starting base activity");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        String currentStage = sharedPreferences.getString(KEY_SETUP_STAGE, setUpDefault);

        // may need to fill in relevant info when a fragment is shown on back navigation

        switch (currentStage) {
            case stageWelcome:
                Snackbar.make(frameLayout, "Cancel setup and quit?", Snackbar.LENGTH_LONG)
                        .setAction("Confirm", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                spEdit.putString(KEY_SETUP_STAGE, stageWelcome);
                                spEdit.remove(KEY_USER_NAME);
                                finish();
                            }
                        }).show();
                break;
            case stageSocial:
                fragment = new SocialFragment();
                transaction.replace(R.id.welcome_container, fragment).commit();
                break;
            default:
                break;
        }

//        super.onBackPressed();
    }
}
