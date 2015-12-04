package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;

/**
 * Created by bradenhart on 4/12/15.
 */
public class GameSettingsActivity extends AppCompatActivity {

    private View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.game_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.game_settings_title));

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view2.getVisibility() == View.VISIBLE) {
                    view2.setVisibility(View.GONE);
                } else {
                    view2.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}
