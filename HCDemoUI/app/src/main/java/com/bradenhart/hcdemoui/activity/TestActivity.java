package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 5/12/15.
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected String setToolbarTitle() {
        return "Test Activity";
    }
}
