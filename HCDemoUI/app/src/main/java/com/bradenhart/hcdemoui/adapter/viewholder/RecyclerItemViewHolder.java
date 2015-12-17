package com.bradenhart.hcdemoui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.database.Challenge;

/**
 * Created by bradenhart on 3/12/15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private ViewExpandedListener mExpandListener;
    private final TextView titleTV, descriptionTV, smGroupIcon, smDoneIcon;
    private final TextView lgGroupIcon, minTV, maxTV, lgDoneIcon;
    private final RelativeLayout topView, bottomView;
    private Challenge challenge;
    private Integer min, max;

    public RecyclerItemViewHolder(final View parent, RelativeLayout topView, RelativeLayout bottomView) {
        super(parent);
        this.context = parent.getContext();
        this.topView = topView;
        this.bottomView = bottomView;
        this.titleTV = (TextView) topView.findViewById(R.id.item_title_textview);
        this.descriptionTV = (TextView) topView.findViewById(R.id.item_description_textview);
        this.smGroupIcon = (TextView) topView.findViewById(R.id.recycler_item_small_group);
        this.smDoneIcon = (TextView) topView.findViewById(R.id.recycler_item_small_done);
        this.lgGroupIcon = (TextView) bottomView.findViewById(R.id.challenge_info_group_size);
        this.lgDoneIcon = (TextView) bottomView.findViewById(R.id.challenge_info_completed);
        this.minTV = (TextView) bottomView.findViewById(R.id.min_size_description);
        this.maxTV = (TextView) bottomView.findViewById(R.id.max_size_description);
        parent.setOnClickListener(this);
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        RelativeLayout topView = (RelativeLayout) parent.findViewById(R.id.short_recycler_item);
        RelativeLayout bottomView = (RelativeLayout) parent.findViewById(R.id.long_recycler_item);
        return new RecyclerItemViewHolder(parent, topView, bottomView);
    }

    public void setItemText(Challenge item) {
        this.challenge = item;
        titleTV.setText(item.getName());
        descriptionTV.setText(item.getDescription());
        this.min = item.getGroupMin();
        this.max = item.getGroupMax();
        String groupStr = this.min + "-" + this.max;
        smGroupIcon.setText(groupStr);
        if (!item.getCompleted()) {
            smDoneIcon.setVisibility(View.GONE);
        }


    }

    public void setViewExpandListener(ViewExpandedListener expandListener) {
        this.mExpandListener = expandListener;
    }

    public interface ViewExpandedListener {
        void onExpand(View toExpandView);
        void onCollapse();
        View getExpandedView();
    }

    @Override
    public void onClick(View v) {
        boolean aViewIsExpanded = mExpandListener.getExpandedView() != null; // true means a view is expanded
        // EXPANDED -> onCollapse
        if (bottomView.getVisibility() == View.VISIBLE) {
            collapseCardView();
            mExpandListener.onCollapse();
        } else { // COLLAPSED -> onExpand
            if (aViewIsExpanded) {
                collapseExpandedCardView(mExpandListener.getExpandedView());
            }
            expandCardView();
            mExpandListener.onExpand(v);
        }

//        Intent intent = new Intent(context, ChallengeInfoActivity.class);
//        context.startActivity(intent);
    }

    /*
        descriptionTV ellipsize = none
        descriptionTV singleLine = false
        bottomView visibility = View.VISIBLE
        hide top icons
     */
    private void expandCardView() {
        String groupStr = challenge.getGroupMin() + " - " + challenge.getGroupMax();
        this.lgGroupIcon.setText(groupStr);
        String minStr = min > 1 ? "min: " + min + " people" : "min: " + min + "person";
        String maxStr = max > 1 ? "max: " + max + " people" : "max: " + max + "person";
        this.minTV.setText(minStr);
        this.maxTV.setText(maxStr);
        this.descriptionTV.setEllipsize(null);
        this.descriptionTV.setSingleLine(false);
        this.bottomView.setVisibility(View.VISIBLE);
        this.smGroupIcon.setVisibility(View.INVISIBLE);
        this.smDoneIcon.setVisibility(View.INVISIBLE);
        if (challenge.getCompleted()) {
            this.lgDoneIcon.setVisibility(View.VISIBLE);
        } else {
            this.lgDoneIcon.setVisibility(View.INVISIBLE);
        }
    }

    /*
        descriptionTV ellipsize = end
        descriptionTV singleLine = true
        bottomView visibility = View.GONE
        show top icons
     */
    private void collapseCardView() {
        this.descriptionTV.setEllipsize(TextUtils.TruncateAt.END);
        this.descriptionTV.setSingleLine(true);
        this.bottomView.setVisibility(View.GONE);
        this.smGroupIcon.setVisibility(View.VISIBLE);
        this.smDoneIcon.setVisibility(View.VISIBLE);
        if (challenge.getCompleted()) {
            this.lgDoneIcon.setVisibility(View.VISIBLE);
        } else {
            this.lgDoneIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void collapseExpandedCardView(View view) {
        TextView descriptionTextView = (TextView) view.findViewById(R.id.item_description_textview);
        RelativeLayout bottomView = (RelativeLayout) view.findViewById(R.id.long_recycler_item);
        TextView smallGroupImageView = (TextView) view.findViewById(R.id.recycler_item_small_group);
        TextView smallDoneImageView = (TextView) view.findViewById(R.id.recycler_item_small_done);
        descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        descriptionTextView.setSingleLine(true);
        bottomView.setVisibility(View.GONE);
        smallGroupImageView.setVisibility(View.VISIBLE);
        smallDoneImageView.setVisibility(View.VISIBLE);

    }
}
