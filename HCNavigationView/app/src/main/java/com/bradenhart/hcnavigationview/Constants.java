package com.bradenhart.hcnavigationview;

import android.content.Context;

/**
 * Created by bradenhart on 30/06/15.
 */
public final class Constants {

    public static final String KEY_SELECTED_ID = "selected_id";
    public static final String KEY_FIRST_LOOK = "first_look";
    public static final String KEY_USER_NAME = "username";
    public static final String defaultName = "J. Doe";
    public static final String KEY_REQUEST_ACTION = "request_action";
    public static final String showNewChallenge = "show_new_challenge";
    public static final String KEY_PROFILE_PIC = "profile_pic";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String ARG_DIFF = "diff";
    public static final int defaultPic = R.drawable.com_facebook_profile_picture_blank_portrait;

    public static final String KEY_LAST_BATCH = "batch";

    public static final String screenProfile = "profile";
    public static final String screenNewChallenge = "newChallenge";
    public static final String screenChallenges = "challenges";
    public static final String screenCompleted = "completed";
    public static final String screenAchievements = "achievements";
    public static final String screenSettings = "settings";
    public static final String screenHelpFeedback = "helpFeedback";

    public static final String DIFF_EASY = "Easy";
    public static final String DIFF_MEDIUM = "Medium";
    public static final String DIFF_HARD = "Hard";
    public static final String DIFF_IMPOSSIBLE = "Impossible";

    public static int convertDptoPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

}
