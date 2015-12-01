package com.bradenhart.hcnavigationview.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bradenhart.hclib.domain.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;
import static com.bradenhart.hcnavigationview.Constants.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bradenhart on 29/06/15.
 */
public class NewChallengeFragment extends Fragment implements View.OnClickListener {

    private final String LOGTAG = "NewChallengeFragment";
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    private DatabaseHandler dbHandler;
    private Button completedBtn;
    private Button tooHardBtn;
    private Button newChallengeBtn;
    private TextView challengeNameTv;
    private TextView challengeTv;
    private Random gen = new Random();
    private Challenge challenge;
    private final String KEY_CHALLENGE = "challenge";

    public NewChallengeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_challenge, container, false);
        context = getActivity();
        dbHandler = DatabaseHandler.getInstance(context);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        spEdit = sp.edit();

        completedBtn = (Button) view.findViewById(R.id.challenge_completed_button);
        tooHardBtn = (Button) view.findViewById(R.id.too_hard_button);
        newChallengeBtn = (Button) view.findViewById(R.id.new_challenge_button);
        challengeNameTv = (TextView) view.findViewById(R.id.challenge_name_textview)
;        challengeTv = (TextView) view.findViewById(R.id.challenge_textview);

        completedBtn.setOnClickListener(this);
        tooHardBtn.setOnClickListener(this);
        newChallengeBtn.setOnClickListener(this);


        String name = sp.getString(KEY_CHALLENGE, null);
        if (name != null) {
            challenge = dbHandler.getChallengeByName(name);
        } else {
            getNewChallenge(sp.getString(KEY_DIFFICULTY, DIFF_EASY));
        }


        updateChallengeTextViews();

        return view;
    }

    private void getNewChallenge(String diff) {
        ArrayList<Challenge> challenges = dbHandler.getUncompletedChallenges(diff);
        int num = challenges.size();
        int index = gen.nextInt(num);
        challenge = challenges.get(index);
        spEdit.putString(KEY_CHALLENGE, challenge.getName()).apply();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.challenge_completed_button:
                dbHandler.updateChallenge(challenge);
                getNewChallenge(sp.getString(KEY_DIFFICULTY, DIFF_EASY));
                updateChallengeTextViews();
                break;
            case R.id.too_hard_button:
                if (DIFF_EASY.equals(sp.getString(KEY_DIFFICULTY, DIFF_EASY)) && dbHandler.getUncompletedChallenges(DIFF_EASY).size() == 1) {
                    Toast.makeText(getActivity(), "You're out of easy challenges, try increasing the difficulty!", Toast.LENGTH_LONG).show();
                }
                getNewChallenge(DIFF_EASY);
                updateChallengeTextViews();
                Log.e(LOGTAG, "CLICKED too hard");
                break;
            case R.id.new_challenge_button:
                //challenge = getNewChallenge();
                updateChallengeTextViews();
                break;
        }

    }

    private void updateChallengeTextViews() {
        challengeNameTv.setText(challenge.getName());
        challengeTv.setText(challenge.getDescription());

        switch (challenge.getDifficulty()) {
            case DIFF_EASY:
                challengeTv.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case DIFF_MEDIUM:
                challengeTv.setBackgroundColor(getResources().getColor(R.color.yellow));
                break;
            case DIFF_HARD:
                challengeTv.setBackgroundColor(getResources().getColor(R.color.fab_ripple_color));
                break;
            case DIFF_IMPOSSIBLE:
                challengeTv.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            default:
                challengeTv.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void increaseDifficulty() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(LOGTAG, "saveInstanceState");
//        outState.putSerializable(KEY_CHALLENGE, challenge);
    }
}
