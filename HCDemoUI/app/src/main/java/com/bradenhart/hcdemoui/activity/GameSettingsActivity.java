package com.bradenhart.hcdemoui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;

/**
 * Created by bradenhart on 4/12/15.
 */
public class GameSettingsActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout filterCard;
    private ImageView filterTab, sortTab;
    private ScrollView filterLayout, sortLayout;
    private TextView cardHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        updateCheckedDrawerItem(R.id.nav_settings);

        filterCard = (RelativeLayout) findViewById(R.id.icon_card_view);
        filterTab = (ImageView) findViewById(R.id.filter_icon_tab);
        sortTab = (ImageView) findViewById(R.id.sort_icon_tab);
        filterLayout = (ScrollView) findViewById(R.id.filter_layout);
        sortLayout = (ScrollView) findViewById(R.id.sort_layout);
        cardHeader = (TextView) findViewById(R.id.icon_card_header);

        filterTab.setSelected(true);

        filterTab.setOnClickListener(this);
        sortTab.setOnClickListener(this);

        findViewById(R.id.base_fab).setOnClickListener(this);

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
                if (filterCard.getVisibility() == View.VISIBLE) {
                    closeFilterCard();
                } else {
                    openFilterCard();
                }
                break;
            case R.id.filter_icon_tab:
                if (filterTab.isSelected()) {
//                    filterTab.setSelected(false);
//                    sortTab.setSelected(true);
                    hideFilterLayout();
                    showSortLayout();
                } else {
                    hideSortLayout();
                    showFilterLayout();
                }
                break;
            case R.id.sort_icon_tab:
                if (sortTab.isSelected()) {
//                    sortTab.setSelected(false);
//                    filterTab.setSelected(true);
                    hideSortLayout();
                    showFilterLayout();
                } else {
                    hideFilterLayout();
                    showSortLayout();
                }
                break;
        }
    }

    private void openFilterCard() {
        filterCard.setVisibility(View.VISIBLE);
        filterTab.setSelected(true);
        filterLayout.setVisibility(View.VISIBLE);
        sortLayout.setVisibility(View.GONE);
    }

    private void closeFilterCard() {
        filterCard.setVisibility(View.GONE);
    }

    private void showFilterLayout() {
        sortTab.setSelected(false);
        filterTab.setSelected(true);
        filterLayout.setVisibility(View.VISIBLE);
        cardHeader.setText("Filter");
    }

    private void showSortLayout() {
        filterTab.setSelected(false);
        sortTab.setSelected(true);
        sortLayout.setVisibility(View.VISIBLE);
        cardHeader.setText("Sort");
    }

    private void hideFilterLayout() {
        filterLayout.setVisibility(View.GONE);
    }

    private void hideSortLayout() {
        sortLayout.setVisibility(View.GONE);
    }

}
