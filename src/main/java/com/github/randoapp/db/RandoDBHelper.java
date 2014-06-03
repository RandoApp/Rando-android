package com.github.randoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RandoDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "rando.db";

    public static final String TABLE_RANDO = "rando";
    public static final String TABLE_RANDO_UPLOAD = "rando_upload";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_RANDO_ID = "USER_RANDO_ID";
    public static final String COLUMN_USER_RANDO_URL = "USER_RANDO_URL";
    public static final String COLUMN_USER_RANDO_URL_SMALL = "USER_RANDO_URL_SMALL";
    public static final String COLUMN_USER_RANDO_URL_MEDIUM = "USER_RANDO_URL_MEDIUM";
    public static final String COLUMN_USER_RANDO_URL_LARGE = "USER_RANDO_URL_LARGE";
    public static final String COLUMN_USER_RANDO_DATE = "USER_RANDO_DATE";
    public static final String COLUMN_USER_MAP_URL = "USER_MAP_URL";
    public static final String COLUMN_USER_MAP_URL_SMALL = "USER_MAP_URL_SMALL";
    public static final String COLUMN_USER_MAP_URL_MEDIUM = "USER_MAP_URL_MEDIUM";
    public static final String COLUMN_USER_MAP_URL_LARGE = "USER_MAP_URL_LARGE";

    public static final String COLUMN_STRANGER_RANDO_ID = "STRANGER_RANDO_ID";
    public static final String COLUMN_STRANGER_RANDO_URL = "STRANGER_RANDO_URL";
    public static final String COLUMN_STRANGER_RANDO_URL_SMALL = "STRANGER_RANDO_URL_SMALL";
    public static final String COLUMN_STRANGER_RANDO_URL_MEDIUM = "STRANGER_RANDO_URL_MEDIUM";
    public static final String COLUMN_STRANGER_RANDO_URL_LARGE = "STRANGER_RANDO_URL_LARGE";
    public static final String COLUMN_STRANGER_RANDO_DATE = "STRANGER_RANDO_DATE";
    public static final String COLUMN_STRANGER_MAP_URL = "STRANGER_MAP_URL";
    public static final String COLUMN_STRANGER_MAP_URL_SMALL = "STRANGER_MAP_URL_SMALL";
    public static final String COLUMN_STRANGER_MAP_URL_MEDIUM = "STRANGER_MAP_URL_MEDIUM";
    public static final String COLUMN_STRANGER_MAP_URL_LARGE = "STRANGER_MAP_URL_LARGE";


    public static final String COLUMN_FILE = "RANDO_FILE";
    public static final String COLUMN_LATITUDE = "RANDO_LATITUDE";
    public static final String COLUMN_LONGITUDE = "RANDO_LONGITUDE";

    private static final String RANDO_UPLOAD_TABLE_CREATE = "CREATE TABLE " + TABLE_RANDO_UPLOAD +
            " (" + COLUMN_ID + " integer primary key autoincrement, " +
            " (" + COLUMN_FILE + " text," +
            " (" + COLUMN_LATITUDE + " text," +
            " (" + COLUMN_LONGITUDE + " text," +
            ");";

    private static final String RANDO_TABLE_CREATE = "CREATE TABLE " + TABLE_RANDO +
            " (" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USER_RANDO_ID + " text," +
            COLUMN_USER_RANDO_URL + " text," +
            COLUMN_USER_RANDO_URL_SMALL + " text," +
            COLUMN_USER_RANDO_URL_MEDIUM + " text," +
            COLUMN_USER_RANDO_URL_LARGE + " text," +
            COLUMN_USER_RANDO_DATE + " integer," +
            COLUMN_USER_MAP_URL + " text," +
            COLUMN_USER_MAP_URL_SMALL + " text," +
            COLUMN_USER_MAP_URL_MEDIUM + " text," +
            COLUMN_USER_MAP_URL_LARGE + " text," +
            COLUMN_STRANGER_RANDO_ID + " text," +
            COLUMN_STRANGER_RANDO_URL + " text," +
            COLUMN_STRANGER_RANDO_URL_SMALL + " text," +
            COLUMN_STRANGER_RANDO_URL_MEDIUM + " text," +
            COLUMN_STRANGER_RANDO_URL_LARGE + " text," +
            COLUMN_STRANGER_RANDO_DATE + " integer," +
            COLUMN_STRANGER_MAP_URL + " text," +
            COLUMN_STRANGER_MAP_URL_SMALL + " text," +
            COLUMN_STRANGER_MAP_URL_MEDIUM + " text," +
            COLUMN_STRANGER_MAP_URL_LARGE + " text" +
            ");";

    RandoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RANDO_TABLE_CREATE);
        db.execSQL(RANDO_UPLOAD_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RandoDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANDO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANDO_UPLOAD);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}