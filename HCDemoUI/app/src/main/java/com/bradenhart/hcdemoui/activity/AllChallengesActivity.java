package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import static com.bradenhart.hcdemoui.Utils.*;
import com.bradenhart.hcdemoui.adapter.RecyclerAdapter;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;
import com.bradenhart.hcdemoui.database.Challenge;
import com.bradenhart.hcdemoui.database.DatabaseHelper;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bradenhart on 1/12/15.
 */
public class AllChallengesActivity extends BaseActivity implements View.OnClickListener, RecyclerItemViewHolder.ViewExpandedListener {

    private final String LOGTAG = "AllChallengesActivity";

    private FloatingActionButton filterFab;
    private CardView filterCard;
    private RecyclerView recyclerView;
    private Button fNewestBtn, fCompletedBtn, fUncompletedBtn, fDifficultyEHBtn, fDifficultyHEBtn;
    private View expandedView = null, transparentView;
    private TextView headerBar;
    private SharedPreferences sp;
    private DatabaseHelper dbHelper;
    private final String KEY_FILTER_VISIBILITY = "filter_visibility";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        sp = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        dbHelper = DatabaseHelper.getInstance(this);

        updateCheckedDrawerItem(R.id.nav_challenges);

        headerBar = (TextView) findViewById(R.id.base_header_bar);
        headerBar.setText(getResources().getString(R.string.filter_card_newest));

        filterFab = (FloatingActionButton) findViewById(R.id.base_fab);
        filterFab.setOnClickListener(this);

        filterCard = (CardView) findViewById(R.id.filter_card);
        transparentView = findViewById(R.id.transparent_view);
        transparentView.setOnClickListener(this);

        initFilterButtons();

        recyclerView = (RecyclerView) findViewById(R.id.challenges_recyclerview);
        setupRecyclerView(recyclerView);

        filterFab.attachToRecyclerView(recyclerView);

        if (savedInstanceState != null) {

            if (savedInstanceState.getInt(KEY_FILTER_VISIBILITY) == View.VISIBLE) {
                filterCard.setVisibility(View.VISIBLE);
            } else {
                filterCard.setVisibility(View.GONE);
            }
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_update) {
            handleUpdateChallengesRequest();
        }
        return true;

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

    @Override
    protected int[] setFabMargins() {
        // left, top, right, bottom
        int margin = (int) getResources().getDimension(R.dimen.fab_margin);
        return new int[] {0, 0, margin, margin};
    }

    @Override
    protected boolean useScrollingBehavior() {
        return true;
    }

    @Override
    protected boolean useUpdateButton() {
        return true;
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
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.base_fab:
                if (filterCard.getVisibility() == View.GONE) {
                    filterCard.setVisibility(View.VISIBLE);
                    transparentView.setVisibility(View.VISIBLE);
                } else {
                    filterCard.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                    recyclerView.setClickable(true);
                }
                break;
            case R.id.transparent_view:
                if (filterCard.getVisibility() == View.VISIBLE) {
                    filterCard.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                }
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

    private List<Challenge> createItemList() {
        return dbHelper.getChallengeWithFilter("Newest");
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
                    headerBar.setText(getResources().getString(R.string.filter_card_newest));
                    break;
                case R.id.filter_by_completed:
                    headerBar.setText(getResources().getString(R.string.filter_card_completed));
                    break;
                case R.id.filter_by_uncompleted:
                    headerBar.setText(getResources().getString(R.string.filter_card_uncompleted));
                    break;
                case R.id.filter_by_difficulty_eh:
                    headerBar.setText(getResources().getString(R.string.filter_card_difficulty_eh));
                    break;
                case R.id.filter_by_difficulty_he:
                    headerBar.setText(getResources().getString(R.string.filter_card_difficulty_he));
                    break;
                default:
                    break;
            }

            filterCard.setVisibility(View.GONE);

        }

    }

    private void handleUpdateChallengesRequest() {
        String dateStr = sp.getString(KEY_LAST_DATE, null);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");

        if (dateStr != null) {
            // a date has been saved before
            Date date = convertDateTimeToDate(dateStr);
            if (date != null) {
                // date string was converted to date successfully
                // add to query that we want data created after the saved date
                query.whereGreaterThan("createdAt", date);
            }
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // no error
                    Log.e(LOGTAG, "calling insertChallenges method");
                    dbHelper.insertChallengesToDb(objects);

                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_FILTER_VISIBILITY, filterCard.getVisibility());

    }
}
