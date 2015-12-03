package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 4/12/15.
 */
public class ChallengeInfoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.challenge_info_toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }
}
