package com.bradenhart.hcdemoui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.RecyclerAdapter;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradenhart on 1/12/15.
 */
public class AllChallengesActivity extends BaseActivity implements View.OnClickListener, RecyclerItemViewHolder.ViewExpandedListener {

    private FloatingActionButton filterFab;
    private CardView filterCard;
    private RecyclerView recyclerView;
    private Button fNewestBtn, fCompletedBtn, fUncompletedBtn, fDifficultyEHBtn, fDifficultyHEBtn;
    private View expandedView = null;
    private TextView headerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

//        headerTab = (TextView) findViewById(R.id.base_header_bar);
//        headerTab.setText(getResources().getString(R.string.filter_card_newest));

        filterFab = (FloatingActionButton) findViewById(R.id.base_fab);
        filterFab.setOnClickListener(this);

        filterCard = (CardView) findViewById(R.id.filter_card);


        initFilterButtons();

        recyclerView = (RecyclerView) findViewById(R.id.challenges_recyclerview);
        setupRecyclerView(recyclerView);

        filterFab.attachToRecyclerView(recyclerView);

    }

    @Override
    protected boolean useHeaderBar() {
        return true;
    }

    @Override
    protected boolean useFab() {
        return true;
    }

    @Override
    protected int setFabIcon() {
        return R.drawable.ic_filter_list_white_24dp;
    }

    private void initFilterButtons() {
        fNewestBtn = (Button) filterCard.findViewById(R.id.filter_by_newest);
        fCompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_completed);
        fUncompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_uncompleted);
        fDifficultyEHBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_eh);
        fDifficultyHEBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_he);

        fNewestBtn.setOnClickListener(new FilterClickListener());
        fCompletedBtn.setOnClickListener(new FilterClickListener());
        fUncompletedBtn.setOnClickListener(new FilterClickListener());
        fDifficultyEHBtn.setOnClickListener(new FilterClickListener());
        fDifficultyHEBtn.setOnClickListener(new FilterClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.base_fab:
                if (filterCard.getVisibility() == View.GONE) {
                    filterCard.setVisibility(View.VISIBLE);
                } else {
                    filterCard.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        recyclerAdapter.setExpandListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();

        int itemsCount = 20;
        for (int i = 1; i <= itemsCount; i++) {
            itemList.add("Item " + i);
        }

        return itemList;
    }

    @Override
    public void onExpand(View toExpandView) {
        expandedView = toExpandView;
    }

    @Override
    public void onCollapse() {
        expandedView = null;
    }

    @Override
    public View getExpandedView() {
        return expandedView;
    }

    private class FilterClickListener implements View.OnClickListener {

        public FilterClickListener() {
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.filter_by_newest:
                    headerTab.setText(getResources().getString(R.string.filter_card_newest));
                    break;
                case R.id.filter_by_completed:
                    headerTab.setText(getResources().getString(R.string.filter_card_completed));
                    break;
                case R.id.filter_by_uncompleted:
                    headerTab.setText(getResources().getString(R.string.filter_card_uncompleted));
                    break;
                case R.id.filter_by_difficulty_eh:
                    headerTab.setText(getResources().getString(R.string.filter_card_difficulty_eh));
                    break;
                case R.id.filter_by_difficulty_he:
                    headerTab.setText(getResources().getString(R.string.filter_card_difficulty_he));
                    break;
                default:
                    break;
            }

            filterCard.setVisibility(View.GONE);

        }

    }
}
