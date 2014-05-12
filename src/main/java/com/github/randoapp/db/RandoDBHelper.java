package com.github.randoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RandoDBHelper extends SQLiteOpenHelper {

    public static String COLUMN_ID = "_id";
    public static String COLUMN_USER_RANDO_ID = "USER_RANDO_ID";
    public static String COLUMN_USER_RANDO_URL = "USER_RANDO_URL";
    public static String COLUMN_USER_RANDO_URL_SMALL = "USER_RANDO_URL_SMALL";
    public static String COLUMN_USER_RANDO_URL_MEDIUM = "USER_RANDO_URL_MEDIUM";
    public static String COLUMN_USER_RANDO_URL_LARGE = "USER_RANDO_URL_LARGE";
    public static String COLUMN_USER_RANDO_DATE = "USER_RANDO_DATE";
    public static String COLUMN_USER_MAP_URL = "USER_MAP_URL";
    public static String COLUMN_USER_MAP_URL_SMALL = "USER_MAP_URL_SMALL";
    public static String COLUMN_USER_MAP_URL_MEDIUM = "USER_MAP_URL_MEDIUM";
    public static String COLUMN_USER_MAP_URL_LARGE = "USER_MAP_URL_LARGE";

    public static String COLUMN_STRANGER_RANDO_ID = "STRANGER_RANDO_ID";
    public static String COLUMN_STRANGER_RANDO_URL = "STRANGER_RANDO_URL";
    public static String COLUMN_STRANGER_RANDO_URL_SMALL = "STRANGER_RANDO_URL_SMALL";
    public static String COLUMN_STRANGER_RANDO_URL_MEDIUM = "STRANGER_RANDO_URL_MEDIUM";
    public static String COLUMN_STRANGER_RANDO_URL_LARGE = "STRANGER_RANDO_URL_LARGE";
    public static String COLUMN_STRANGER_RANDO_DATE = "STRANGER_RANDO_DATE";
    public static String COLUMN_STRANGER_MAP_URL = "STRANGER_MAP_URL";
    public static String COLUMN_STRANGER_MAP_URL_SMALL = "STRANGER_MAP_URL_SMALL";
    public static String COLUMN_STRANGER_MAP_URL_MEDIUM = "STRANGER_MAP_URL_MEDIUM";
    public static String COLUMN_STRANGER_MAP_URL_LARGE = "STRANGER_MAP_URL_LARGE";

    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_RANDO = "rando";
    private static final String DATABASE_NAME = "rando.db";
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RandoDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANDO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}