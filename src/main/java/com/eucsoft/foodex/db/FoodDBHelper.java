package com.eucsoft.foodex.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "database";

    public static String COLUMN_ID = "_id";
    public static String COLUMN_USER_FOOD_URL = "USER_FOOD_URL";
    public static String COLUMN_USER_FOOD_DATE = "USER_FOOD_DATE";
    public static String COLUMN_USER_BON_APPETIT = "USER_BON_APPETIT";
    public static String COLUMN_USER_MAP_URL = "USER_MAP_URL";

    public static String COLUMN_STRANGER_FOOD_URL = "STRANGER_PHOTO_URL";
    public static String COLUMN_STRANGER_FOOD_DATE = "STRANGER_FOOD_DATE";
    public static String COLUMN_STRANGER_BON_APPETIT = "STRANGER_BON_APPETIT";
    public static String COLUMN_STRANGER_MAP_URL = "STRANGER_MAP_URL";

    private static final int DATABASE_VERSION = 7;
    public static final String TABLE_FOOD = "food";
    private static final String DATABASE_NAME = "foodex.db";
    private static final String FOOD_TABLE_CREATE = "CREATE TABLE " + TABLE_FOOD +
            " (" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USER_FOOD_URL + " text," +
            COLUMN_USER_FOOD_DATE + " integer," +
            COLUMN_USER_BON_APPETIT + " integer not null," +
            COLUMN_USER_MAP_URL + " text," +
            COLUMN_STRANGER_FOOD_URL + " text," +
            COLUMN_STRANGER_FOOD_DATE + " integer," +
            COLUMN_STRANGER_BON_APPETIT + " integer not null," +
            COLUMN_STRANGER_MAP_URL + " text" +
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}