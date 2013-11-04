package com.eucsoft.foodex.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "database";

    public static String COLUMN_ID = "_id";
    public static String COLUMN_USER_PHOTO_URL = "USER_PHOTO_URL";
    public static String COLUMN_USER_LOCAL_FILE = "USER_LOCAL_FILE";
    public static String COLUMN_USER_LIKED = "USER_LIKED";
    public static String COLUMN_USER_MAP = "USER_MAP";

    public static String COLUMN_STRANGER_PHOTO_URL = "STRANGER_PHOTO_URL";
    public static String COLUMN_STRANGER_LOCAL_FILE = "STRANGER_LOCAL_FILE";
    public static String COLUMN_STRANGER_LIKED = "STRANGER_LIKED";
    public static String COLUMN_STRANGER_MAP = "STRANGER_MAP";

    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_FOOD = "food";
    private static final String DATABASE_NAME = "foodex.db";
    private static final String FOOD_TABLE_CREATE = "CREATE TABLE " + TABLE_FOOD +
            " (" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USER_PHOTO_URL + " text," +
            COLUMN_USER_LOCAL_FILE + " text not null," +
            COLUMN_USER_LIKED + " integer not null," +
            COLUMN_USER_MAP + " text," +
            COLUMN_STRANGER_PHOTO_URL + " text," +
            COLUMN_STRANGER_LOCAL_FILE + " text," +
            COLUMN_STRANGER_LIKED + " integer not null," +
            COLUMN_STRANGER_MAP + " text" +
            ");";

    FoodDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FOOD_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FoodDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }
}