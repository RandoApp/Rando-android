package com.github.randoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RandoDAO {

    // Database fields
    private SQLiteDatabase database;
    private RandoDBHelper randoDBHelper;

    public RandoDAO(Context context) {
        randoDBHelper = new RandoDBHelper(context);
        database = randoDBHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        randoDBHelper.close();
    }

    //TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void addToUpload(String file, double latitude, double longitude) {
        if (file == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_FILE, file);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE, String.valueOf(latitude));
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE, String.valueOf(longitude));
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_DATE, new Date().getTime());

        database.insert(RandoDBHelper.RandoUploadTable.NAME, null, values);
    }

    //TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public List<RandoUpload> getAllRandosToUpload() {
        List<RandoUpload> randos = new ArrayList<RandoUpload>();

        Cursor cursor = database.query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, null, null, null, null, RandoDBHelper.RandoUploadTable.COLUMN_DATE + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RandoUpload rando = new RandoUpload();
            rando.id = cursor.getInt(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_ID));
            rando.file = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_FILE));
            rando.latitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE));
            rando.longitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_DATE));
            if (userDate > 0) {
                rando.date = new Date(userDate);
            } else {
                rando.date = new Date();
            }
            randos.add(rando);
            cursor.moveToNext();
        }
        cursor.close();
        return randos;
    }

    //TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void deleteRandoToUpload(RandoUpload rando) {
        String id = String.valueOf(rando.id);
        database.delete(RandoDBHelper.RandoUploadTable.NAME, RandoDBHelper.RandoUploadTable.COLUMN_ID + " = " + id, null);
        Log.i(RandoDAO.class, "Rando to upload deleted with id: ", String.valueOf(id));
    }


    /**
     * Creates randoPair and returns instance of created randoPair.
     *
     * @param randoPair RandoPair to insert
     * @return returns instance of created randoPair or null
     */

    public RandoPair createRandoPair(RandoPair randoPair) {
        if (randoPair == null) {
            return null;
        }
        ContentValues values = randoPairToContentValues(randoPair);
        long insertId = database.insert(RandoDBHelper.RandoTable.NAME, null,
                values);
        return getRandoPairById(insertId);
    }

    /**
     * Finds rando instance.
     *
     * @param id RandoPair id to get
     * @return RandoPair instance or null if rando hasn't been found
     */

    public RandoPair getRandoPairById(long id) {
        Cursor cursor = database.query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        RandoPair newRandoPair = cursorToRandoPair(cursor);
        cursor.close();
        return newRandoPair;
    }

    /**
     * Deletes randoPair instance from DB.
     *
     * @param randoPair RandoPair to delete
     */
    public void deleteRandoPair(RandoPair randoPair) {
        long id = randoPair.id;
        database.delete(RandoDBHelper.RandoTable.NAME, RandoDBHelper.RandoTable.COLUMN_ID
                + " = " + id, null);
        Log.i(RandoDAO.class, "RandoPair deleted with id: ", String.valueOf(id));
    }

    /**
     * clear rando pairs Table in DB.
     */
    public void clearRandoPairs() {
        database.delete(RandoDBHelper.RandoTable.NAME, "", null);
        Log.i(RandoDAO.class, "RandoPairs cleared");
    }


    /**
     * Updates randoPair instance from DB.
     *
     * @param randoPair RandoPair to update
     */
    public void updateRandoPair(RandoPair randoPair) {
        long id = randoPair.id;

        ContentValues values = randoPairToContentValues(randoPair);

        database.update(RandoDBHelper.RandoTable.NAME, values, RandoDBHelper.RandoTable.COLUMN_ID + " = " + id, null);
        Log.w(RandoDAO.class, "RandoPair updated with id: ", String.valueOf(id));
    }

    //TODO: NEED TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    public List<RandoPair> getAllRandos() {
        List<RandoPair> randos = new ArrayList<RandoPair>();

        List<RandoUpload> randosToUpload = getAllRandosToUpload();
        for (RandoUpload randoUpload : randosToUpload) {
            RandoPair randoPair = new RandoPair();
            randoPair.user.randoId = String.valueOf(randoUpload.id);
            randoPair.user.date = randoUpload.date;
            randoPair.user.imageURL = randoUpload.file;
            randoPair.user.imageURLSize.small = randoUpload.file;
            randoPair.user.imageURLSize.medium = randoUpload.file;
            randoPair.user.imageURLSize.large = randoUpload.file;
            randos.add(randoPair);
        }
        List<RandoPair> randoPairs = getAllRandoPairs();
        randos.addAll(randoPairs);
        return randos;
    }


    //TODO: NEED TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    public int getAllRandosNumber() {
        int randoPairsNumber = getRandoPairsNumber();
        int randoToUploads = getRandosToUploadNumber();
        return randoPairsNumber + randoToUploads;
    }

    //TODO: NEED TEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    public int getRandosToUploadNumber() {
        Cursor cursor = database.query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     * @return all rando instances found in DB
     */
    public List<RandoPair> getAllRandoPairs() {
        List<RandoPair> randoPairs = new ArrayList<RandoPair>();

        Cursor cursor = database.query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, null, null, null, null, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RandoPair randoPair = cursorToRandoPair(cursor);
            randoPairs.add(randoPair);
            cursor.moveToNext();
        }
        cursor.close();

        return randoPairs;
    }

    /**
     * Counts randos amount in DB
     *
     * @return randos amount in DB
     */
    public int getRandoPairsNumber() {
        Cursor cursor = database.query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }


    /**
     * Counts not paired randos amount in DB
     *
     * @return not paired randos amount in DB
     */
    public int getNotPairedRandosNumber() {
        Cursor cursor = database.query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_ID
                        + " is null or " + RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_ID + " = ''", null, null, null, null
        );
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     * Inserts list of randoPairs into DB
     *
     * @param randoPairs RandoPair list to insert
     */
    public void insertRandoPairs(List<RandoPair> randoPairs) {
        database.beginTransaction();
        try {
            for (RandoPair randoPair : randoPairs) {
                createRandoPair(randoPair);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Begins new Transaction
     * USED ONLY BY TESTS
     */
    public void beginTransaction() {
        database.beginTransaction();
    }

    /**
     * Ends current Transaction
     * USED ONLY BY TESTS
     */
    public void endTransaction() {
        database.endTransaction();
    }

    /**
     * Extracts RandoPair object from Cursor object
     *
     * @param cursor Cursor pointing to row
     * @return rando object extracted from cursor
     */

    private RandoPair cursorToRandoPair(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        } else {
            RandoPair randoPair = new RandoPair();
            randoPair.id = cursor.getInt(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_ID));

            randoPair.user.randoId = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID));
            randoPair.user.imageURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL));
            randoPair.user.imageURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_SMALL));
            randoPair.user.imageURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_MEDIUM));
            randoPair.user.imageURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_LARGE));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE));
            randoPair.user.date = userDate != 0 ? new Date(userDate) : null;
            randoPair.user.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL));
            randoPair.user.mapURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_SMALL));
            randoPair.user.mapURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_MEDIUM));
            randoPair.user.mapURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_LARGE));

            randoPair.stranger.randoId = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_ID));
            randoPair.stranger.imageURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL));
            randoPair.stranger.imageURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_SMALL));
            randoPair.stranger.imageURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_MEDIUM));
            randoPair.stranger.imageURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_LARGE));
            long strangerDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_DATE));
            randoPair.stranger.date = strangerDate != 0 ? new Date(strangerDate) : null;
            randoPair.stranger.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL));
            randoPair.stranger.mapURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_SMALL));
            randoPair.stranger.mapURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_MEDIUM));
            randoPair.stranger.mapURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_LARGE));

            return randoPair;
        }
    }

    /**
     * Converts RandoPair object into ContentValues object
     *
     * @param randoPair RandoPair to convert
     * @return ContentValues representing randoPair
     */
    private ContentValues randoPairToContentValues(RandoPair randoPair) {
        ContentValues values = new ContentValues();

        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID, randoPair.user.randoId);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL, randoPair.user.imageURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_SMALL, randoPair.user.imageURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_MEDIUM, randoPair.user.imageURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_LARGE, randoPair.user.imageURLSize.large);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE, randoPair.user.date.getTime());
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL, randoPair.user.mapURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_SMALL, randoPair.user.mapURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_MEDIUM, randoPair.user.mapURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_LARGE, randoPair.user.mapURLSize.large);

        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_ID, randoPair.stranger.randoId);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL, randoPair.stranger.imageURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_SMALL, randoPair.stranger.imageURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_MEDIUM, randoPair.stranger.imageURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_URL_LARGE, randoPair.stranger.imageURLSize.large);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_RANDO_DATE, randoPair.stranger.date != null ? randoPair.stranger.date.getTime() : null);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL, randoPair.stranger.mapURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_SMALL, randoPair.stranger.mapURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_MEDIUM, randoPair.stranger.mapURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_STRANGER_MAP_URL_LARGE, randoPair.stranger.mapURLSize.large);

        return values;
    }
}
