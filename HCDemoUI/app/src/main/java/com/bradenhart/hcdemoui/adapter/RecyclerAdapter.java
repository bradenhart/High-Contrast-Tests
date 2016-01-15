package com.bradenhart.hcdemoui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.viewholder.RecyclerItemViewHolder;
import com.bradenhart.hcdemoui.database.Challenge;

import java.util.List;

/**
 * Created by bradenhart on 3/12/15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final String LOGTAG = "RecyclerAdapter";
    private List<Challenge> mItemList;
    private RecyclerItemViewHolder holder;
    private RecyclerItemViewHolder.ViewExpandedListener mExpandListener;

    // Constructor
    public RecyclerAdapter(List<Challenge> itemList) {
        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_dark, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (RecyclerItemViewHolder) viewHolder;
        holder.setViewExpandListener(mExpandListener);
        Challenge item = mItemList.get(position);
        holder.setItemText(item, position);
        holder.collapseCardView();
    }

    public void setExpandListener(RecyclerItemViewHolder.ViewExpandedListener expandListener) {
        this.mExpandListener = expandListener;
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void updateList(List<Challenge> list) {
        this.mItemList.clear();
        this.mItemList = list;
    }

//    public void updateList(Challenge challenge) {
//        mItemList.set
//    }

    public int getListSize() {
        return mItemList.size();
    }

    public void removeItemAt(int position) {
        mItemList.remove(position);
        notifyItemRemoved(0);
//        notifyItemRangeChanged(0, mItemList.size());
    }

    public void addItem(Challenge challenge) {
        mItemList.add(challenge);
        notifyItemInserted(0);

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

}
