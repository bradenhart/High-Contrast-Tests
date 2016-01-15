package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.ScrollView;
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

import jp.wasabeef.recyclerview.animators.FlipInLeftYAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by bradenhart on 1/12/15.
 */
public class AllChallengesActivity extends BaseActivity implements View.OnClickListener, RecyclerItemViewHolder.ViewExpandedListener {

    private final String LOGTAG = "AllChallengesActivity";

    private FloatingActionButton filterFab;
    private RecyclerView recyclerView;
    private Button fCompleted, fUncompleted, fEasy, fMedium, fHard, fInsane, sNewest, sOldest, sDifficultyAsc, sDifficultyDesc;
    private View expandedView = null, transparentView;
    private TextView headerBar;
    private SharedPreferences sp;
    private DatabaseHelper dbHelper;
    private final String KEY_FILTER_VISIBILITY = "filter_visibility";
    //    private final String KEY_FILTER_TAB_SELECTED = "filter_tab_selected";
    private final String KEY_SORT_TAB_SELECTED = "sort_tab_selected";
    private final String KEY_FILTER_TERM = "filter_term";
    private Animation fabOutAnim, fabInAnim, spinAnim, slideDownAnim, slideUpAnim;
    private RecyclerAdapter recyclerAdapter;
    private RelativeLayout updateLayout;
    private ImageView updateIcon;
    private String filterTerm;

    private RelativeLayout filterCard;
    private ImageView filterTab, sortTab;
    private ScrollView filterLayout, sortLayout;
    private TextView cardHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        initViews();

        sp = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        dbHelper = DatabaseHelper.getInstance(this);

        updateCheckedDrawerItem(R.id.nav_challenges);

        initFilterButtons();

        restoreFromSavedInstance(savedInstanceState);

        setupRecyclerView();

        filterFab.attachToRecyclerView(recyclerView);

        initAnimations();

    }

    /**
     * initialisations
     */
    private void initViews() {
        headerBar = (TextView) findViewById(R.id.base_header_bar);
        headerBar.setText(getResources().getString(R.string.filter_card_newest));

        filterFab = (FloatingActionButton) findViewById(R.id.base_fab);
        filterFab.setOnClickListener(this);

        transparentView = findViewById(R.id.ac_transparent_view);
        transparentView.setOnClickListener(this);

        updateLayout = (RelativeLayout) findViewById(R.id.list_refresh_layout);
        updateIcon = (ImageView) findViewById(R.id.refresh_icon);

        filterCard = (RelativeLayout) findViewById(R.id.icon_card_view);
        filterTab = (ImageView) findViewById(R.id.filter_icon_tab);
        sortTab = (ImageView) findViewById(R.id.sort_icon_tab);
        filterLayout = (ScrollView) findViewById(R.id.filter_layout);
        sortLayout = (ScrollView) findViewById(R.id.sort_layout);
        cardHeader = (TextView) findViewById(R.id.icon_card_header);

        filterTab.setOnClickListener(this);
        sortTab.setOnClickListener(this);
    }

    private void initFilterButtons() {
        fCompleted = (Button) filterCard.findViewById(R.id.filter_by_completed);
        fUncompleted = (Button) filterCard.findViewById(R.id.filter_by_uncompleted);
        fEasy = (Button) filterCard.findViewById(R.id.filter_by_easy);
        fMedium = (Button) filterCard.findViewById(R.id.filter_by_medium);
        fHard = (Button) filterCard.findViewById(R.id.filter_by_hard);
        fInsane = (Button) filterCard.findViewById(R.id.filter_by_insane);

        sNewest = (Button) filterCard.findViewById(R.id.sort_by_newest);
        sOldest = (Button) filterCard.findViewById(R.id.sort_by_oldest);
        sDifficultyAsc = (Button) filterCard.findViewById(R.id.sort_by_difficulty_asc);
        sDifficultyDesc = (Button) filterCard.findViewById(R.id.sort_by_difficulty_desc);

        fCompleted.setOnClickListener(new FilterClickListener());
        fUncompleted.setOnClickListener(new FilterClickListener());
        fEasy.setOnClickListener(new FilterClickListener());
        fMedium.setOnClickListener(new FilterClickListener());
        fHard.setOnClickListener(new FilterClickListener());
        fInsane.setOnClickListener(new FilterClickListener());

        sNewest.setOnClickListener(new FilterClickListener());
        sOldest.setOnClickListener(new FilterClickListener());
        sDifficultyAsc.setOnClickListener(new FilterClickListener());
        sDifficultyDesc.setOnClickListener(new FilterClickListener());
    }

    private void initAnimations() {
        fabOutAnim = AnimationUtils.loadAnimation(this, R.anim.anim_translate_fab_out);
        fabInAnim = AnimationUtils.loadAnimation(this, R.anim.anim_translate_fab_in);
        spinAnim = AnimationUtils.loadAnimation(this, R.anim.anim_spin);
        slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down);
        slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up);
    }

    /**
     * update animations
     */
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

    /**
     * filter menu methods
     */
    private void openFilterCard() {
        filterCard.setVisibility(View.VISIBLE);
        showFilterLayout();
        transparentView.setVisibility(View.VISIBLE);
    }

    private void closeFilterCard() {
        filterCard.setVisibility(View.GONE);
        hideFilterLayout();
        hideSortLayout();
        transparentView.setVisibility(View.GONE);
    }

    private void showFilterLayout() {
        if (dbHelper.getCompletedChallengeCount() == 0) {
            fCompleted.setEnabled(false);
        }
        if (sortTab.isSelected()) {
            sortTab.setSelected(false);
        }
        filterTab.setSelected(true);
        filterLayout.setVisibility(View.VISIBLE);
        cardHeader.setText("Filter");
    }

    private void showSortLayout() {
        hideFilterLayout();
        sortTab.setSelected(true);
        sortLayout.setVisibility(View.VISIBLE);
        cardHeader.setText("Sort");
    }

    private void hideFilterLayout() {
        filterTab.setSelected(false);
        filterLayout.setVisibility(View.GONE);
    }

    private void hideSortLayout() {
        sortTab.setSelected(false);
        sortLayout.setVisibility(View.GONE);
    }

    /**
     * set up
     */
    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.challenges_recyclerview);
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration
//        recyclerView.addItemDecoration(new SimpleItemDecoration(getApplicationContext()));
//        recyclerView.setItemAnimator(new SlideInLeftAnimator());

