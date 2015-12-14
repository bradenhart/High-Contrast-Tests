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
public class GameSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        updateCheckedDrawerItem(R.id.nav_settings);

    }

}
