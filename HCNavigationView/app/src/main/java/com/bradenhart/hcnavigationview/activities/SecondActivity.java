package com.bradenhart.hcnavigationview.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.bradenhart.hcnavigationview.R;

public class SecondActivity extends BaseActivity {

    private final String LOGTAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_second, frameLayout);
        Toast.makeText(this, "Second", Toast.LENGTH_SHORT).show();
    }


}
