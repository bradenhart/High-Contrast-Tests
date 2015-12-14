package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 15/12/15.
 */
public class HelpFeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);

        updateCheckedDrawerItem(R.id.nav_help_feedback);

    }
}
