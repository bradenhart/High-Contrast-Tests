package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.bradenhart.hcdemoui.Utils.*;

import com.bradenhart.hcdemoui.database.Challenge;
import com.bradenhart.hcdemoui.database.DatabaseHelper;
import com.melnykov.fab.FloatingActionButton;

import com.bradenhart.hcdemoui.Difficulty;

import com.bradenhart.hcdemoui.R;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener {

    private final String LOGTAG = "ChallengeActivity";

    private FrameLayout root;
    private FloatingActionButton fab;
    private TextView randomChallengeBtn, skipChallengeBtn, allChallengeBtn, challengeSettingsBtn, confirmChangeBtn;
    private RelativeLayout popupMenu;
    private ImageView difficultyTab, groupTab, cancelChangeBtn;
    private LinearLayout difficultyLayout;
    private LinearLayout groupLayout;
    private TextView popupMenuHeader;
    private NumberPicker difficultyPicker, groupSizePicker;

    private TextView challengeTitle, challengeText, challengeDifficulty, challengeMin, challengeMax;
    private SharedPreferences sp;
    private final String KEY_POPUP_VISIBILITY = "popup_visibility";
    private final String KEY_GROUP_LAYOUT_VISIBILITY = "group_layout_visibility";
    private DatabaseHelper dbHelper;
    private Animation slideUpAnim, slideDownAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // initialise all views here
        initViews();
        // set onClickListener for all views here
        setClickListenerOnViews();
        //
        initGroupSizePicker();

        sp = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        dbHelper = DatabaseHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String playId = extras.getString(KEY_EXTRA_PLAY_ID);
            if (playId != null) {
                restoreChallenge(playId);
            }
        }

        if (storedObjectId() == null) {
            // nothing saved, show new or random challenge
            getChallenge();
        } else {
            // something saved, display that challenge
            restoreChallenge(storedObjectId());
        }

        updateCheckedDrawerItem(R.id.nav_new_challenge);

        slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_from_bottom);
        slideUpAnim.setDuration(50);

        slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_to_bottom);
        slideDownAnim.setDuration(100);

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
        // popup menu tab for difficulty layout
        difficultyTab = (ImageView) findViewById(R.id.difficulty_icon_tab);
        // popup menu tab for group size layout
        groupTab = (ImageView) findViewById(R.id.group_icon_tab);
        // difficulty layout in popup menu
        difficultyLayout = (LinearLayout) findViewById(R.id.difficulty_layout);
        // group size layout in popup menu
        groupLayout = (LinearLayout) findViewById(R.id.group_layout);
        // text header for popup menu
        popupMenuHeader = (TextView) findViewById(R.id.popup_menu_header);
        // number picker for difficulty
        difficultyPicker = (NumberPicker) findViewById(R.id.pick_difficulty);
        // number picker for group size
        groupSizePicker = (NumberPicker) findViewById(R.id.pick_group_size);
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
        // challenge difficulty
        challengeDifficulty = (TextView) findViewById(R.id.challenge_difficulty_textview);
        // challenge min
        challengeMin = (TextView) findViewById(R.id.challenge_min);
        // challenge max
        challengeMax = (TextView) findViewById(R.id.challenge_max);
        //
        confirmChangeBtn = (TextView) findViewById(R.id.popup_confirm_change);
        //
        cancelChangeBtn = (ImageView) findViewById(R.id.popup_cancel_change);

        fab = (FloatingActionButton) findViewById(R.id.base_fab);
        fab.setOnClickListener(this);
    }

    private void setClickListenerOnViews() {
        root.setOnClickListener(this);
        randomChallengeBtn.setOnClickListener(this);
        skipChallengeBtn.setOnClickListener(this);
        allChallengeBtn.setOnClickListener(this);
        challengeSettingsBtn.setOnClickListener(this);
        difficultyTab.setOnClickListener(this);
        groupTab.setOnClickListener(this);
        confirmChangeBtn.setOnClickListener(this);
        cancelChangeBtn.setOnClickListener(this);
    }

    private void initDifficultyPicker() {
//        String[] items = dbHelper.getValidDifficultyOptions();
//        Log.e(LOGTAG, printQueryParams(items));
        String[] items = Difficulty.getNames();
        difficultyPicker.setMinValue(1);
        difficultyPicker.setMaxValue(items.length);
        difficultyPicker.setDisplayedValues(items);
        difficultyPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    private void setDifficultyValueChangeClickListener() {
        difficultyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String[] values = picker.getDisplayedValues();
                updateDifficultyState(values[newVal]);
            }
        });
    }

    private void setGroupSizeValueChangeClickListener() {

    }

    private void initGroupSizePicker() {
        groupSizePicker.setMinValue(GROUP_SIZE_MINIMUM);
        groupSizePicker.setMaxValue(GROUP_SIZE_MAXIMUM);
        groupSizePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    /** removed animation methods from here */

    /**
     * popup menu methods
     */
    private void openPopupMenu() {
        popupMenu.setVisibility(View.VISIBLE);
        popupMenu.startAnimation(slideUpAnim);
        showDifficultyLayout();
        challengeSettingsBtn.setSelected(true);
        setButtonsEnabled(false);
    }

    private void closePopupMenu() {
        popupMenu.startAnimation(slideDownAnim);
        popupMenu.setVisibility(View.GONE);
        hideDifficultyLayout();
        hideGroupLayout();
        setButtonsEnabled(true);
        challengeSettingsBtn.setSelected(false);
    }

    private void setButtonsEnabled(boolean enabled) {
        randomChallengeBtn.setClickable(enabled);
        skipChallengeBtn.setClickable(enabled);
        allChallengeBtn.setClickable(enabled);
    }

    private void showDifficultyLayout() {
        groupTab.setSelected(false);
        difficultyTab.setSelected(true);
        initDifficultyPicker();
        difficultyLayout.setVisibility(View.VISIBLE);
        popupMenuHeader.setText("Difficulty");
        difficultyPicker.setValue(storedDifficultyAsInt());
        Log.e(LOGTAG, "showing " + storedDifficultyAsString() + " (" + storedDifficultyAsInt() + ")");
    }

    private void hideDifficultyLayout() {
        difficultyTab.setSelected(false);
        difficultyLayout.setVisibility(View.GONE);
    }

    private void showGroupLayout() {
        hideDifficultyLayout();
        groupTab.setSelected(true);
        groupLayout.setVisibility(View.VISIBLE);
        popupMenuHeader.setText("Group Size");
        groupSizePicker.setValue(storedGroupMin());
        Log.e(LOGTAG, "showing " + storedGroupMin());
    }

    private void hideGroupLayout() {
        groupTab.setSelected(false);
        groupLayout.setVisibility(View.GONE);
    }

    /**
     * removed download methods from here
     */

    private String printQueryParams(String[] params) {
        String string = "";
        for (String s : params) {
            string += s + ", ";
        }
        return string;
    }

    /**
     * retrieve/display challenge
     */
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

    private boolean inRepeatMode() {
        return sp.getBoolean(KEY_REPEAT_MODE, false);
    }

    private void updateDifficultyState(String newState) {
        if (newState == null) {
            sp.edit().putString(KEY_STATE_DIFFICULTY, DEFAULT_DIFFICULTY_STATE).apply();
        } else {
            sp.edit().putString(KEY_STATE_DIFFICULTY, newState).apply();
            Log.e(LOGTAG, "updating difficulty state: " + newState);
        }
    }

    private void updateGroupSizeState(Integer newState) {
        if (newState == null) {
            sp.edit().putInt(KEY_STATE_GROUP_SIZE, DEFAULT_GROUP_SIZE_STATE).apply();
        } else {
            sp.edit().putInt(KEY_STATE_GROUP_SIZE, newState).apply();
            Log.e(LOGTAG, "Group Size updated: " + newState);
        }
    }

    private void updateCurrentObjectId(String objectId) {
        sp.edit().putString(KEY_OBJECT_ID, objectId).apply();
    }

    private void displayChallenge(Challenge c) {
        updateCurrentObjectId(c.getObjectId());
        // set text for title textview
        challengeTitle.setText(c.getName());
        // set text for description textview
//        challengeText.setText(c.getDescription());
        // set text for difficulty textview
        challengeDifficulty.setText(c.getDifficulty().getName());
        // set text for min textview
        challengeMin.setText(String.valueOf(c.getGroupMin()));
        // set text for max textview
        challengeMax.setText(String.valueOf(c.getGroupMax()));
        Log.e(LOGTAG, "displaying challenge");
    }

    private void setRandomChallengeMode(boolean isSet) {
        sp.edit().putBoolean(KEY_RANDOM_MODE, isSet).apply();
        Log.e(LOGTAG, isSet ? "Random mode: ON" : "Random mode: OFF");
    }

    private boolean inRandomMode() {
        return sp.getBoolean(KEY_RANDOM_MODE, DEFAULT_RANDOM_MODE_STATE);
    }

    /**
     * restore methods
     */
    private void restoreState(Bundle state) {
        if (state != null) {
            // restores the popup menu to the visible state it had before screen orientation changed
            if (state.getInt(KEY_POPUP_VISIBILITY) == View.VISIBLE) {
                popupMenu.setVisibility(View.VISIBLE);
                if (state.getInt(KEY_GROUP_LAYOUT_VISIBILITY) == View.VISIBLE) {
                    showGroupLayout();
                } else {
                    showDifficultyLayout();
                }
            } else {
                popupMenu.setVisibility(View.GONE);
            }
        }

        randomChallengeBtn.setSelected(inRandomMode());
    }

    /**
     * overridden methods
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POPUP_VISIBILITY, popupMenu.getVisibility());
        outState.putInt(KEY_GROUP_LAYOUT_VISIBILITY, groupLayout.getVisibility());
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

    private boolean getChallenge() {
        Challenge challenge;
        String query;
        String[] params;

        if (!inRandomMode() && !inRepeatMode()) {
            // NORMAL
            Log.e(LOGTAG, "SELECT_NORMAL");
            if (storedObjectId() == null) {
                query = SELECT_NORMAL_NEW;
                params = new String[]{COMPLETED_FALSE, String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), LIMIT_ONE};
            } else {
                query = SELECT_NORMAL;
                params = new String[]{COMPLETED_FALSE, String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), storedObjectId(), LIMIT_ONE};
            }
        } else if (inRandomMode() && !inRepeatMode()) {
            // SHUFFLE
            Log.e(LOGTAG, "SELECT_SHUFFLE");
            query = SELECT_SHUFFLE;
            params = new String[]{COMPLETED_FALSE, storedObjectId(), LIMIT_ONE};
        } else if (!inRandomMode() && inRepeatMode()) {
            // NORMAL_REPEAT
            Log.e(LOGTAG, "SELECT_NORMAL_REPEAT");
            query = SELECT_NORMAL_REPEAT;
            params = new String[]{String.valueOf(storedDifficultyAsInt()), String.valueOf(storedGroupMin()), storedObjectId(), LIMIT_ONE};
        } else if (inRandomMode() && inRepeatMode()) {
            // SHUFFLE_REPEAT
            Log.e(LOGTAG, "SELECT_SHUFFLE_REPEAT");
            query = SELECT_SHUFFLE_REPEAT;
            params = new String[]{COMPLETED_TRUE, COMPLETED_FALSE, storedObjectId(), LIMIT_ONE};
        } else {
            // BY_ID
            Log.e(LOGTAG, "SELECT_BY_ID");
            query = SELECT_BY_ID;
            params = new String[]{storedObjectId(), LIMIT_ONE};
        }


        challenge = dbHelper.getChallenge(query, params);
        if (challenge != null) {
            displayChallenge(challenge);
            return true;
        } else {
            Log.e(LOGTAG, "Couldn't get a challenge from database");
            return false;
        }

    }

    private void restoreChallenge(String id) {
        Challenge challenge = dbHelper.getChallenge(SELECT_BY_ID, new String[]{id, LIMIT_ONE});
        if (challenge != null) {
            displayChallenge(challenge);
        } else {
            Log.e(LOGTAG, "Couldn't restore challenge from database");
        }
    }

    // click interface implementation
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.base_fab:
                // user has completed the current challenge
                // update the challenge to 'completed' in database
                dbHelper.setChallengeCompleted(storedObjectId());
                // get the next challenge to display

                // if challenge is null, we must be out of challenges
                if (!getChallenge()) {
                    showCompletionDialog();
                }
                break;
            case R.id.random_challenge_btn:
//                boolean selected = randomChallengeBtn.isSelected();
                randomChallengeBtn.setSelected(!randomChallengeBtn.isSelected());
                setRandomChallengeMode(randomChallengeBtn.isSelected());

//                if (randomChallengeBtn.isSelected()) {
//                    randomChallengeBtn.setSelected(false);
//                    setRandomChallengeMode(false);
//                } else {
//                    randomChallengeBtn.setSelected(true);
//                    setRandomChallengeMode(true);
//                }

                // go back to getting challenges based on game settings

                // if challenge is null, we must be out of challenges
                if (!getChallenge()) {
                    showCompletionDialog();
                }
                break;
            case R.id.skip_challenge_btn:
                // if challenge is null, we must be out of challenges
                if (!getChallenge()) {
                    showCompletionDialog();
                }
                break;
            case R.id.all_challenges_button:
                startActivity(AllChallengesActivity.class, R.anim.anim_slide_from_bottom, R.anim.anim_fade_out); //R.anim.anim_slide_to_bottom exit
                break;
            case R.id.challenge_settings_button:
                if (popupMenu.getVisibility() == View.VISIBLE) {
                    closePopupMenu();
                } else {
                    openPopupMenu();
                }
                break;
            case R.id.difficulty_icon_tab:
                if (!difficultyTab.isSelected()) {
                    hideGroupLayout();
                    showDifficultyLayout();
                }
                break;
            case R.id.group_icon_tab:
                if (!groupTab.isSelected()) {
                    hideDifficultyLayout();
                    showGroupLayout();
                }
                break;
            case R.id.popup_confirm_change:
                if (difficultyLayout.getVisibility() == View.VISIBLE) {
                    String[] values = difficultyPicker.getDisplayedValues();
                    int index = difficultyPicker.getValue() - 1;
                    final String oldState = storedDifficultyAsString();
                    final String updatedState = values[index];
                    final String currentId = storedObjectId();

                    if (!oldState.equals(updatedState)) {
                        showDifficultyChangedSnackbar(oldState, updatedState, currentId);
                    }
                } else if (groupLayout.getVisibility() == View.VISIBLE) {
                    final Integer oldState = storedGroupMin();
                    final Integer updatedState = groupSizePicker.getValue();
                    final String currentId = storedObjectId();

                    if (!oldState.equals(updatedState)) {
                        showGroupSizeChangedSnackbar(oldState, updatedState, currentId);
                    }
                }
                closePopupMenu();
                break;
            case R.id.popup_cancel_change:
                closePopupMenu();
                break;
            default:
                break;
        }
    }

    private void showCompletionDialog() {
        String message = inRandomMode() ?
                "You've completed all the challenges! You can now replay any challenge, or pick one from the list to have another go." :
                "You've completed all the " + storedDifficultyAsString().toLowerCase() + " challenges!\nChange the difficulty up, or repeat some challenges.";
        final Boolean mode = inRandomMode();

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Woohoo!");
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_done_green_24dp);
        dialog.setCancelable(false);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putBoolean(KEY_REPEAT_MODE, mode).apply();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void showDifficultyChangedSnackbar(final String oldState, final String updatedState, final String currentId) {
        updateDifficultyState(updatedState);
        getChallenge();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.challenge_root), "Changed difficulty", Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDifficultyState(oldState);
                updateCurrentObjectId(currentId);
                // reload a challenge that meets settings, or if in random mode, do nothing.
                getChallenge();
            }
        });

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                fab.setClickable(true);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                fab.setClickable(false);
            }
        });

        snackbar.show();
    }

    private void showGroupSizeChangedSnackbar(final Integer oldState, final Integer updatedState, final String currentId) {
        updateGroupSizeState(updatedState);
        getChallenge();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.challenge_root), "Changed group size", Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroupSizeState(oldState);
                updateCurrentObjectId(currentId);
                // reload a challenge that meets settings, or if in random mode, do nothing.
                getChallenge();
            }
        });

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                fab.setClickable(true);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                fab.setClickable(false);
            }
        });

        snackbar.show();
    }

}
