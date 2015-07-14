package com.bradenhart.hcnavigationview.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by bradenhart on 26/06/15.
 */
public class ChallengesListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> jsonList;
    private ArrayList<Challenge> challengesList = new ArrayList<>();
    private Gson gson = new Gson();

    public ChallengesListViewAdapter(Context context, ArrayList<String> jsonList) {
        this.context = context;
        this.jsonList = jsonList;
        int size = jsonList.size();
        for (int i = 0; i < size; i++) {
            Challenge challenge = gson.fromJson(jsonList.get(i), Challenge.class);
            challengesList.add(challenge);
        }

    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public Object getItem(int i) {
        return gson.fromJson(jsonList.get(i), Challenge.class);
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
        title.setText(challengesList.get(i).getTitle());

        return view;
    }
}
