package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;

/**
 * Created by bradenhart on 4/12/15.
 */
public class GameSettingsActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout splitCardView;
    private TextView filterHeader, sortHeader;
    private LinearLayout filterLayout, sortLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        updateCheckedDrawerItem(R.id.nav_settings);

        splitCardView = (LinearLayout) findViewById(R.id.split_card_view);
        filterHeader = (TextView) findViewById(R.id.filter_by_header);
        sortHeader = (TextView) findViewById(R.id.sort_by_header);
        filterLayout = (LinearLayout) findViewById(R.id.filter_button_layout);
        sortLayout = (LinearLayout) findViewById(R.id.sort_button_layout);

        findViewById(R.id.base_fab).setOnClickListener(this);
        filterHeader.setOnClickListener(this);
        sortHeader.setOnClickListener(this);


    }

    @Override
    protected boolean useFab() {
        return true;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.base_fab:
                if (splitCardView.getVisibility() == View.VISIBLE) {
                    showSplitCardView();
                } else {
                    hideSplitCardView();
                }
                break;
            case R.id.filter_by_header:
                handleFilterHeaderClick();
                break;
            case R.id.sort_by_header:
                handleSortHeaderClick();
                break;
        }
    }

    private void showSplitCardView() {
        filterLayout.setVisibility(View.VISIBLE);
        sortLayout.setVisibility(View.GONE);
        splitCardView.setVisibility(View.GONE);
    }

    private void hideSplitCardView() {
        filterLayout.setVisibility(View.VISIBLE);
        sortLayout.setVisibility(View.GONE);
        splitCardView.setVisibility(View.VISIBLE);
    }

    private void handleFilterHeaderClick() {
        if (filterLayout.getVisibility() == View.VISIBLE) {
            filterLayout.setVisibility(View.GONE);
        } else {
            filterLayout.setVisibility(View.VISIBLE);
        }
        if (sortLayout.getVisibility() == View.VISIBLE) {
            sortLayout.setVisibility(View.GONE);
        }
    }

    private void handleSortHeaderClick() {
        if (sortLayout.getVisibility() == View.VISIBLE) {
            sortLayout.setVisibility(View.GONE);
        } else {
            sortLayout.setVisibility(View.VISIBLE);
        }
        if (filterLayout.getVisibility() == View.VISIBLE) {
            filterLayout.setVisibility(View.GONE);
        }
    }

}
