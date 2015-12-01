package com.bradenhart.hcdemoui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class ChallengeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private ImageView randomChallengeBtn, skipChallengeBtn, challengeSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab1);
        randomChallengeBtn = (ImageView) findViewById(R.id.random_challenge_btn);
        skipChallengeBtn = (ImageView) findViewById(R.id.skip_challenge_btn);
        challengeSettingsBtn = (ImageView) findViewById(R.id.challenge_settings_button);

        randomChallengeBtn.setOnClickListener(this);
        skipChallengeBtn.setOnClickListener(this);
        challengeSettingsBtn.setOnClickListener(this);

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
                break;
            case R.id.random_challenge_btn:
                Toast.makeText(this, "Random challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.skip_challenge_btn:
                Toast.makeText(this, "Skip challenge... ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.challenge_settings_button:
                Toast.makeText(this, "Change challenge settings... ", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}
