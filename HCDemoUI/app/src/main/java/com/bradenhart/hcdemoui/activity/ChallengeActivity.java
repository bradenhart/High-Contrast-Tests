package com.bradenhart.hcdemoui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.Utils;
import com.melnykov.fab.FloatingActionButton;

public class ChallengeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private TextView randomChallengeBtn, skipChallengeBtn, allChallengeBtn, challengeSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        setUpToolbar();

        fab = (FloatingActionButton) findViewById(R.id.fab1);
        randomChallengeBtn = (TextView) findViewById(R.id.random_challenge_btn);
        skipChallengeBtn = (TextView) findViewById(R.id.skip_challenge_btn);
        allChallengeBtn = (TextView) findViewById(R.id.all_challenges_button);
        challengeSettingsBtn = (TextView) findViewById(R.id.challenge_settings_button);

        fab.setOnClickListener(this);
        randomChallengeBtn.setOnClickListener(this);
        skipChallengeBtn.setOnClickListener(this);
        allChallengeBtn.setOnClickListener(this);
        challengeSettingsBtn.setOnClickListener(this);

    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_challenge_toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.app_title));
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
            case R.id.fab1:
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.random_challenge_btn:
                Toast.makeText(this, "Random challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.skip_challenge_btn:
                Toast.makeText(this, "Skip challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.all_challenges_button:
                Utils.startActivity(this, AllChallengesActivity.class);
                break;
            case R.id.challenge_settings_button:
//                Toast.makeText(this, "Change challenge settings... ", Toast.LENGTH_SHORT).show();
                Utils.startActivity(this, GameSettingsActivity.class);
                break;
            default:
                break;
        }

    }

//    private void startActivity(Class<?> theClass) {
//        Intent intent = new Intent(this, theClass);
//        startActivity(intent);
//    }
}
