package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bradenhart.hcnavigationview.R;
import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 29/06/15.
 */
public class SocialFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private Button skipBtn, nextBtn;

    public SocialFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

        skipBtn = (Button) view.findViewById(R.id.social_skip);
        nextBtn = (Button) view.findViewById(R.id.social_next);

        skipBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = null;

        switch (id) {
            case R.id.social_skip:
                spEdit.putString(KEY_SETUP_STAGE, stageSettings).apply();
                fragment = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                break;
            case R.id.social_next:
                spEdit.putString(KEY_SETUP_STAGE, stageSettings).apply();
                fragment = new SettingsFragment();
                fragmentManager.beginTransaction().replace(R.id.welcome_container, fragment).commit();
                break;
            default:
                break;
        }

    }
}
