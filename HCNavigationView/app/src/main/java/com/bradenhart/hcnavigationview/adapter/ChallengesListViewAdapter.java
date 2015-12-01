package com.bradenhart.hcnavigationview.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bradenhart.hclib.domain.Challenge;
import com.bradenhart.hcnavigationview.R;
import static com.bradenhart.hcnavigationview.Constants.*;

import java.util.ArrayList;

/**
 * Created by bradenhart on 26/06/15.
 */
public class ChallengesListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Challenge> challengesList;

    public ChallengesListViewAdapter(Context context, ArrayList<Challenge> challengesList) {
        this.context = context;
        this.challengesList = challengesList;
    }

    @Override
    public int getCount() {
        return challengesList.size();
    }

    @Override
    public Object getItem(int i) {
        return challengesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.challenge_listview_item, null);
        }
        TextView title = (TextView) view.findViewById(R.id.challenge_item_title);
        title.setText(challengesList.get(i).getName());

        return view;
    }
}
