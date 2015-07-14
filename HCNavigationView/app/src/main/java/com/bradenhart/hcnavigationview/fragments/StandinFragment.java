package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bradenhart.hcnavigationview.R;

/**
 * Created by bradenhart on 2/07/15.
 */
public class StandinFragment extends Fragment implements View.OnClickListener{

    private final String LOGTAG = "StandinFragment class";
    private Context context;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private FragmentManager manager;

    public StandinFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_standin, container, false);
        context = getActivity();
        manager = getActivity().getSupportFragmentManager();

        //fab = (FloatingActionButton) view.findViewById(R.id.edit_profile_button);
        //fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        /*switch (id) {
            case R.id.edit_profile_button:
                Fragment editFragment = new EditProfileFragment();
                manager.beginTransaction().replace(R.id.profile_page_container, editFragment).commit();
                break;
        }*/


    }
}
