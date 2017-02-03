package com.github.randoapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RandoDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "rando.db";

    private static RandoDBHelper helperInstance;
    private SQLiteDatabase db;

    private RandoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static synchronized RandoDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new RandoDBHelper(context);
        }
        return helperInstance;
    }

    public static synchronized SQLiteDatabase getDatabase(Context context) {
        getInstance(context);
        if (null == helperInstance.db) {
            helperInstance.db = helperInstance.getWritableDatabase();
        }
        return helperInstance.db;
    }

    @Override
    protected void finalize() throws Throwable {
        if (null != helperInstance)
            helperInstance.close();
        if (null != db)
            db.close();
        super.finalize();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RandoTable.CREATE_TABLE_SQL);
        db.execSQL(RandoUploadTable.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RandoDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        if (oldVersion < 9) {
            dropAllAndCreate(db);
            return;
        }
        if (oldVersion < 10) {
            upgradeTo10(db);
        }

        if (oldVersion < 11) {
            upgradeTo11(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllAndCreate(db);
    }

    private void upgradeTo10(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + RandoTable.NAME + " ADD COLUMN " + RandoTable.COLUMN_DETECTED + " TEXT");
    }

    private void upgradeTo11(SQLiteDatabase db) {
        if (!isRandoTableColumnExist(db, RandoTable.COLUMN_RANDO_STATUS)) {
            dropAllAndCreate(db);
        }
    }

    private void dropAllAndCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + RandoTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RandoUploadTable.NAME);
        onCreate(db);
    }

    private boolean isRandoTableColumnExist(SQLiteDatabase db, String columnName) {
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + RandoTable.NAME + " limit 1", null);
            if (c != null && c.getColumnIndex(columnName) > 0) {
                return true;
            }
        } catch (Exception e) {
            Log.e(RandoDBHelper.class.toString(), e.getMessage(), e);
        } finally {
            if (c != null)
                c.close();
        }
        return false;
    }


    public static class RandoTable {
        public static final String NAME = "rando";

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
        public static final String COLUMN_DETECTED = "DETECTED";

        public static final String COLUMN_RANDO_STATUS = "RANDO_STATUS";

        public static final String[] ALL_COLUMNS = {
                COLUMN_ID, COLUMN_USER_RANDO_ID, COLUMN_USER_RANDO_URL, COLUMN_USER_RANDO_URL_SMALL, COLUMN_USER_RANDO_URL_MEDIUM,
                COLUMN_USER_RANDO_URL_LARGE, COLUMN_USER_RANDO_DATE, COLUMN_USER_MAP_URL, COLUMN_USER_MAP_URL_SMALL, COLUMN_USER_MAP_URL_MEDIUM, COLUMN_USER_MAP_URL_LARGE, COLUMN_RANDO_STATUS, COLUMN_DETECTED};

        public static final String CREATE_TABLE_SQL = "CREATE TABLE " + RandoTable.NAME +
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
                COLUMN_RANDO_STATUS + " text," +
                COLUMN_DETECTED + " text" +
                ");";
    }


    public static class RandoUploadTable {
        public static final String NAME = "rando_upload";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_FILE = "RANDO_FILE";
        public static final String COLUMN_LATITUDE = "RANDO_LATITUDE";
        public static final String COLUMN_LONGITUDE = "RANDO_LONGITUDE";
        public static final String COLUMN_DATE = "RANDO_DATE";
        public static final String COLUMN_LAST_TRY_DATE = "RANDO_LAST_TRY_DATE";

        public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_FILE, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE, COLUMN_LAST_TRY_DATE};

        private static final String CREATE_TABLE_SQL = "CREATE TABLE " + RandoUploadTable.NAME +
                " (" + COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_FILE + " text," +
                COLUMN_LATITUDE + " text," +
                COLUMN_LONGITUDE + " text," +
                COLUMN_DATE + " integer," +
                COLUMN_LAST_TRY_DATE + " integer" +
                ");";
    }
}