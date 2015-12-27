package com.bradenhart.hcdemoui.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
    private final String KEY_POPUP_VISIBILITY = "popup_visibility";
    private final String KEY_HAD_FIRST_USE = "first_use";
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

        dbHelper = DatabaseHelper.getInstance(this);

//        updateDifficultyState(null); //  ???
//        updateGroupSizeState(null);  //  ???

        // display saved challenge
        // display random challenge
        // display non-random challenge

        if (sp.contains(KEY_HAD_FIRST_USE)) {
            if (storedObjectId() == null) {
                // nothing saved, show new or random challenge
                if (inRandomChallengeMode()) {
                    String queryString = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?";
                    String[] queryParams = new String[]{"0", "1"};
                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                } else {

                    String queryString = "select * from " + TABLE_CHALLENGE
                            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? limit ?";
                    String[] queryParams = new String[] { "0", String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), "1" };
                    displayChallenge( dbHelper.getChallenge(queryString, queryParams) );
                }
            } else {
                // something saved, display that challenge
                String queryString = "select * from " + TABLE_CHALLENGE + " where " + KEY_OBJECT_ID + " =? ";
                String[] queryParams = new String[] { storedObjectId() };
                displayChallenge( dbHelper.getChallenge(queryString, queryParams) );
            }
        } else {
            initiateFirstDataDownload();
            sp.edit().putBoolean(KEY_HAD_FIRST_USE, true).apply();
        }

//        initiateFirstDataDownload();

        updateCheckedDrawerItem(R.id.nav_new_challenge);

        // restore activity from savedInstanceState
        restoreState(savedInstanceState);

    }

    /**
     * initialisations
     */
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

    /**
     * loading animations
     */
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

    /**
     * popup menu methods
     */
    private void hidePopupMenu() {
        if (popupMenu.getVisibility() == View.VISIBLE) {
            popupMenu.setVisibility(View.GONE);
        }
    }

    /**
     * download methods
     */
    private void initiateFirstDataDownload() {
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

                    String queryString = "select * from " + TABLE_CHALLENGE
                            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? limit ?";
                    String[] queryParams = new String[]{"0", String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), "1"};

                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }
        });

    }

    /** */

    private String storedDifficultyAsString() {
        return sp.getString(KEY_STATE_DIFFICULTY, DEFAULT_DIFFICULTY_STATE);
    }

    private Integer storedDifficultyAsInt() {
        return getDifficultyValue(storedDifficultyAsString());
    }

    private Integer storedGroupMin() {
        return sp.getInt(KEY_STATE_GROUP_SIZE, DEFAULT_GROUP_SIZE_STATE);
    }

    private String storedObjectId() {
        return sp.getString(KEY_OBJECT_ID, null);
    }

    /** */

    /**
     * retrieve/display challenge
     */
    private void updateDifficultyState(String newState) {
        if (newState == null) {
            sp.edit().putString(KEY_STATE_DIFFICULTY, DEFAULT_DIFFICULTY_STATE).apply();
        } else {
            sp.edit().putString(KEY_STATE_DIFFICULTY, newState).apply();
        }
    }

    private void updateGroupSizeState(Integer newState) {
        if (newState == null) {
            sp.edit().putInt(KEY_STATE_GROUP_SIZE, DEFAULT_GROUP_SIZE_STATE).apply();
        } else {
            sp.edit().putInt(KEY_STATE_GROUP_SIZE, newState).apply();
        }
    }

    private void updateCurrentObjectId(String objectId) {
        sp.edit().putString(KEY_OBJECT_ID, objectId).apply();
    }

//    private void retrieveNewChallenge() {
//        Challenge c = dbHelper.retrieveNewChallenge("Easy", 1);
//
//        if (c != null) {
//            updateCurrentObjectId(c.getObjectId());
//            displayChallenge(c);
//        } else {
//            Log.e(LOGTAG, "Couldn't get a challenge from database");
//        }
//    }

    private void restoreChallenge(String id) {
        displayChallenge(dbHelper.retrieveChallengeById(id));
    }

    private void displayChallenge(Challenge c) {
        if (c != null) {
            updateCurrentObjectId(c.getObjectId());
            // set text for title textview
            challengeTitle.setText(c.getName());
            // set text for description textview
//        challengeText.setText(c.getDescription());
            // set text for difficulty textview
            challengeDifficulty.setText(c.getDifficulty());
            // set text for min textview
            challengeMin.setText(String.valueOf(c.getGroupMin()));
            // set text for max textview
            challengeMax.setText(String.valueOf(c.getGroupMax()));
            Log.e(LOGTAG, "displaying challenge");
        } else {
            Log.e(LOGTAG, "Couldn't get a challenge from database");
        }
    }

    private void setRandomChallengeMode(boolean isSet) {
        if (!sp.contains(KEY_RANDOM_MODE)) {
            sp.edit().putBoolean(KEY_RANDOM_MODE, DEFAULT_RANDOM_MODE_STATE).apply();
        } else {
            sp.edit().putBoolean(KEY_RANDOM_MODE, isSet).apply();
        }
    }

    private boolean inRandomChallengeMode() {
        return sp.getBoolean(KEY_RANDOM_MODE, DEFAULT_RANDOM_MODE_STATE);
    }

