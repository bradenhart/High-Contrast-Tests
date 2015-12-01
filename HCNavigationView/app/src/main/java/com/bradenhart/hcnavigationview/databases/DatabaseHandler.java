package com.bradenhart.hcnavigationview.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bradenhart.hclib.domain.Challenge;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static com.bradenhart.hcnavigationview.Constants.*;

/**
 * Created by bradenhart on 10/07/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    //private Context context;
    private static DatabaseHandler sInstance;

    // Logcat tag
    private static final String LOGTAG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "HCNavigationViewDb";

    // Table names
    private static final String TABLE_PROFILE = "profile";
    private static final String TABLE_CHALLENGE = "challenge";

    // Table column names
    // Profile table's columns
    private static final String KEY_ID = "id";
    private static final String KEY_PICTURE = "picture";
    // Challenge table's columns
    private static final String KEY_BATCH = "batch";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_GROUPMIN = "groupMin";
    private static final String KEY_GROUPMAX = "groupMax";
    private static final String KEY_COMPLETEDWITH = "completedWith";

    // Table Create Statements
    // PROFILE table create statement
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_PROFILE
            + "(" + KEY_ID + " TEXT,"
            + KEY_PICTURE + " BLOB"
            + ")";
    private static final String CREATE_TABLE_CHALLENGE = "CREATE TABLE "
            + TABLE_CHALLENGE
            + "(" + KEY_BATCH + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_DIFFICULTY + " TEXT,"
            + KEY_COMPLETED + " INTEGER DEFAULT 0,"
            + KEY_GROUPMIN + " INTEGER,"
            + KEY_GROUPMAX + " INTEGER,"
            + KEY_COMPLETEDWITH + " INTEGER DEFAULT 0"
            + ")";

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_CHALLENGE);
        Log.e(LOGTAG, "on create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGE);
        onCreate(db);
        Log.e(LOGTAG, "on upgrade");
    }

    public void saveByteArrayToDb(byte[] img) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PROFILE, null, null);
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, KEY_PROFILE_PIC);
        cv.put(KEY_PICTURE, img);
        db.insert(TABLE_PROFILE, null, cv);
        Log.e(LOGTAG, "saved byte array in db");
    }

    public byte[] retrieveByteArrayFromDb() {
        SQLiteDatabase db = getReadableDatabase();
        byte[] array = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_ID + " =?", new String[]{ KEY_PROFILE_PIC });
        if (cursor.moveToFirst()) {
            array = cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE));
            Log.e(LOGTAG, "retrieved byte array from db");
        } else {
            Log.e(LOGTAG, "byte array is null, not found in db");
        }
        cursor.close();
        return array;
    }

    public void addChallengeToDb(Challenge challenge) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_BATCH, challenge.getId());
        cv.put(KEY_NAME, challenge.getName());
        cv.put(KEY_DESCRIPTION, challenge.getDescription());
        cv.put(KEY_DIFFICULTY, challenge.getDifficulty());
        if (challenge.getCompleted()) cv.put(KEY_COMPLETED, 1);
        else cv.put(KEY_COMPLETED, 0);
        cv.put(KEY_GROUPMIN, challenge.getGroupMin());
        cv.put(KEY_GROUPMAX, challenge.getGroupMax());
        cv.put(KEY_COMPLETEDWITH, challenge.getCompletedWith());

        db.insert(TABLE_CHALLENGE, null, cv);
        Log.e(LOGTAG, "saved challenge in db");
    }

    public ArrayList<Challenge> getChallengesFromDb() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Challenge> challenges = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHALLENGE + " ORDER BY " + KEY_BATCH + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                Integer batch = cursor.getInt(cursor.getColumnIndex(KEY_BATCH));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                String difficulty = cursor.getString(cursor.getColumnIndex(KEY_DIFFICULTY));
                Boolean completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1;
                Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMIN));
                Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMAX));
                Integer completedWith = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETEDWITH));
                Challenge challenge = new Challenge();
                challenge.setId(batch);
                challenge.setName(name);
                challenge.setDescription(description);
                challenge.setDifficulty(difficulty);
                challenge.setCompleted(completed);
                challenge.setGroupMin(groupMin);
                challenge.setGroupMax(groupMax);
                challenge.setCompletedWith(completedWith);
                challenges.add(challenge);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return challenges;
    }

    public ArrayList<Challenge> getChallengesByDifficulty(String difficulty) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Challenge> challenges = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHALLENGE + " WHERE " + KEY_DIFFICULTY + " =?", new String[]{ difficulty });

        if (cursor.moveToFirst()) {
            do {
                Integer batch = cursor.getInt(cursor.getColumnIndex(KEY_BATCH));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                Boolean completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1;
                Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMIN));
                Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMAX));
                Integer completedWith = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETEDWITH));
                Challenge challenge = new Challenge();
                challenge.setId(batch);
                challenge.setName(name);
                challenge.setDescription(description);
                challenge.setDifficulty(difficulty);
                challenge.setCompleted(completed);
                challenge.setGroupMin(groupMin);
                challenge.setGroupMax(groupMax);
                challenge.setCompletedWith(completedWith);
                challenges.add(challenge);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return challenges;
    }

    public ArrayList<Challenge> getUncompletedChallenges(String difficulty) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Challenge> challenges = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHALLENGE + " WHERE " + KEY_DIFFICULTY + " =?", new String[]{ difficulty });

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 0) {
                    Integer batch = cursor.getInt(cursor.getColumnIndex(KEY_BATCH));
                    String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
                    Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMIN));
                    Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMAX));
                    Integer completedWith = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETEDWITH));
                    Challenge challenge = new Challenge();
                    challenge.setId(batch);
                    challenge.setName(name);
                    challenge.setDescription(description);
                    challenge.setDifficulty(difficulty);
                    challenge.setCompleted(false);
                    challenge.setGroupMin(groupMin);
                    challenge.setGroupMax(groupMax);
                    challenge.setCompletedWith(completedWith);
                    challenges.add(challenge);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return challenges;
    }

    public int updateChallenge(Challenge c) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_COMPLETED, 1);

        int rowsAffected = db.update(TABLE_CHALLENGE, values, KEY_NAME + "=?",
                new String[]{c.getName()});

        db.close();

        return rowsAffected;
    }

    public Challenge getChallengeByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHALLENGE + " WHERE " + KEY_NAME + " =?", new String[]{ name });
        if (cursor.moveToFirst()) {
            Integer batch = cursor.getInt(cursor.getColumnIndex(KEY_BATCH));
            String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
            String difficulty = cursor.getString(cursor.getColumnIndex(KEY_DIFFICULTY));
            Integer groupMin = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMIN));
            Integer groupMax = cursor.getInt(cursor.getColumnIndex(KEY_GROUPMAX));
            Integer completedWith = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETEDWITH));
            Challenge challenge = new Challenge();
            challenge.setId(batch);
            challenge.setName(name);
            challenge.setDescription(description);
            challenge.setDifficulty(difficulty);
            challenge.setCompleted(false);
            challenge.setGroupMin(groupMin);
            challenge.setGroupMax(groupMax);
            challenge.setCompletedWith(completedWith);
            return challenge;
        }
        return null;
    }

}
