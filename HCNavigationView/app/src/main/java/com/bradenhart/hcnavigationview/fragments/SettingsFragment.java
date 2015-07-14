package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.bradenhart.hcnavigationview.activities.BaseActivity;
import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 29/06/15.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private Button doneBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        spEdit = sharedPreferences.edit();

        doneBtn = (Button) view.findViewById(R.id.settings_done);
        doneBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.settings_done:
                spEdit.putString(KEY_SETUP_STAGE, stageCompleted).apply();
                Intent intent = new Intent(getActivity().getApplicationContext(), BaseActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the user's choice of difficulty and show it again if screen rotates.
    }
}
