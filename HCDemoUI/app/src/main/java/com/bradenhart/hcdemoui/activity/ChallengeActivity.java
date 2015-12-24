package com.bradenhart.hcdemoui.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.bradenhart.hcdemoui.Utils.*;

import com.bradenhart.hcdemoui.database.Challenge;
import com.bradenhart.hcdemoui.database.DatabaseHelper;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import com.bradenhart.hcdemoui.R;

import java.util.Date;
import java.util.List;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener {

    private final String LOGTAG = "ChallengeActivity";

    private FrameLayout root;
    private TextView randomChallengeBtn, skipChallengeBtn, allChallengeBtn, challengeSettingsBtn;
    private RelativeLayout popupMenu;

    private TextView challengeTitle, challengeText, challengeDifficulty, challengeMin, challengeMax;
    private ImageView diffDown, diffUp, groupDown, groupUp;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    private final String KEY_POPUP_VISIBILITY = "popup_visibility";
    private final String KEY_HAD_FIRST_USE = "first_use";
    private String stateDifficulty;
    private Integer stateGroupSize;
    private String currentObjectId;
    private DatabaseHelper dbHelper;
    private Animation slideDownAnim, slideUpAnim, spinAnim;
    private RelativeLayout loadingLayout;
    private View screenBlock;
    private ImageView loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // initialise all views here
        initViews();
        // set onClickListener for all views here
        setClickListenerOnViews();
        // initialise animations from anim folder
        initAnimations();

        sp = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        spEdit = sp.edit();

        dbHelper = DatabaseHelper.getInstance(this);

        updateDifficultyState(null);
        updateGroupSizeState(null);

        initiateFirstDataDownload();

        updateCheckedDrawerItem(R.id.nav_new_challenge);

        // restore activity from savedInstanceState
        restoreFromSavedInstanceState(savedInstanceState);

    }

    /** initialisations */
    private void initViews() {
        // root layout (the container this activity is loaded into)
        root = (FrameLayout) findViewById(R.id.base_container);
        // pop up menu for the game settings (difficulty and group size)
        popupMenu = (RelativeLayout) findViewById(R.id.game_settings_popup_menu);
        // the title for the challenge being displayed
        challengeTitle = (TextView) findViewById(R.id.challenge_title);
        // the description/text for the challenge being displayed
        challengeText = (TextView) findViewById(R.id.challenge_text);
        // button to randomise/shuffle the challenges
        randomChallengeBtn = (TextView) findViewById(R.id.random_challenge_btn);
        // button to skip to the next challenge in the list
        skipChallengeBtn = (TextView) findViewById(R.id.skip_challenge_btn);
        // button to display all of the challenges in a list
        allChallengeBtn = (TextView) findViewById(R.id.all_challenges_button);
        // button to open the popup menu for altering the current challenge settings
        challengeSettingsBtn = (TextView) findViewById(R.id.challenge_settings_button);
        // button to decrease the difficulty of challenges
        diffDown = (ImageView) findViewById(R.id.difficulty_down);
        // button to increase the difficulty of challenges
        diffUp = (ImageView) findViewById(R.id.difficulty_up);
        // button to decrease the group size for challenges
        groupDown = (ImageView) findViewById(R.id.group_down);
        // button to increase the group size for challenges
        groupUp = (ImageView) findViewById(R.id.group_up);
        // layout that contains a spinning loading icon and a message for the user
        loadingLayout = (RelativeLayout) findViewById(R.id.challenge_download_layout);
        // the loading image that will spin while data is being downloaded
        loadingIcon = (ImageView) findViewById(R.id.download_icon);
        // a view that will block any buttons that need to be disabled while data is being downloaded
        screenBlock = findViewById(R.id.download_screen_block);
        // challenge difficulty
        challengeDifficulty = (TextView) findViewById(R.id.challenge_difficulty_textview);
        // challenge min
        challengeMin = (TextView) findViewById(R.id.challenge_min);
        // challenge max
        challengeMax = (TextView) findViewById(R.id.challenge_max);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.base_fab);
        fab.setOnClickListener(this);
    }

    private void setClickListenerOnViews() {
        root.setOnClickListener(this);
        challengeTitle.setOnClickListener(this);
        challengeText.setOnClickListener(this);
        randomChallengeBtn.setOnClickListener(this);
        skipChallengeBtn.setOnClickListener(this);
        allChallengeBtn.setOnClickListener(this);
        challengeSettingsBtn.setOnClickListener(this);
        diffDown.setOnClickListener(this);
        diffUp.setOnClickListener(this);
        groupDown.setOnClickListener(this);
        groupUp.setOnClickListener(this);
    }

    private void initAnimations() {
        slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down);
        slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up);
        spinAnim = AnimationUtils.loadAnimation(this, R.anim.anim_spin);
    }

    /** loading animations */
    private void showLoadingAnimation() {
        if (loadingLayout != null && loadingIcon != null && screenBlock != null) {
            loadingLayout.setAnimation(slideDownAnim);
            loadingLayout.setVisibility(View.VISIBLE);
            loadingIcon.setAnimation(spinAnim);
            screenBlock.setVisibility(View.VISIBLE);
            setFabEnabled(false);
        }
    }

    private void hideLoadingAnimation() {
        if (loadingLayout != null && loadingIcon != null && screenBlock != null) {
            if (loadingLayout.getVisibility() == View.VISIBLE && screenBlock.getVisibility() == View.VISIBLE) {
                spinAnim.cancel();
                spinAnim.reset();
                loadingLayout.startAnimation(slideUpAnim);
                loadingLayout.setVisibility(View.GONE);
                screenBlock.setVisibility(View.GONE);
                setFabEnabled(true);
            }
        }
    }

    /** popup menu methods */
    private void hidePopupMenu() {
        if (popupMenu.getVisibility() == View.VISIBLE) {
            popupMenu.setVisibility(View.GONE);
        }
    }

    /** download methods */
    private void initiateFirstDataDownload() {
        if (sp != null) {
            if (sp.contains(KEY_HAD_FIRST_USE)) {
                // data has been loaded already
                Log.e(LOGTAG, "app has had first use");
                //
                retrieveNewChallenge();
            } else {
                // need to load data
//                showLoadingAnimation();
                downloadDataFromParse();
                sp.edit().putBoolean(KEY_HAD_FIRST_USE, true).apply();
            }
        }
    }

    private void downloadDataFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        showLoadingAnimation();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // no error
                    Log.e(LOGTAG, "calling insertChallenges method");
                    dbHelper.insertChallengesToDb(objects);

                    hideLoadingAnimation();

                    retrieveNewChallenge();
                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }
        });
    }

    /** retrieve/display challenge */
    private void updateDifficultyState(String newState) {
        if (newState == null) {
            stateDifficulty = DEFAULT_DIFFICULTY_STATE;
        } else {
            if (sp.contains(KEY_STATE_DIFFICULTY)) {
                stateDifficulty = newState;
            } else {
                stateDifficulty = DEFAULT_DIFFICULTY_STATE;
            }
        }

        sp.edit().putString(KEY_STATE_DIFFICULTY, stateDifficulty).apply();
    }

    private void updateGroupSizeState(Integer newState) {
        if (newState == null) {
            stateGroupSize = DEFAULT_GROUP_SIZE_STATE;
        } else {
            if (sp.contains(KEY_STATE_GROUP_SIZE)) {
                stateGroupSize = newState;
            } else {
                stateGroupSize = DEFAULT_GROUP_SIZE_STATE;
            }
        }

        sp.edit().putInt(KEY_STATE_GROUP_SIZE, stateGroupSize).apply();
    }

    private void updateCurrentObjectId(String objectId) {
        if (objectId != null) {
            this.currentObjectId = objectId;
            sp.edit().putString(KEY_OBJECT_ID, objectId).apply();
        }
    }

    private void retrieveNewChallenge() {
        Challenge c = dbHelper.retrieveNewChallenge("Easy", 1);

        if (c != null) {
            updateCurrentObjectId(c.getObjectId());
            displayChallenge(c);
        } else {
            Log.e(LOGTAG, "Couldn't get a challenge from database");
        }
    }

    private void restoreChallenge(String id) {
        displayChallenge(dbHelper.retrieveChallengeById(id));
    }

    private void displayChallenge(Challenge c) {
        // set text for title textview
        challengeTitle.setText(c.getName());
        // set text for description textview
        challengeText.setText(c.getName());
        // set text for difficulty textview
        challengeDifficulty.setText(c.getDifficulty());
        // set text for min textview
        challengeMin.setText(String.valueOf(c.getGroupMin()));
        // set text for max textview
        challengeMax.setText(String.valueOf(c.getGroupMax()));
    }

    /** restore methods */
    private void restoreFromSavedInstanceState(Bundle state) {
        if (state != null) {
            // restores the popup menu to the visible state it had before screen orientation changed
            if (state.getInt(KEY_POPUP_VISIBILITY) == View.VISIBLE) {
                popupMenu.setVisibility(View.VISIBLE);
            } else {
                popupMenu.setVisibility(View.GONE);
            }

            // show challenge
            if (state.getString(KEY_OBJECT_ID) != null) {
                restoreChallenge(state.getString(KEY_OBJECT_ID));
            } else {
                Log.e(LOGTAG, "getting new challenge for restore");
                retrieveNewChallenge();
            }
        }
    }

    /** overridden methods */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POPUP_VISIBILITY, popupMenu.getVisibility());
        outState.putString(KEY_OBJECT_ID, currentObjectId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.challenge_root), "Close app?", Snackbar.LENGTH_LONG);

        snackbar.setAction("YEAH", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        snackbar.show();
    }

    // parent methods
    @Override
    protected String setToolbarTitle() {
        return getResources().getString(R.string.app_title);
    }

    @Override
    protected boolean useFab() {
        return true;
    }

    @Override
    protected int setFabGravity() {
        return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
    }

    @Override
    protected int setFabIcon() {
        return R.drawable.ic_done_white_24dp;
    }

    @Override
    protected int[] setFabMargins() {
        // left, top, right, bottom
        return new int[]{0, 0, 0, (int) getResources().getDimension(R.dimen.fab_radius)};
    }

    // click interface implementation
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.base_fab:
                // user has completed the current challenge
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                // update the challenge to 'completed' in database
                dbHelper.setChallengeCompleted(currentObjectId);
                // get the next challenge to display
                retrieveNewChallenge();
                /*
                    if there are no challenges left for current difficulty
                 */
                retrieveNewChallenge();
                break;
            case R.id.random_challenge_btn:
                Toast.makeText(this, "Random challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.skip_challenge_btn:
                Toast.makeText(this, "Skip challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.all_challenges_button:
                startActivity(AllChallengesActivity.class);
                break;
            case R.id.challenge_settings_button:
                if (popupMenu.getVisibility() == View.VISIBLE) {
                    popupMenu.setVisibility(View.GONE);
                } else {
                    popupMenu.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.difficulty_down:

                break;
            case R.id.difficulty_up:

                break;
            case R.id.group_down:

                break;
            case R.id.group_up:

                break;
            case R.id.base_container:
                hidePopupMenu();
                break;
            case R.id.challenge_title:
                hidePopupMenu();
                break;
            case R.id.challenge_text:
                hidePopupMenu();
                break;
            default:
                break;
        }

    }

}
