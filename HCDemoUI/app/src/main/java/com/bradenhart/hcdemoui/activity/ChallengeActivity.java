package com.bradenhart.hcdemoui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.Utils;
import com.melnykov.fab.FloatingActionButton;

public class ChallengeActivity extends BaseActivity implements View.OnClickListener {

    private TextView randomChallengeBtn, skipChallengeBtn, allChallengeBtn, challengeSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        randomChallengeBtn = (TextView) findViewById(R.id.random_challenge_btn);
        skipChallengeBtn = (TextView) findViewById(R.id.skip_challenge_btn);
        allChallengeBtn = (TextView) findViewById(R.id.all_challenges_button);
        challengeSettingsBtn = (TextView) findViewById(R.id.challenge_settings_button);

        randomChallengeBtn.setOnClickListener(this);
        skipChallengeBtn.setOnClickListener(this);
        allChallengeBtn.setOnClickListener(this);
        challengeSettingsBtn.setOnClickListener(this);

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
                startActivity(this, AllChallengesActivity.class);
                break;
            case R.id.challenge_settings_button:
//                Toast.makeText(this, "Change challenge settings... ", Toast.LENGTH_SHORT).show();
                startActivity(this, GameSettingsActivity.class);
                break;
            default:
                break;
        }

    }

}
