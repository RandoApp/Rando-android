package com.github.randoapp.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RandoDAO {

    private RandoDAO() {
    }

    public static synchronized RandoUpload addToUpload(RandoUpload randoUpload) {
        if (randoUpload.file == null || randoUpload.date == null) {
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_FILE, randoUpload.file);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE, randoUpload.latitude);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE, randoUpload.longitude);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_DATE, randoUpload.date.getTime());
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LAST_TRY_DATE, randoUpload.lastTry.getTime());

        long id = getDB().insert(RandoDBHelper.RandoUploadTable.NAME, null, values);

        if (id > 0) {
            randoUpload.id = (int) id;
            return randoUpload;
        } else {
            return null;
        }
    }

    public static synchronized int countAllRandosToUpload() {
        Cursor cursor = getDB().query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, null, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    public static synchronized List<RandoUpload> getAllRandosToUpload(String sortOrder) {
        List<RandoUpload> randos = new ArrayList<>();

        Cursor cursor = getDB().query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, null, null, null, null, RandoDBHelper.RandoUploadTable.COLUMN_DATE + " " + sortOrder, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RandoUpload rando = new RandoUpload();
            rando.id = cursor.getInt(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_ID));
            rando.file = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_FILE));
            rando.latitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE));
            rando.longitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_DATE));
            long lastTryDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LAST_TRY_DATE));
            if (userDate > 0) {
                rando.date = new Date(userDate);
            } else {
                rando.date = new Date();
            }
            if (lastTryDate > 0) {
                rando.lastTry = new Date(lastTryDate);
            } else {
                rando.lastTry = new Date(0);
            }
            randos.add(rando);
            cursor.moveToNext();
        }
        cursor.close();
        return randos;
    }

    public static synchronized RandoUpload getNextRandoToUpload() {
        String dateS = String.valueOf(System.currentTimeMillis()+Constants.UPLOAD_RETRY_TIMEOUT);
        Cursor cursor = getDB().query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, RandoDBHelper.RandoUploadTable.COLUMN_LAST_TRY_DATE
                        + "<" + dateS, null, null, null, RandoDBHelper.RandoUploadTable.COLUMN_DATE + " ASC", "1");

        cursor.moveToFirst();
        RandoUpload rando = null;
        if (!cursor.isAfterLast()) {
            rando = new RandoUpload();
            rando.id = cursor.getInt(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_ID));
            rando.file = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_FILE));
            rando.latitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE));
            rando.longitude = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_DATE));
            long lastTryDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoUploadTable.COLUMN_LAST_TRY_DATE));
            if (userDate > 0) {
                rando.date = new Date(userDate);
            } else {
                rando.date = new Date();
            }
            if (lastTryDate > 0) {
                rando.lastTry = new Date(lastTryDate);
            } else {
                rando.lastTry = new Date(0);
            }
            cursor.close();
        }
        return rando;
    }

    public static synchronized void updateRandoToUpload(RandoUpload rando) {
        ContentValues values = new ContentValues();
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_FILE, rando.file);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LATITUDE, rando.latitude);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LONGITUDE, rando.longitude);
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_DATE, rando.date.getTime());
        values.put(RandoDBHelper.RandoUploadTable.COLUMN_LAST_TRY_DATE, rando.lastTry.getTime());

        getDB().update(RandoDBHelper.RandoUploadTable.NAME, values, RandoDBHelper.RandoTable.COLUMN_ID + " = " + rando.id, null);
    }

    public static synchronized void deleteRandoToUpload(RandoUpload rando) {
        String id = String.valueOf(rando.id);
        getDB().delete(RandoDBHelper.RandoUploadTable.NAME, RandoDBHelper.RandoUploadTable.COLUMN_ID + " = " + id, null);
        Log.i(RandoDAO.class, "Rando to upload deleted with id: ", String.valueOf(id));
    }

    public static synchronized void clearRandoToUpload() {
        getDB().delete(RandoDBHelper.RandoUploadTable.NAME, "", null);
        Log.i(RandoDAO.class, "RandosToUpload cleared");
    }

    /**
     * Creates rando and returns instance of created rando.
     *
     * @param rando Rando to insert
     * @return returns instance of created rando or null
     */

    public static synchronized Rando createRando(Rando rando) {
        if (rando == null) {
            return null;
        }
        ContentValues values = randoToContentValues(rando);
        long insertId = getDB().insert(RandoDBHelper.RandoTable.NAME, null,
                values);
        return getRandoById(insertId);
    }

    /**
     * Creates rando and returns instance of created rando.
     *
     * @param rando Rando to insert
     * @return returns instance of created rando or null
     */

    public static synchronized Rando createOrUpdateRandoCheckingByRandoId(Rando rando) {
        if (rando == null) {
            return null;
        }
        Rando dbRando = getRandoByRandoId(rando.randoId);
        long dbId;
        if (dbRando != null) {
            rando.id = dbRando.id;
            dbId = dbRando.id;
            updateRando(rando);
        } else {
            ContentValues values = randoToContentValues(rando);
            dbId = getDB().insert(RandoDBHelper.RandoTable.NAME, null,
                    values);
        }
        return getRandoById(dbId);
    }

    /**
     * Finds rando instance.
     *
     * @param id Rando id to get
     * @return Rando instance or null if rando hasn't been found
     */

    public static synchronized Rando getRandoById(long id) {
        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Rando newRando = cursorToRando(cursor);
        cursor.close();
        return newRando;
    }

    /**
     * Finds rando instance.
     *
     * @param randoId Rando id to get
     * @return Rando instance or null if rando hasn't been found
     */

    public static synchronized Rando getRandoByRandoId(String randoId) {
        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID + " = '" + randoId + "'", null,
                null, null, null);
        cursor.moveToFirst();
        Rando rando = cursorToRando(cursor);
        cursor.close();
        return rando;
    }

    /**
     * Deletes rando instance from DB.
     *
     * @param rando Rando to delete
     */
    public static synchronized void deleteRando(Rando rando) {
        long id = rando.id;
        getDB().delete(RandoDBHelper.RandoTable.NAME, RandoDBHelper.RandoTable.COLUMN_ID
                + " = " + id, null);
        Log.i(RandoDAO.class, "Rando deleted with id: ", String.valueOf(id));
    }

    /**
     * Deletes rando instance from DB by randoId.
     *
     * @param randoId randoId
     */
    public static synchronized void deleteRandoByRandoId(String randoId) {
        getDB().delete(RandoDBHelper.RandoTable.NAME, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID
                + " = '" + randoId + "'", null);
        Log.i(RandoDAO.class, "Rando deleted with randoId: ", String.valueOf(randoId));
    }

    /**
     * clear rando pairs Table in DB.
     */
    public static synchronized void clearRandos() {
        getDB().delete(RandoDBHelper.RandoTable.NAME, "", null);
        Log.i(RandoDAO.class, "Randos table cleared");
    }

    /**
     * clear rando pairs Table in DB.
     */
    public static synchronized void clearInRandos() {
        getDB().delete(RandoDBHelper.RandoTable.NAME, RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS
                + " = '" + Rando.Status.IN.name() + "'", null);
        Log.i(RandoDAO.class, "Randos table cleared");
    }

    /**
     * clear rando pairs Table in DB.
     */
    public static synchronized void clearOutRandos() {
        getDB().delete(RandoDBHelper.RandoTable.NAME, RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS
                + " = '" + Rando.Status.OUT.name() + "'", null);
        Log.i(RandoDAO.class, "Randos table cleared");
    }


    /**
     * Updates rando instance from DB.
     *
     * @param rando Rando to update
     */
    public static synchronized void updateRando(Rando rando) {
        long id = rando.id;

        ContentValues values = randoToContentValues(rando);

        getDB().update(RandoDBHelper.RandoTable.NAME, values, RandoDBHelper.RandoTable.COLUMN_ID + " = " + id, null);
        Log.w(RandoDAO.class, "Rando updated with id: ", String.valueOf(id));
    }

    public static synchronized List<Rando> getAllRandos(boolean includeRandosFromUpload) {
        List<Rando> randos = new ArrayList<Rando>();

        List<RandoUpload> randosToUpload = getAllRandosToUpload("DESC");
        for (RandoUpload randoUpload : randosToUpload) {
            Rando rando = new Rando();
            if (includeRandosFromUpload) {
                rando.randoId = String.valueOf(randoUpload.id);
                rando.date = randoUpload.date;
                rando.imageURL = randoUpload.file;
                rando.imageURLSize.small = randoUpload.file;
                rando.imageURLSize.medium = randoUpload.file;
                rando.imageURLSize.large = randoUpload.file;
            }
            randos.add(rando);
        }
        List<Rando> dbRandos = getAllRandos();
        randos.addAll(dbRandos);
        Log.i(RandoDAO.class, "Size:", String.valueOf(dbRandos.size()), "include=", String.valueOf(includeRandosFromUpload));
        return randos;
    }

    public static synchronized int countAllRandosNumber() {
        return getRandosNumber() + getRandosToUploadNumber();
    }

    public static synchronized int getRandosToUploadNumber() {
        Cursor cursor = getDB().query(RandoDBHelper.RandoUploadTable.NAME,
                RandoDBHelper.RandoUploadTable.ALL_COLUMNS, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     * @return all rando instances found in DB
     */
    public static synchronized List<Rando> getAllRandos() {
        List<Rando> randos = new LinkedList<>();

        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, null, null, null, null, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rando rando = cursorToRando(cursor);
            randos.add(rando);
            cursor.moveToNext();
        }
        cursor.close();

        return randos;
    }

    /**
     * @return all incoming rando instances found in DB
     */
    public static synchronized List<Rando> getAllRandosByStatus(Rando.Status status) {
        List<Rando> randos = new LinkedList<>();

        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS
                        + " = '" + status.name() + "'", null, null, null, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rando rando = cursorToRando(cursor);
            randos.add(rando);
            cursor.moveToNext();
        }
        cursor.close();

        return randos;
    }

    /**
     * @return all incoming rando instances found in DB
     */
    public static synchronized List<Rando> getAllInRandos() {
        return getAllRandosByStatus(Rando.Status.IN);

    }

    /**
     * @return all outgoing rando instances found in DB
     */
    public static synchronized List<Rando> getAllOutRandos() {
        return getAllRandosByStatus(Rando.Status.OUT);

    }

    /**
     * @return all outgoing rando instances found in DB including Randos to Upload
     */
    public static synchronized List<Rando> getAllOutRandosWithUploadQueue() {
        List<Rando> randos = new ArrayList<>();
        List<RandoUpload> randosToUpload = getAllRandosToUpload("DESC");
        for (RandoUpload randoUpload : randosToUpload) {
            Rando rando = new Rando();
            rando.randoId = String.valueOf(Constants.TO_UPLOAD_RANDO_ID);
            rando.date = randoUpload.date;
            rando.imageURL = randoUpload.file;
            rando.imageURLSize.small = randoUpload.file;
            rando.imageURLSize.medium = randoUpload.file;
            rando.imageURLSize.large = randoUpload.file;
            randos.add(rando);
        }
        randos.addAll(getAllRandosByStatus(Rando.Status.OUT));

        return randos;
    }

    /**
     * @return count of all randos instances found in DB by status
     */
    public static synchronized int countAllRandosByStatus(Rando.Status status) {
        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS
                        + " = '" + status.name() + "'", null, null, null, RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE + " DESC", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * @return coint all incoming rando instances found in DB
     */
    public static synchronized int countAllInRandos() {
        return countAllRandosByStatus(Rando.Status.IN);

    }

    /**
     * @return count all outgoing rando instances found in DB
     */
    public static synchronized int countAllOutRandos() {
        return countAllRandosByStatus(Rando.Status.OUT);
    }

    /**
     * Counts randos amount in DB
     *
     * @return randos amount in DB
     */
    public static synchronized int getRandosNumber() {
        Cursor cursor = getDB().query(RandoDBHelper.RandoTable.NAME,
                RandoDBHelper.RandoTable.ALL_COLUMNS, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     * Inserts list of randos into DB
     *
     * @param randos Rando list to insert
     */
    public static synchronized void insertRandos(List<Rando> randos) {
        getDB().beginTransaction();
        try {
            for (Rando rando : randos) {
                createRando(rando);
            }
            getDB().setTransactionSuccessful();
        } finally {
            getDB().endTransaction();
        }
    }

    private static synchronized SQLiteDatabase getDB() {
        return RandoDBHelper.getDatabase(App.context);
    }

    /**
     * Extracts Rando object from Cursor object
     *
     * @param cursor Cursor pointing to row
     * @return rando object extracted from cursor
     */

    private static Rando cursorToRando(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        } else {
            Rando rando = new Rando();
            rando.id = cursor.getInt(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_ID));

            rando.randoId = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID));
            rando.imageURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL));
            rando.imageURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_SMALL));
            rando.imageURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_MEDIUM));
            rando.imageURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_LARGE));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE));
            rando.date = userDate != 0 ? new Date(userDate) : null;
            rando.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL));
            rando.mapURLSize.small = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_SMALL));
            rando.mapURLSize.medium = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_MEDIUM));
            rando.mapURLSize.large = cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_LARGE));
            rando.status = Rando.Status.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS)));

            return rando;
        }
    }

    /**
     * Converts Rando object into ContentValues object
     *
     * @param rando Rando to convert
     * @return ContentValues representing rando
     */
    private static ContentValues randoToContentValues(Rando rando) {
        ContentValues values = new ContentValues();

        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_ID, rando.randoId);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL, rando.imageURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_SMALL, rando.imageURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_MEDIUM, rando.imageURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_URL_LARGE, rando.imageURLSize.large);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_RANDO_DATE, rando.date.getTime());
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL, rando.mapURL);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_SMALL, rando.mapURLSize.small);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_MEDIUM, rando.mapURLSize.medium);
        values.put(RandoDBHelper.RandoTable.COLUMN_USER_MAP_URL_LARGE, rando.mapURLSize.large);
        values.put(RandoDBHelper.RandoTable.COLUMN_RANDO_STATUS, rando.status.name());

        return values;
    }
}
