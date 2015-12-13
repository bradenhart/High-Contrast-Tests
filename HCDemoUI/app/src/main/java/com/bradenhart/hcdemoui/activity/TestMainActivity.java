package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.view.Gravity;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 12/12/15.
 */
public class TestMainActivity extends TestBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
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
}
