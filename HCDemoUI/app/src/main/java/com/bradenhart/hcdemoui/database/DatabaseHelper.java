package com.bradenhart.hcdemoui.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.bradenhart.hcdemoui.Utils.*;
import com.parse.ParseObject;

import java.util.ArrayList;
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

    // Table names
    private static final String TABLE_CHALLENGE = "challenges";

    // Column names
    private static final String KEY_OBJECT_ID = "object_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_GROUP_MIN = "group_min";
    private static final String KEY_GROUP_MAX = "group_max";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_COMPLETED = "completed";

    // Table create statements
    private static final String CREATE_TABLE_CHALLENGE = "create table " + TABLE_CHALLENGE
            + "("
            + KEY_OBJECT_ID + " text,"
            + KEY_NAME + " text,"
            + KEY_DESCRIPTION + " text,"
            + KEY_DIFFICULTY + " integer,"
            + KEY_GROUP_MIN + " integer,"
            + KEY_GROUP_MAX + " integer,"
            + KEY_CREATED_AT + " datetime,"
            + KEY_COMPLETED + " integer default 0"
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
                cv.put(KEY_OBJECT_ID, c.getString("objectId"));
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
        String selectStr = "select * from " + TABLE_CHALLENGE ;
        Cursor cursor = null;

        switch (filter) {
            case NEWEST:
                cursor = db.rawQuery(selectStr + " order by " + KEY_CREATED_AT + " desc", null);
                break;
            case COMPLETED:
                cursor = db.rawQuery(selectStr + " where " + KEY_COMPLETED + " =?", new String[] {"1"});
                break;
            case UNCOMPLETED:
                cursor = db.rawQuery(selectStr + " where " + KEY_COMPLETED + " =?", new String[] {"0"});
                break;
            case DIFFICULTY_E_H:
                cursor = db.rawQuery(selectStr + " order by " + KEY_DIFFICULTY + " asc", null);
                break;
            case DIFFICULTY_H_E:
                cursor = db.rawQuery(selectStr + " order by " + KEY_DIFFICULTY + " asc", null);
                break;
        }

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

    public int setChallengeCompleted(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_COMPLETED, 1);
        return db.update(TABLE_CHALLENGE, cv, "where " + KEY_NAME + " =?", new String[] { name });
    }

    private Integer getDifficultyValue(String difficulty) {

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

    private String getDifficultyTerm(Integer value) {
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
