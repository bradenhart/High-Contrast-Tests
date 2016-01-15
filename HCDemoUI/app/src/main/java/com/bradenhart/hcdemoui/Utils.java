package com.bradenhart.hcdemoui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bradenhart on 3/12/15.
 */
public final class Utils {

    public static final String COMPLETED = "Completed";
    public static final String UNCOMPLETED = "Uncompleted";
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
    public static final String INSANE = "Insane";

    public static final String NEWEST = "Newest";
    public static final String OLDEST = "Oldest";
    public static final String DIFFICULTY_ASC = "Difficulty (asc)";
    public static final String DIFFICULTY_DESC = "Difficulty (desc)";

    public static final String SHARED_PREFERENCES = "mySharedPreferences";
    public static final String KEY_LAST_DATE = "last_date";
    public static final String KEY_STATE_DIFFICULTY = "difficulty_state";
    public static final String KEY_STATE_GROUP_SIZE = "group_size_state";
    public static final String DEFAULT_DIFFICULTY_STATE = EASY;
    public static final Integer DEFAULT_GROUP_SIZE_STATE = 1;
    public static final String KEY_RANDOM_MODE = "random_mode";
    public static final Boolean DEFAULT_RANDOM_MODE_STATE = false;
    public static final String KEY_EXTRA_PLAY_ID = "to_play_id";
    public static final String KEY_REPEAT_MODE = "repeat_mode";

    // DATABASE variables
    // Table names
    public static final String TABLE_CHALLENGE = "challenges";

    // Column names
    public static final String KEY_OBJECT_ID = "object_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_GROUP_MIN = "group_min";
    public static final String KEY_GROUP_MAX = "group_max";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_COMPLETED_AT = "completed_at";

    public static final String COMPLETED_FALSE = "0";
    public static final String COMPLETED_TRUE = "1";
    public static final String LIMIT_ONE = "1";

    // QUERY SELECT STATEMENTS
//    public static final String SELECT_SHUFFLE = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?";
    public static final String SELECT_SHUFFLE = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? order by random() limit ?";
    public static final String SELECT_NORMAL = "select * from " + TABLE_CHALLENGE
            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? limit ?";
    public static final String SELECT_BY_ID = "select * from " + TABLE_CHALLENGE + " where " + KEY_OBJECT_ID + " =? ";
    public static final String SELECT_SHUFFLE_SKIP = "select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? and " + KEY_OBJECT_ID
            + " !=? order by random() limit ?";
    public static final String SELECT_NORMAL_SKIP = "select * from " + TABLE_CHALLENGE
            + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =? and " + KEY_GROUP_MIN + " =? and " + KEY_OBJECT_ID
            + " !=? order by random() limit ?";
//    public static final String SELECT_RANDOM

    public static final Integer GROUP_SIZE_MINIMUM = 1;
    public static final Integer GROUP_SIZE_MAXIMUM = 7;

    public static final String KEY_HAD_FIRST_USE = "first_use";


    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabs_height);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
//    public static float convertDpToPixel(float dp, Context context){
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float px = dp * (metrics.densityDpi / 160f);
//        return px;
//    }
    public static int getPixelsFromDP(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date convertDateTimeToDate(String dateTime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(date);
        return date;
    }

    public static Integer getDifficultyValue(String difficulty) {

        switch (difficulty) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            case "Insane":
                return 4;
        }

        return null;
    }

    public static String getDifficultyTerm(Integer value) {
        switch (value) {
            case 1:
                return "Easy";
            case 2:
                return "Medium";
            case 3:
                return "Hard";
            case 4:
                return "Insane";
        }

        return null;
    }

}
