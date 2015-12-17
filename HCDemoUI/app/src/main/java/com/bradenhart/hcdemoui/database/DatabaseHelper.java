package com.bradenhart.hcdemoui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bradenhart.hcdemoui.Utils;
import com.parse.ParseObject;

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
            + KEY_DIFFICULTY + " text,"
            + KEY_GROUP_MIN + " integer,"
            + KEY_GROUP_MAX + " integer,"
            + KEY_CREATED_AT + " datetime,"
            + KEY_COMPLETED + " integer default 0"
            + ")";

    // Drop table sql statement
    private static final String DROP_TABLE = "drop table if exists ";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

            for (ParseObject c : objects) {
                cv.put(KEY_OBJECT_ID, c.getString("objectId"));
                cv.put(KEY_NAME, c.getString("name"));
                cv.put(KEY_DESCRIPTION, c.getString("description"));
                cv.put(KEY_DIFFICULTY, c.getString("difficulty"));
                cv.put(KEY_GROUP_MIN, c.getInt("groupMin"));
                cv.put(KEY_GROUP_MAX, c.getInt("groupMax"));
                cv.put(KEY_CREATED_AT, Utils.getDateTime(c.getCreatedAt()));
                db.insert(TABLE_CHALLENGE, null, cv);
                cv.clear();
            }

            Log.e(LOGTAG, "finished inserting challenges in db");

        }
    }
}
