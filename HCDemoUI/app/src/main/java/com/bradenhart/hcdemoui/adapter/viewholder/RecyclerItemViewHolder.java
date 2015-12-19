package com.bradenhart.hcdemoui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
    private TextView titleTv, descriptionTV, minTV, maxTV, groupV, completedV;
    private Button doItBtn;
    private RelativeLayout expandedView;

    private Challenge challenge;
    private Integer min, max;

    public RecyclerItemViewHolder(final View parent) {
        super(parent);
        this.context = parent.getContext();
        this.titleTv = (TextView) parent.findViewById(R.id.item_title_textview);
        this.descriptionTV = (TextView) parent.findViewById(R.id.item_description_textview);
        this.minTV = (TextView) parent.findViewById(R.id.item_min_textview);
        this.maxTV = (TextView) parent.findViewById(R.id.item_max_textview);
        this.groupV = (TextView) parent.findViewById(R.id.item_group_icon);
        this.completedV = (TextView) parent.findViewById(R.id.item_completed_icon);
        this.doItBtn = (Button) parent.findViewById(R.id.do_it_button);
        this.expandedView = (RelativeLayout) parent.findViewById(R.id.item_expanded_view);
        parent.setOnClickListener(this);
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        return new RecyclerItemViewHolder(parent);
    }

    public void setItemText(Challenge item) {
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
        if (item.getCompleted()) {
            completedV.setVisibility(View.VISIBLE);
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
        if (expandedView.getVisibility() == View.VISIBLE) {
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
        expandedView visibility = View.VISIBLE
     */
    private void expandCardView() {
        descriptionTV.setEllipsize(null);
        descriptionTV.setSingleLine(false);
        expandedView.setVisibility(View.VISIBLE);
    }

    /*
        descriptionTV ellipsize = end
        descriptionTV singleLine = true
        expandedView visibility = View.GONE
     */
    private void collapseCardView() {
        descriptionTV.setEllipsize(TextUtils.TruncateAt.END);
        descriptionTV.setSingleLine(true);
        expandedView.setVisibility(View.GONE);
    }

    private void collapseExpandedCardView(View view) {
        TextView descriptionTextView = (TextView) view.findViewById(R.id.item_description_textview);
        RelativeLayout expandedView = (RelativeLayout) view.findViewById(R.id.item_expanded_view);
        descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        descriptionTextView.setSingleLine(true);
        expandedView.setVisibility(View.GONE);
    }
}
