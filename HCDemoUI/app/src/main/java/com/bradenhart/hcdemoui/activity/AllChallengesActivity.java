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
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradenhart on 1/12/15.
 */
public class AllChallengesActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton filterFab;
    private CardView filterCard;
    private RecyclerView recyclerView;
    private Button fNewestBtn, fCompletedBtn, fUncompletedBtn, fDifficultyEHBtn, fDifficultyHEBtn;
    private TextView headerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        Toolbar toolbar = (Toolbar) findViewById(R.id.all_challenges_toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.title_challenges));

        headerTab = (TextView) findViewById(R.id.challenges_header_tab);
        headerTab.setText(getResources().getString(R.string.filter_card_newest));

        filterFab = (FloatingActionButton) findViewById(R.id.filter_challenges_btn);
        filterFab.setOnClickListener(this);

        filterCard = (CardView) findViewById(R.id.filter_card);

        initFilterButtons();

        recyclerView = (RecyclerView) findViewById(R.id.challenges_recyclerview);
        setupRecyclerView(recyclerView);

        filterFab.attachToRecyclerView(recyclerView);

    }

    private void initFilterButtons() {
        fNewestBtn = (Button) filterCard.findViewById(R.id.filter_by_newest);
        fCompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_completed);
        fUncompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_uncompleted);
        fDifficultyEHBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_eh);
        fDifficultyHEBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_he);

        fNewestBtn.setOnClickListener(new FilterClickListener(fNewestBtn, headerTab));
        fCompletedBtn.setOnClickListener(new FilterClickListener(fCompletedBtn, headerTab));
        fUncompletedBtn.setOnClickListener(new FilterClickListener(fUncompletedBtn, headerTab));
        fDifficultyEHBtn.setOnClickListener(new FilterClickListener(fDifficultyEHBtn, headerTab));
        fDifficultyHEBtn.setOnClickListener(new FilterClickListener(fDifficultyHEBtn, headerTab));
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
            case R.id.filter_challenges_btn:
//                Toast.makeText(this, "Filtering... ", Toast.LENGTH_SHORT).show();
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

    private class FilterClickListener implements View.OnClickListener {

        private View view;
        private TextView header;

        public FilterClickListener(View view, TextView header) {
            this.view = view;
            this.header = header;
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.filter_by_newest:
                    header.setText(getResources().getString(R.string.filter_card_newest));
                    break;
                case R.id.filter_by_completed:
                    header.setText(getResources().getString(R.string.filter_card_completed));
                    break;
                case R.id.filter_by_uncompleted:
                    header.setText(getResources().getString(R.string.filter_card_uncompleted));
                    break;
                case R.id.filter_by_difficulty_eh:
                    header.setText(getResources().getString(R.string.filter_card_difficulty_eh));
                    break;
                case R.id.filter_by_difficulty_he:
                    header.setText(getResources().getString(R.string.filter_card_difficulty_he));
                    break;
                default:
                    break;
            }

            filterCard.setVisibility(View.GONE);

        }
    }
}
