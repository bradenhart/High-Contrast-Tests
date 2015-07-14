package com.bradenhart.hcnavigationview.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bradenhart.hcnavigationview.R;

/**
 * Created by bradenhart on 29/06/15.
 */
public class NewChallengeFragment extends Fragment {

    public NewChallengeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_challenge, container, false);

        return view;
    }
}
