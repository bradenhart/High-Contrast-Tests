package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.Achievement;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.adapter.AchievementsGridViewAdapter;

import java.util.ArrayList;

public class AchievementsFragment extends Fragment {

    private Context context;
    private final String LOGTAG = "AchievementsFragment";

    private GridView gridView;
    private ArrayList<Achievement> achievementList = new ArrayList<>();
    private AchievementsGridViewAdapter adapter;
    private int[] imageIds = {R.drawable.badge1,
                                R.drawable.badge2,
                                R.drawable.badge3,
                                R.drawable.badge4,
                                R.drawable.badge5,
                                R.drawable.badge6,
                                R.drawable.badge7,
                                R.drawable.badge8,
                                R.drawable.badge9,
                                R.drawable.badge10,
                                R.drawable.badge11,
                                R.drawable.badge12,
                                R.drawable.badge13
                            };
    private TextView unlockCount;

    public AchievementsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        context = getActivity();
        gridView = (GridView) view.findViewById(R.id.gridview_achievements);

        for (int i = 0; i < imageIds.length; i++) {
            achievementList.add(new Achievement("Badge " + (i+1), imageIds[i], true));
        }

        adapter = new AchievementsGridViewAdapter(context, achievementList);
        gridView.setAdapter(adapter);

        unlockCount = (TextView) view.findViewById(R.id.unlocked_count);
        unlockCount.setText(adapter.getCount() + " Unlocked");

        return view;
    }

}
