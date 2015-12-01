package com.bradenhart.hcnavigationview.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bradenhart.hclib.domain.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.adapter.ChallengesListViewAdapter;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;
import static com.bradenhart.hcnavigationview.Constants.*;

import java.util.ArrayList;

/**
 * Created by bradenhart on 25/06/15.
 */
public class ChallengePageFragment extends Fragment {

    private ListView mListView;
    private ChallengesListViewAdapter mAdapter;
    private DatabaseHandler dbHandler;

    public ChallengePageFragment() {}

    public static ChallengePageFragment newInstance(String difficulty) {
        ChallengePageFragment fragment = new ChallengePageFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_DIFF, difficulty);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_challenge_page, container, false);
        dbHandler = DatabaseHandler.getInstance(getActivity());
        Bundle arguments = getArguments();
        ArrayList<Challenge> arrayList = dbHandler.getChallengesByDifficulty(arguments.getString(ARG_DIFF));

        mListView = (ListView) root.findViewById(R.id.challenges_listview);
        mAdapter = new ChallengesListViewAdapter(getActivity(), arrayList);
        mListView.setAdapter(mAdapter);

        return root;
    }
}
