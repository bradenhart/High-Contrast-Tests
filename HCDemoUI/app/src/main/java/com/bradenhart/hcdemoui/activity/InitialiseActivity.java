package com.bradenhart.hcdemoui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.database.Challenge;
import com.bradenhart.hcdemoui.database.DatabaseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import static com.bradenhart.hcdemoui.Utils.*;

/**
 * Created by bradenhart on 2/01/16.
 */
public class InitialiseActivity extends AppCompatActivity {

    private final String LOGTAG = "InitialiseActivity";
    private View root;
    private ImageView loadingIcon;
    private Animation slideOutLeftAnim, slideInLeftAnim, slideUpAnim, spinAnim;
    private DatabaseHelper dbHelper;
    private SharedPreferences sp;
    private CountDownTimer cTimer;
    private boolean downloadComplete = false;
    private long timePassed = 0;
    private long timeRemaining = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialise);

        Log.e(LOGTAG, "on create Initialise Activity");

//        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        root = findViewById(R.id.initialise_root);

        Boolean firstUse = sp.contains(KEY_HAD_FIRST_USE);
        if (firstUse) {
            startActivity(new Intent(this, ChallengeActivity.class));
        } else {

            dbHelper = DatabaseHelper.getInstance(this);
            loadingIcon = (ImageView) findViewById(R.id.loading_icon);

            initAnimations();

            cTimer = new CountDownTimer(timeRemaining, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed += 1000;
                    Log.e(LOGTAG, "time passed: " + timePassed / 1000 + "s");
                    if (downloadComplete && timePassed > 6000) {
                        cTimer.onFinish();
                    }
                }

                @Override
                public void onFinish() {
                    if (!downloadComplete) {
                        cTimer.cancel();
                        cTimer.start();
                    }
                    sp.edit().putBoolean(KEY_HAD_FIRST_USE, true).apply();
                    Intent intent = new Intent(InitialiseActivity.this, ChallengeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    root.setAnimation(slideLeftAnim);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                }
            };

            initiateFirstDataDownload();
        }

    }

    private void initAnimations() {
        spinAnim = AnimationUtils.loadAnimation(this, R.anim.anim_spin);
//        slideOutLeftAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_left);
//        slideInLeftAnim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_left);
    }

    /**
     * download methods
     */
    private void initiateFirstDataDownload() {
        cTimer.start();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        loadingIcon.setAnimation(spinAnim);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // no error
                    Log.e(LOGTAG, "calling insertChallenges method");
                    dbHelper.insertChallengesToDb(objects);
                    Log.e(LOGTAG, "Stored difficulty: " + storedDifficultyAsString());
                    Log.e(LOGTAG, "Stored group min: " + storedGroupMin());
                    downloadComplete = true;
                } else {
                    Log.e(LOGTAG, "error occurred querying for challenges");
                }
            }

        });
    }

    private String storedDifficultyAsString() {
        return sp.getString(KEY_STATE_DIFFICULTY, DEFAULT_DIFFICULTY_STATE);
    }

    private Integer storedDifficultyAsInt() {
        return getDifficultyValue(storedDifficultyAsString());
    }

    private Integer storedGroupMin() {
        return sp.getInt(KEY_STATE_GROUP_SIZE, DEFAULT_GROUP_SIZE_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOGTAG, "resume");
    }

    private boolean versionIsAboveKitkat() {
        int version = Build.VERSION.SDK_INT;
        return version >= Build.VERSION_CODES.KITKAT;
    }

}