//    private void retrieveRandomChallenge() {
//        Challenge c = dbHelper.retrieveRandomChallenge();
//        updateCurrentObjectId(c.getObjectId());
//        displayChallenge(c);
//    }

    /**
     * restore methods
     */
    private void restoreState(Bundle state) {
        if (state != null) {
            // restores the popup menu to the visible state it had before screen orientation changed
            if (state.getInt(KEY_POPUP_VISIBILITY) == View.VISIBLE) {
                popupMenu.setVisibility(View.VISIBLE);
            } else {
                popupMenu.setVisibility(View.GONE);
            }
        }

        // show challenge
        // get the challenge from SP instead of savedInstance bundle
//        if (sp != null) {
//            Log.e(LOGTAG, "sp not null");
//            currentObjectId = sp.getString(KEY_OBJECT_ID, null);
//            boolean randomState = sp.getBoolean(KEY_RANDOM_MODE, DEFAULT_RANDOM_MODE_STATE);
//            if (currentObjectId == null) {
//                Log.e(LOGTAG, "currentObjectId is null");
//                // no challenge saved, need to get another
//                if (randomState) {
//                    Log.e(LOGTAG, "randomState is true");
//                    // random button is selected, get a random challenge
//                    retrieveRandomChallenge();
//                } else {
//                    Log.e(LOGTAG, "randomState is false");
//                    // get a new challenge based on game settings
//                    retrieveNewChallenge();
//                }
//            } else {
//                Log.e(LOGTAG, "currentObjectId is  not null");
//                // display the saved one
//                restoreChallenge(currentObjectId);
//            }
//
//        }

        randomChallengeBtn.setSelected(inRandomChallengeMode());


    }

    /**
     * overridden methods
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POPUP_VISIBILITY, popupMenu.getVisibility());
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
//                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                // update the challenge to 'completed' in database
                dbHelper.setChallengeCompleted(storedObjectId());
                // get the next challenge to display
                if (inRandomChallengeMode()) {
                    String queryString = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?";
                    String[] queryParams = new String[] { "0", "1" };
                    displayChallenge( dbHelper.getChallenge(queryString, queryParams) );
                } else {
                    String queryString = "select * from " + TABLE_CHALLENGE
                            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? limit ?";
                    String[] queryParams = new String[] { "0", String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), "1" };
                    displayChallenge( dbHelper.getChallenge(queryString, queryParams) );
                }
                /*
                    if there are no challenges left for current difficulty
                 */
                break;
            case R.id.random_challenge_btn:
                if (randomChallengeBtn.isSelected()) {
                    setRandomChallengeMode(false);
                    randomChallengeBtn.setSelected(false);
                    // go back to getting challenges based on game settings
                    String queryString = "select * from " + TABLE_CHALLENGE
                            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? limit ?";
                    String[] queryParams = new String[] { "0", String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), "1" };
                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                } else {
//                    Toast.makeText(this, "Random challenge... ", Toast.LENGTH_SHORT).show();
                    setRandomChallengeMode(true);
                    randomChallengeBtn.setSelected(true);
                    String queryString = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?";
                    String[] queryParams = new String[] { "0", "1" };
                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                }
                break;
            case R.id.skip_challenge_btn:
//                Toast.makeText(this, "Skip challenge... ", Toast.LENGTH_SHORT).show();
                if (inRandomChallengeMode()) {
                    Log.e(LOGTAG, "skip to next random challenge");
                    String queryString = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? and " + KEY_OBJECT_ID
                            + " !=? order by random() limit ?";
                    String[] queryParams = new String[] { "0", storedObjectId(), "1" };
                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                } else {
                    Log.e(LOGTAG, "skip to next new challenge");
                    String queryString = "select * from " + TABLE_CHALLENGE
                            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? and " + KEY_OBJECT_ID
                            + " !=? order by random() limit ?";
                    String[] queryParams = new String[] { "0", String.valueOf(storedDifficultyAsInt())
                            , String.valueOf(storedGroupMin()), storedObjectId(), "1" };
                    displayChallenge(dbHelper.getChallenge(queryString, queryParams));
                }
                break;
            case R.id.all_challenges_button:
                startActivity(AllChallengesActivity.class);
                break;
            case R.id.challenge_settings_button:
                if (popupMenu.getVisibility() == View.VISIBLE) {
                    popupMenu.setVisibility(View.GONE);
                    challengeSettingsBtn.setSelected(false);
                } else {
                    popupMenu.setVisibility(View.VISIBLE);
                    challengeSettingsBtn.setSelected(true);
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
