package com.bradenhart.hcnavigationview.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.bradenhart.hcnavigationview.R;

/**
 * Created by bradenhart on 25/06/15.
 */
public class AppSettingsFragment extends Fragment {

    private final String LOGTAG = "SettingsActivity";

    public AppSettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        return view;
    }

}
