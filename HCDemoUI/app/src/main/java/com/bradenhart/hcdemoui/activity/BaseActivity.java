package com.bradenhart.hcdemoui.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 5/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        if (useToolbar()) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(setToolbarTitle());
        } else {
            toolbar.setVisibility(View.GONE);
        }

    }

    protected String setToolbarTitle() {
        return getResources().getString(R.string.app_title);
    }

    protected boolean useToolbar() {
        return true;
    }

}
