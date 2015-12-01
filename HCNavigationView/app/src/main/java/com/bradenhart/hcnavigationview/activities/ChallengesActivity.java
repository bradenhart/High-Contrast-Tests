package com.bradenhart.hcnavigationview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

//import com.bradenhart.hcnavigationview.Challenge;
import com.bradenhart.hclib.domain.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;
import com.bradenhart.hcnavigationview.fragments.ChallengePageFragment;
import static com.bradenhart.hcnavigationview.Constants.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bradenhart on 25/06/15.
 */
public class ChallengesActivity extends AppCompatActivity {

    private final String LOGTAG = "ChallengesActivity";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;
    private String[] difficultyTypes = {DIFF_EASY, DIFF_MEDIUM, DIFF_HARD, DIFF_IMPOSSIBLE};
    private Toolbar toolbar;
    private DatabaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        dbHandler = DatabaseHandler.getInstance(ChallengesActivity.this);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setTabsFromPagerAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        toolbar = (Toolbar) findViewById(R.id.challenges_app_bar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChallengesActivity.this, BaseActivity.class);
        intent.putExtra(KEY_REQUEST_ACTION, showNewChallenge);
        startActivity(intent);
        finish();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ArrayList<Challenge> challenges = dbHandler.getChallengesByDifficulty(difficultyTypes[position]);
            ChallengePageFragment fragment = ChallengePageFragment.newInstance(difficultyTypes[position]);
            return fragment;
        }

        @Override
        public int getCount() {
            return difficultyTypes.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return difficultyTypes[position];
        }
    }

}
