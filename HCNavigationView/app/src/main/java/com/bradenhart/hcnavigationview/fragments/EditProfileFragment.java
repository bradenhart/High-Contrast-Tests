package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bradenhart.hcnavigationview.R;

import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 1/07/15.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private Context context;
    private final String KEY_INPUT = "input";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEdit;
    private ImageView galleryButton, cameraButton;
    private EditText newNameEditText;
    private FloatingActionButton fab;
    private String newName = null;
    private FragmentManager manager;

    public EditProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);



        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.finished_editing_button:
                Fragment standinFragment = new StandinFragment();
                manager.beginTransaction().remove(new EditProfileFragment()).commit();
                //manager.beginTransaction().replace(R.id.profile_page_container, standinFragment).commit();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        int id = view.getId();
        String currentInput;
        String currentName = sharedPreferences.getString(KEY_USER_NAME, defaultName);

        if (id == R.id.edit_input_name) {
            currentInput = newNameEditText.getText().toString();
            if (currentInput.length() > 0 && !currentInput.equals(currentName)) {
                newName = currentInput;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INPUT, newName);
    }
}
