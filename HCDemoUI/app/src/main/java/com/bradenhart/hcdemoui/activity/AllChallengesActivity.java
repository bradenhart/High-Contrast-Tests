package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button fNewestBtn, fOldestBtn, fCompletedBtn, fUncompletedBtn, fDifficultyEHBtn, fDifficultyHEBtn;
    private View expandedView = null, transparentView;
    private TextView headerBar;
    private SharedPreferences sp;
    private DatabaseHelper dbHelper;
    private final String KEY_FILTER_VISIBILITY = "filter_visibility";
    private final String KEY_FILTER_TERM = "filter_term";
    private Animation fabOutAnim, fabInAnim, spinAnim, slideDownAnim, slideUpAnim;
    private RecyclerAdapter recyclerAdapter;
    private RelativeLayout updateLayout;
    private ImageView updateIcon;
    private String filterTerm;

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

        updateLayout = (RelativeLayout) findViewById(R.id.list_refresh_layout);
        updateIcon = (ImageView) findViewById(R.id.refresh_icon);

        initFilterButtons();

        if (savedInstanceState != null) {

            if (savedInstanceState.getInt(KEY_FILTER_VISIBILITY) == View.VISIBLE) {
                filterCard.setVisibility(View.VISIBLE);
            } else {
                filterCard.setVisibility(View.GONE);
            }

            if (savedInstanceState.containsKey(KEY_FILTER_TERM)) {
                filterTerm = savedInstanceState.getString(KEY_FILTER_TERM);
            }

        } else {
            filterTerm = NEWEST;
        }

        recyclerView = (RecyclerView) findViewById(R.id.challenges_recyclerview);
        setupRecyclerView(recyclerView);

        filterFab.attachToRecyclerView(recyclerView);

        fabOutAnim = AnimationUtils.loadAnimation(this, R.anim.anim_translate_fab_out);
        fabInAnim = AnimationUtils.loadAnimation(this, R.anim.anim_translate_fab_in);
        spinAnim = AnimationUtils.loadAnimation(this, R.anim.anim_spin);
        slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down);
        slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_update) {
            showListUpdateAnimation();
            handleUpdateChallengesRequest();
            //hideListUpdateAnimation();
        }
        return true;

    }

    private void showListUpdateAnimation() {
        if (updateLayout != null && updateIcon != null) {
            if (updateLayout.getVisibility() == View.GONE) {
                updateLayout.startAnimation(slideDownAnim);
                updateLayout.setVisibility(View.VISIBLE);
                updateIcon.startAnimation(spinAnim);
            }
        }
    }

    private void hideListUpdateAnimation() {
        if (updateLayout != null && updateIcon != null) {
            if (updateLayout.getVisibility() == View.VISIBLE) {
                spinAnim.cancel();
                spinAnim.reset();
                updateLayout.startAnimation(slideUpAnim);
                updateLayout.setVisibility(View.GONE);
            }
        }
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
        fOldestBtn = (Button) filterCard.findViewById(R.id.filter_by_oldest);
        fCompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_completed);
        fUncompletedBtn = (Button) filterCard.findViewById(R.id.filter_by_uncompleted);
        fDifficultyEHBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_eh);
        fDifficultyHEBtn = (Button) filterCard.findViewById(R.id.filter_by_difficulty_he);

        fNewestBtn.setOnClickListener(new FilterClickListener());
        fOldestBtn.setOnClickListener(new FilterClickListener());
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
        recyclerAdapter = new RecyclerAdapter(createItemList(filterTerm));
        recyclerAdapter.setExpandListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<Challenge> createItemList(String term) {
        return dbHelper.getChallengesWithFilter(term);
    }

    @Override
    public void onExpand(View toExpandView) {
        expandedView = toExpandView;
        filterFab.startAnimation(fabOutAnim);
        filterFab.setVisibility(View.GONE);
    }

    @Override
    public void onCollapse() {
        expandedView = null;
        filterFab.startAnimation(fabInAnim);
        filterFab.setVisibility(View.VISIBLE);
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
                    filterTerm = NEWEST;
                    updateRecyclerView();
                    break;
                case R.id.filter_by_oldest:
                    filterTerm = OLDEST;
                    updateRecyclerView();
                    break;
                case R.id.filter_by_completed:
                    filterTerm = COMPLETED;
                    updateRecyclerView();
                    break;
                case R.id.filter_by_uncompleted:
                    filterTerm = UNCOMPLETED;
                    updateRecyclerView();
                    break;
                case R.id.filter_by_difficulty_eh:
                    filterTerm = DIFFICULTY_E_H;
                    updateRecyclerView();
                    break;
                case R.id.filter_by_difficulty_he:
                    filterTerm = DIFFICULTY_H_E;
                    updateRecyclerView();
                    break;
                default:
                    break;
            }

            filterCard.setVisibility(View.GONE);
            transparentView.setVisibility(View.GONE);

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
                Log.e(LOGTAG, "date not null, handleUpdateChallengesRequest");
                query.whereGreaterThan("createdAt", date);
            } else {
                Log.e(LOGTAG, "date is null");
            }
        } else {
            Log.e(LOGTAG, "date string is null");
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // no error
                    Log.e(LOGTAG, "calling insertChallenges method");
                    Log.e(LOGTAG, "found " + objects.size() + " new challenges.");
                    if (objects.size() > 0) {
                        dbHelper.insertChallengesToDb(objects);
                        updateRecyclerView();
                    } else {
                        Toast.makeText(AllChallengesActivity.this, "No new challenges found.", Toast.LENGTH_SHORT).show();
                    }
                    hideListUpdateAnimation();
                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }
        });
    }

    private void updateRecyclerView() {
        headerBar.setText(filterTerm);
        List<Challenge> list = createItemList(filterTerm);
        recyclerAdapter.updateList(list);
        recyclerAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_FILTER_VISIBILITY, filterCard.getVisibility());

    }
}
