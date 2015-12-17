package com.bradenhart.hcdemoui.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcdemoui.Utils;
import com.bradenhart.hcdemoui.database.Challenge;
import com.bradenhart.hcdemoui.database.DatabaseHelper;
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
    private TextView challengeTitle, challengeText;
    private ImageView diffDown, diffUp, groupDown, groupUp;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    private final String KEY_POPUP_VISIBILITY = "popup_visibility";
    private final String KEY_HAD_FIRST_USE = "first_use";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        sp = getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        spEdit = sp.edit();

        dbHelper = DatabaseHelper.getInstance(this);

        initiateFirstDataDownload();

        updateCheckedDrawerItem(R.id.nav_new_challenge);

        // initialise all views here
        initViews();
        // set onClickListener for all views here
        setClickListenerOnViews();

        // restore activity from savedInstanceState
        restoreFromSavedInstanceState(savedInstanceState);

    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.test_fab:
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(this, "Change challenge settings... ", Toast.LENGTH_SHORT).show();
//                startActivity(GameSettingsActivity.class);
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

    private void hidePopupMenu() {
        if (popupMenu.getVisibility() == View.VISIBLE) {
            popupMenu.setVisibility(View.GONE);
        }
    }

    private void restoreFromSavedInstanceState(Bundle state) {
        if (state != null) {

            // restores the popup menu to the visible state it had before screen orientation changed
            if (state.getInt(KEY_POPUP_VISIBILITY) == View.VISIBLE) {
                popupMenu.setVisibility(View.VISIBLE);
            } else {
                popupMenu.setVisibility(View.GONE);
            }


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POPUP_VISIBILITY, popupMenu.getVisibility());
    }

    private void initiateFirstDataDownload() {
        if (sp != null) {
            if (sp.contains(KEY_HAD_FIRST_USE)) {
                // data has been loaded already
                Log.e(LOGTAG, "app has had first use");
            } else {
                // need to load data
                ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("Downloading App Data");
                pd.setMessage("Downloading Challenges from the cloud. \nWon't be long.");
                pd.setCancelable(false);
                pd.show();
                downloadDataFromParse();
                pd.cancel();
                sp.edit().putBoolean(KEY_HAD_FIRST_USE, true).apply();
            }
        }
    }

    private void downloadDataFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // no error
                    Log.e(LOGTAG, "calling insertChallenges method");
                    dbHelper.insertChallengesToDb(objects);

                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }
        });
    }

    private void testDownloadDataFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        query.getInBackground("PEQcJBDRYv", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object is the Challenge
//                    String name = object.getString("name");
//                    String description = object.getString("description");
//                    String difficulty = object.getString("difficulty");
//                    int groupMin = object.getInt("groupMin");
//                    int groupMax = object.getInt("groupMax");
//                    Date createdAt = object.getCreatedAt();

                    Log.e(LOGTAG, challengeObjectToString(object));
                    Log.e(LOGTAG, "this date: " + new Date());

                } else {
                    // something went wrong
                }
            }
        });
    }

    private String challengeObjectToString(ParseObject object) {
        String name = object.getString("name");
        String description = object.getString("description");
        String difficulty = object.getString("difficulty");
        int groupMin = object.getInt("groupMin");
        int groupMax = object.getInt("groupMax");
        Date createdAt = object.getCreatedAt();
        Date today = new Date();

        if (today.after(createdAt)) {
            Log.e(LOGTAG, "dates work");
        }

        String string = "";
        string += "name: " + name + ",\n";
        string += "description: " + description + ",\n";
        string += "difficulty: " + difficulty + ",\n";
        string += "groupMin: " + groupMin + ",\n";
        string += "groupMax: " + groupMax + ",\n";
        string += "createdAt: " + createdAt + ".";

        return string;

    }
}
