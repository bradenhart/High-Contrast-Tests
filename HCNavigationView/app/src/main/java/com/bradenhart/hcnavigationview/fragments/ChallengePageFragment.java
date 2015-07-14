package com.bradenhart.hcnavigationview.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bradenhart.hcnavigationview.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.adapter.ChallengesListViewAdapter;

import java.util.ArrayList;

/**
 * Created by bradenhart on 25/06/15.
 */
public class ChallengePageFragment extends Fragment {

    private static final String ARG_HEADER = "header";
    private static final String ARG_LIST = "list";
    private ListView mListView;
    private ChallengesListViewAdapter mAdapter;

    public ChallengePageFragment() {}

    public static ChallengePageFragment newInstance(ArrayList<String> list) {
        ChallengePageFragment fragment = new ChallengePageFragment();

        Bundle arguments = new Bundle();
        arguments.putStringArrayList(ARG_LIST, list);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_challenge_page, container, false);
        Bundle arguments = getArguments();
        ArrayList<String> arrayList = arguments.getStringArrayList(ARG_LIST);
        mListView = (ListView) root.findViewById(R.id.challenges_listview);
        mAdapter = new ChallengesListViewAdapter(getActivity(), arrayList);
        mListView.setAdapter(mAdapter);

        return root;
    }
}