//        ScaleInLeftAnimator animator = new ScaleInLeftAnimator();
//        LandingAnimator animator = new LandingAnimator();
        OvershootInLeftAnimator animator = new OvershootInLeftAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(200);

        recyclerView.setItemAnimator(animator);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(createItemList(filterTerm));
        recyclerAdapter.setExpandListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<Challenge> createItemList(String term) {
        return dbHelper.getChallengesWithFilter(term);
    }

    /**
     * update methods
     */
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
        int listSize = recyclerAdapter.getItemCount();
        Log.e(LOGTAG, "size: " + listSize);
        for (int i = 0; i < listSize; i++) {
            recyclerAdapter.removeItemAt(0);
        }
        listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            recyclerAdapter.addItem(list.get(i));
        }
    }

    private void testUpdateRecyclerView() {
//        List<Challenge> newList = createItemList(filterTerm);
        // (remove each item from the recyclerview)
        // remove an item
        // notify the adapter of removed item

        // get a new list of items
        // (add each item to the recyclerview)
        // add an item
        // notify the adapter of the added item
    }

    /**
     * restore methods
     */
    private void restoreFromSavedInstance(Bundle bundle) {
        if (bundle != null) {

            if (bundle.getInt(KEY_FILTER_VISIBILITY) == View.VISIBLE) {
                openFilterCard();
                if (bundle.getBoolean(KEY_SORT_TAB_SELECTED)) {
                    showSortLayout();
                }
            } else {
                closeFilterCard();
            }

            if (bundle.containsKey(KEY_FILTER_TERM)) {
                filterTerm = bundle.getString(KEY_FILTER_TERM);
            }

        } else {
            filterTerm = NEWEST;
        }
    }

    /**
     * overridden methods
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FILTER_VISIBILITY, filterCard.getVisibility());
        outState.putString(KEY_FILTER_TERM, filterTerm);
        outState.putBoolean(KEY_SORT_TAB_SELECTED, sortTab.isSelected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_update) {
            showListUpdateAnimation();
            handleUpdateChallengesRequest();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ChallengeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_slide_to_bottom);
    }

    // recyclerview interface implementation
    @Override
    public void onExpand(View toExpandView, int position) {
        expandedView = toExpandView;
//        filterFab.startAnimation(fabOutAnim);
//        filterFab.setVisibility(View.GONE);
    }

    @Override
    public void onCollapse() {
        expandedView = null;

//        filterFab.startAnimation(fabInAnim);
//        filterFab.setVisibility(View.VISIBLE);
    }

    // parent methods
    @Override
    public View getExpandedView() {
        return expandedView;
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
        return new int[]{0, 0, margin, margin};
    }

    @Override
    protected boolean useScrollingBehavior() {
        return true;
    }

    @Override
    protected boolean useUpdateButton() {
        return true;
    }

    // click interface implementation
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.base_fab:
                if (filterCard.getVisibility() == View.VISIBLE) {
                    closeFilterCard();
                } else {
                    openFilterCard();
                }
                break;
            case R.id.ac_transparent_view:
                closeFilterCard();
            case R.id.filter_icon_tab:
                if (!filterTab.isSelected()) {
                    hideSortLayout();
                    showFilterLayout();
                }
                break;
            case R.id.sort_icon_tab:
                if (!sortTab.isSelected()) {
                    hideFilterLayout();
                    showSortLayout();
                }
                break;
            default:
                break;
        }

    }

    /**
     * private classes
     */
    private class FilterClickListener implements View.OnClickListener {

        public FilterClickListener() {
        }

        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.filter_by_completed:
                    filterTerm = COMPLETED;
                    break;
                case R.id.filter_by_uncompleted:
                    filterTerm = UNCOMPLETED;
                    break;
                case R.id.filter_by_easy:
                    filterTerm = EASY;
                    break;
                case R.id.filter_by_medium:
                    filterTerm = MEDIUM;
                    break;
                case R.id.filter_by_hard:
                    filterTerm = HARD;
                    break;
                case R.id.filter_by_insane:
                    filterTerm = INSANE;
                    break;
                case R.id.sort_by_newest:
                    filterTerm = NEWEST;
                    break;
                case R.id.sort_by_oldest:
                    filterTerm = OLDEST;
                    break;
                case R.id.sort_by_difficulty_asc:
                    filterTerm = DIFFICULTY_ASC;
                    break;
                case R.id.sort_by_difficulty_desc:
                    filterTerm = DIFFICULTY_DESC;
                    break;
                default:
                    break;
            }

            updateRecyclerView();
            closeFilterCard();
        }

    }

}
