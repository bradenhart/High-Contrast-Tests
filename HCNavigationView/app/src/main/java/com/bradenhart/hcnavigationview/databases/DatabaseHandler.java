package com.bradenhart.hcnavigationview.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    // CLASSES column names
    // Profile table's columns
    private static final String KEY_ID = "id";
    private static final String KEY_PICTURE = "picture";

    // Table Create Statements
    // PROFILE table create statement
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_PROFILE
            + "(" + KEY_ID + " TEXT,"
            + KEY_PICTURE + " BLOB"
            + ")";

    public static synchronized DatabaseHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
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
        Log.e(LOGTAG, "on create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + KEY_ID + " =?", new String[] { KEY_PROFILE_PIC });
        if (cursor.moveToFirst()) {
            array = cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE));
            Log.e(LOGTAG, "retrieved byte array from db");
        } else {
            Log.e(LOGTAG, "byte array is null, not found in db");
        }
        cursor.close();
        return array;
    }
}
