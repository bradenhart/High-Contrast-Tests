package com.bradenhart.hcdemoui.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.bradenhart.hcdemoui.Utils.*;

import com.bradenhart.hcdemoui.Difficulty;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by bradenhart on 17/12/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Static database instance
    private static DatabaseHelper sInstance;

    // Logcat tag
    private static final String LOGTAG = "DatabaseHelper";

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "HighContrastDb";

    // Table create statements
    private static final String CREATE_TABLE_CHALLENGE = "create table " + TABLE_CHALLENGE
            + "("
            + KEY_OBJECT_ID + " text not null, "
            + KEY_NAME + " text not null, "
            + KEY_DESCRIPTION + " text not null, "
            + KEY_DIFFICULTY + " integer not null, "
            + KEY_GROUP_MIN + " integer not null, "
            + KEY_GROUP_MAX + " integer not null, "
            + KEY_CREATED_AT + " datetime not null, "
            + KEY_COMPLETED + " integer default 0 not null, "
            + KEY_COMPLETED_AT + " datetime default null"
            + ")";

    // Drop table sql statement
    private static final String DROP_TABLE = "drop table if exists ";

    private SharedPreferences sp;


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sp = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHALLENGE);
        Log.e(LOGTAG, "on create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + TABLE_CHALLENGE);

        if (newVersion > oldVersion) {
            Log.e(LOGTAG, "database upgraded");
        }

        onCreate(db);
    }

    public void insertChallengesToDb(List<ParseObject> objects) {

        if (objects.get(0).getClassName().equals("Challenge")) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues cv = new ContentValues();

            SharedPreferences.Editor spEdit = sp.edit();

            for (ParseObject c : objects) {
                cv.put(KEY_OBJECT_ID, c.getObjectId());
                cv.put(KEY_NAME, c.getString("name"));
                cv.put(KEY_DESCRIPTION, c.getString("description"));
                cv.put(KEY_DIFFICULTY, getDifficultyValue(c.getString("difficulty")));
                cv.put(KEY_GROUP_MIN, c.getInt("groupMin"));
                cv.put(KEY_GROUP_MAX, c.getInt("groupMax"));
                String dateString = getDateTime(c.getCreatedAt());
                cv.put(KEY_CREATED_AT, dateString);
                spEdit.putString(KEY_LAST_DATE, dateString);
                cv.put(KEY_COMPLETED, 0);
                db.insert(TABLE_CHALLENGE, null, cv);
                cv.clear();
            }

            spEdit.apply();
            Log.e(LOGTAG, "finished inserting challenges in db");

        }
    }

    public List<Challenge> getChallengesWithFilter(String filter) {
        SQLiteDatabase db = getReadableDatabase();
        String select = "select * from " + TABLE_CHALLENGE;
        String queryString = null;
        String[] queryParams = null;
        Cursor cursor;

        switch (filter) {
            case COMPLETED:
                queryString = " where " + KEY_COMPLETED + " =?";
                queryParams = new String[]{"1"};
                break;
            case UNCOMPLETED:
                queryString = " where " + KEY_COMPLETED + " =?";
                queryParams = new String[]{"0"};
                break;
            case EASY:
                queryString = " where " + KEY_DIFFICULTY + " =?";
                queryParams = new String[]{String.valueOf(getDifficultyValue(EASY))};
                break;
            case MEDIUM:
                queryString = " where " + KEY_DIFFICULTY + " =?";
                queryParams = new String[]{String.valueOf(getDifficultyValue(MEDIUM))};
                break;
            case HARD:
                queryString = " where " + KEY_DIFFICULTY + " =?";
                queryParams = new String[]{String.valueOf(getDifficultyValue(HARD))};
                break;
            case INSANE:
                queryString = " where " + KEY_DIFFICULTY + " =?";
                queryParams = new String[]{String.valueOf(getDifficultyValue(INSANE))};
                break;
            case NEWEST:
                queryString = " order by " + KEY_CREATED_AT + " desc";
                queryParams = null;
                break;
            case OLDEST:
                queryString = " order by " + KEY_CREATED_AT + " asc";
                queryParams = null;
                break;
            case DIFFICULTY_ASC:
                queryString = " order by " + KEY_DIFFICULTY + " asc";
                queryParams = null;
                break;
            case DIFFICULTY_DESC:
                queryString = " order by " + KEY_DIFFICULTY + " desc";
                queryParams = null;
                break;
        }

        cursor = db.rawQuery(select + queryString, queryParams);

        List<Challenge> results = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Challenge c = new Challenge();
                String objectId = cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                Integer diffValue = cursor.getInt(cursor.getColumnIndex(KEY_DIFFICULTY));
                String difficulty = getDifficultyTerm(diffValue);
                Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_MIN));
                Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_MAX));
                String dateStr = cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT));
                Date createdAt = convertDateTimeToDate(dateStr);
                Boolean completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1;

                c.setObjectId(objectId);
                c.setName(name);
                c.setDescription(description);
                c.setDifficulty(difficulty);
                c.setGroupMin(groupMin);
                c.setGroupMax(groupMax);
                c.setCreatedAt(createdAt);
                c.setCompleted(completed);

                results.add(c);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return results;
    }

    public Challenge getChallenge(String queryString, String[] queryParams) {
        SQLiteDatabase db = getReadableDatabase();

        Challenge c = null;

        Cursor cursor = db.rawQuery(queryString, queryParams);

        if (cursor.moveToFirst()) {
            c = new Challenge();
            String objectId = cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
            String difficulty = getDifficultyTerm(cursor.getInt(cursor.getColumnIndex(KEY_DIFFICULTY)));
            Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_MIN));
            Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_MAX));
            Date createdAt = convertDateTimeToDate(cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));
            Boolean completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1;

            c.setObjectId(objectId);
            c.setName(name);
            c.setDescription(description);
            c.setDifficulty(difficulty);
            c.setGroupMin(groupMin);
            c.setGroupMax(groupMax);
            c.setCreatedAt(createdAt);
            c.setCompleted(completed);
        } else {
            Log.e(LOGTAG, "cursor is empty");
        }

        cursor.close();
        return c;
    }

    public boolean noChallengesCompleted() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?",
                new String[]{COMPLETED_TRUE, LIMIT_ONE});

        // true - we found no challenges, false - we did
        boolean result = cursor.getCount() == 0;

        cursor.close();

        return result;
    }

    public boolean allChallengesCompleted() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? limit ?",
                new String[]{COMPLETED_FALSE, LIMIT_ONE});

        // true - we found no uncompleted challenges, false - we did
        boolean result = cursor.getCount() == 0;

        cursor.close();

        return result;
    }

    public int getCompletedChallengeCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =?",
                new String[]{COMPLETED_TRUE});

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getCompletedChallengeCount(String difficulty) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? and " + KEY_DIFFICULTY + " =?",
                new String[]{COMPLETED_TRUE, String.valueOf(getDifficultyValue(difficulty))});

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int setChallengeCompleted(String objectId) {
        SQLiteDatabase db = getWritableDatabase();

        String completedAt = getDateTime(new Date());

        ContentValues cv = new ContentValues();
        cv.put(KEY_COMPLETED, 1);
        cv.put(KEY_COMPLETED_AT, completedAt);

        return db.update(TABLE_CHALLENGE,
                cv,
                KEY_OBJECT_ID + " =?",
                new String[]{objectId});
    }

    public String[] getValidDifficultyOptions() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select distinct " + KEY_DIFFICULTY + " from " + TABLE_CHALLENGE + " where " + KEY_COMPLETED + " =? order by " + KEY_DIFFICULTY + " asc",
                new String[]{COMPLETED_FALSE});

        String[] results = new String[cursor.getCount()];
        int index = 0;

        if (cursor.moveToFirst()) {
            do {
                Integer value = cursor.getInt(cursor.getColumnIndex(KEY_DIFFICULTY));
                results[index++] = getDifficultyTerm(value);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return results;
    }

}
