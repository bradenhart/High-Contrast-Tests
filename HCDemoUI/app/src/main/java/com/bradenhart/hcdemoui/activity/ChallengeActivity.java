package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcdemoui.R;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout root;
    private TextView randomChallengeBtn, skipChallengeBtn, allChallengeBtn, challengeSettingsBtn;
    private RelativeLayout popupMenu;
    private TextView challengeTitle, challengeText;
    private ImageView diffDown, diffUp, groupDown, groupUp;

    private final String KEY_POPUP_VISIBILITY = "popup_visibility";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge, menu);
        return true;
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
}
