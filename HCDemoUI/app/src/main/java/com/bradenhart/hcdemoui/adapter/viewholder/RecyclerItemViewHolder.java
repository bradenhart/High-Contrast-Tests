package com.bradenhart.hcdemoui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;

/**
 * Created by bradenhart on 3/12/15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private ViewExpandedListener mExpandListener;
    private final TextView titleTextView, descriptionTextView, smallGroupImageView, smallDoneImageView;
    private final RelativeLayout topView, bottomView;

    public RecyclerItemViewHolder(final View parent, RelativeLayout topView, RelativeLayout bottomView) {
        super(parent);
        this.context = parent.getContext();
        this.titleTextView = (TextView) topView.findViewById(R.id.item_title_textview);
        this.descriptionTextView = (TextView) topView.findViewById(R.id.item_description_textview);
        this.smallGroupImageView = (TextView) topView.findViewById(R.id.recycler_item_small_group);
        this.smallDoneImageView = (TextView) topView.findViewById(R.id.recycler_item_small_done);
        this.topView = topView;
        this.bottomView = bottomView;
        parent.setOnClickListener(this);
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        RelativeLayout topView = (RelativeLayout) parent.findViewById(R.id.short_recycler_item);
        RelativeLayout bottomView = (RelativeLayout) parent.findViewById(R.id.long_recycler_item);
        return new RecyclerItemViewHolder(parent, topView, bottomView);
    }

    public void setItemText(CharSequence text) {
        titleTextView.setText(text);
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
        descriptionTextView ellipsize = none
        descriptionTextView singleLine = false
        bottomView visibility = View.VISIBLE
        hide top icons
     */
    private void expandCardView() {
        this.descriptionTextView.setEllipsize(null);
        this.descriptionTextView.setSingleLine(false);
        this.bottomView.setVisibility(View.VISIBLE);
        this.smallGroupImageView.setVisibility(View.INVISIBLE);
        this.smallDoneImageView.setVisibility(View.INVISIBLE);
    }

    /*
        descriptionTextView ellipsize = end
        descriptionTextView singleLine = true
        bottomView visibility = View.GONE
        show top icons
     */
    private void collapseCardView() {
        this.descriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.descriptionTextView.setSingleLine(true);
        this.bottomView.setVisibility(View.GONE);
        this.smallGroupImageView.setVisibility(View.VISIBLE);
        this.smallDoneImageView.setVisibility(View.VISIBLE);
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
