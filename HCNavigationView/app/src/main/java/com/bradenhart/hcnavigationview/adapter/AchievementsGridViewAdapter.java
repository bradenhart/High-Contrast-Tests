package com.bradenhart.hcnavigationview.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.Achievement;
import com.bradenhart.hcnavigationview.R;

import java.util.ArrayList;

/**
 * Created by bradenhart on 24/06/15.
 */
public class AchievementsGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Achievement> achievementList;

    public AchievementsGridViewAdapter(Context context, ArrayList<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @Override
    public int getCount() {
        return achievementList.size();
    }

    @Override
    public Object getItem(int i) {
        return achievementList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = mInflater.inflate(R.layout.achievement_gridview_item, null);
        }

        int imageRes = achievementList.get(i).getImageId();

        TextView title = (TextView) view.findViewById(R.id.badge_title);
        title.setText(achievementList.get(i).getTitle());

        ImageView badgeImg = (ImageView) view.findViewById(R.id.badge_image);
        badgeImg.setImageResource(imageRes);

        return view;
    }
}
