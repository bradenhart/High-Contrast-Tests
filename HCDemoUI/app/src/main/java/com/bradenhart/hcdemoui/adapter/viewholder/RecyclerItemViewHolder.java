package com.bradenhart.hcdemoui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.Utils;
import com.bradenhart.hcdemoui.activity.ChallengeActivity;
import com.bradenhart.hcdemoui.database.Challenge;

/**
 * Created by bradenhart on 3/12/15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private View parent;
    private ViewExpandedListener mExpandListener;
    private TextView titleTv, descriptionTV, minTV, maxTV, groupV, difficultyTV, completedTV;
    private RelativeLayout expandedView, clickableLayout;
    private ImageView playBtn;

    private Challenge challenge;
    private Integer min, max, position;

    public RecyclerItemViewHolder(final View parent) {
        super(parent);
        this.parent = parent;
        this.context = parent.getContext();
        this.titleTv = (TextView) parent.findViewById(R.id.item_title_textview);
        this.descriptionTV = (TextView) parent.findViewById(R.id.item_description_textview);
        this.minTV = (TextView) parent.findViewById(R.id.item_min_textview);
        this.maxTV = (TextView) parent.findViewById(R.id.item_max_textview);
        this.groupV = (TextView) parent.findViewById(R.id.item_group_icon);
        this.difficultyTV = (TextView) parent.findViewById(R.id.item_difficulty_textview);
        this.completedTV = (TextView) parent.findViewById(R.id.item_completed_textview);
        this.expandedView = (RelativeLayout) parent.findViewById(R.id.item_expanded_view);
        this.clickableLayout = (RelativeLayout) parent.findViewById(R.id.clickable_layout);
        this.playBtn = (ImageView) parent.findViewById(R.id.challenge_play_button);
        this.clickableLayout.setOnClickListener(this);
        this.playBtn.setOnClickListener(this);
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        return new RecyclerItemViewHolder(parent);
    }

    public void setItemText(Challenge item, int position) {
        this.position = position;
        collapseExpandedCardView();
        challenge = item;
        titleTv.setText(item.getName());
        descriptionTV.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed justo libero, semper id sem id, lacinia bibendum erat.");
        min = item.getGroupMin();
        max = item.getGroupMax();
        String groupStr = min + "-" + max;
        String minStr = min > 1 ? "min: " + min + " people" : "min: " + min + "person";
        String maxStr = max > 1 ? "max: " + max + " people" : "max: " + max + "person";
        groupV.setText(groupStr);
        minTV.setText(minStr);
        maxTV.setText(maxStr);
        difficultyTV.setText("Difficulty: " + item.getDifficulty());
        if (item.getCompleted()) {
            titleTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_green_24dp, 0);
            completedTV.setText("Completed: Yep");
        } else {
            completedTV.setText("Completed: Nope");
            titleTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blank_icon, 0);
        }
    }

    public void setViewExpandListener(ViewExpandedListener expandListener) {
        this.mExpandListener = expandListener;
    }

    public interface ViewExpandedListener {
        void onExpand(View toExpandView, int position);

        void onCollapse();

        View getExpandedView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.clickable_layout:
                boolean aViewIsExpanded = mExpandListener.getExpandedView() != null; // true means a view is expanded
                // EXPANDED -> onCollapse
                if (expandedView.getVisibility() == View.VISIBLE) {
                    collapseCardView();
                } else { // COLLAPSED -> onExpand
                    if (aViewIsExpanded) {
                        collapseExpandedCardView();
                    }
                    expandCardView();
                }
                break;
            case R.id.challenge_play_button:
                Intent playIntent = new Intent(context, ChallengeActivity.class);
                playIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                playIntent.putExtra(Utils.KEY_EXTRA_PLAY_ID, challenge.getObjectId());
                context.startActivity(playIntent);
                break;
        }
    }

    /*
        descriptionTV ellipsize = none
        descriptionTV singleLine = false
        expandedView visibility = View.VISIBLE
     */
    private void expandCardView() {
        descriptionTV.setEllipsize(null);
        descriptionTV.setSingleLine(false);
        expandedView.setVisibility(View.VISIBLE);
        mExpandListener.onExpand(clickableLayout, position);
    }

    /*
        descriptionTV ellipsize = end
        descriptionTV singleLine = true
        expandedView visibility = View.GONE
     */
    public void collapseCardView() {
        descriptionTV.setEllipsize(TextUtils.TruncateAt.END);
        descriptionTV.setSingleLine(true);
        expandedView.setVisibility(View.GONE);
        mExpandListener.onCollapse();
    }

    public void collapseExpandedCardView() {
        View view = mExpandListener.getExpandedView();
        if (view != null) {
            TextView descriptionTextView = (TextView) view.findViewById(R.id.item_description_textview);
            RelativeLayout expandedView = (RelativeLayout) view.findViewById(R.id.item_expanded_view);
            descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
            descriptionTextView.setSingleLine(true);
            expandedView.setVisibility(View.GONE);
            mExpandListener.onCollapse();
        }
    }

}
