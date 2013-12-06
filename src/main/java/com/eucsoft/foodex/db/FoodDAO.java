package com.eucsoft.foodex.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.model.FoodPair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodDAO {

    // Database fields
    private SQLiteDatabase database;
    private FoodDBHelper foodDBHelper;
    private String[] allColumns = {FoodDBHelper.COLUMN_ID,
            FoodDBHelper.COLUMN_USER_FOOD_ID, FoodDBHelper.COLUMN_USER_FOOD_URL, FoodDBHelper.COLUMN_USER_FOOD_DATE, FoodDBHelper.COLUMN_USER_BON_APPETIT, FoodDBHelper.COLUMN_USER_MAP_URL,
            FoodDBHelper.COLUMN_STRANGER_FOOD_ID, FoodDBHelper.COLUMN_STRANGER_FOOD_URL, FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, FoodDBHelper.COLUMN_STRANGER_MAP_URL};

    public FoodDAO(Context context) {
        foodDBHelper = new FoodDBHelper(context);
        database = foodDBHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        foodDBHelper.close();
    }

    /**
     * Creates foodPair and returns instance of created foodPair.
     *
     * @param foodPair FoodPair to insert
     * @return returns instance of created foodPair or null
     */

    public FoodPair createFoodPair(FoodPair foodPair) {
        if (foodPair == null) {
            return null;
        }
        ContentValues values = foodPairToContentValues(foodPair);
        long insertId = database.insert(FoodDBHelper.TABLE_FOOD, null,
                values);
        return getFoodPairById(insertId);
    }

    /**
     * Finds food instance.
     *
     * @param id FoodPair id to get
     * @return FoodPair instance or null if food hasn't been found
     */

    public FoodPair getFoodPairById(long id) {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, FoodDBHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        FoodPair newFoodPair = cursorToFoodPair(cursor);
        cursor.close();
        return newFoodPair;
    }

    /**
     * Deletes foodPair instance from DB.
     *
     * @param foodPair FoodPair to delete
     */
    public void deleteFoodPair(FoodPair foodPair) {
        long id = foodPair.id;
        database.delete(FoodDBHelper.TABLE_FOOD, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "FoodPair deleted with id: " + id);
    }

    /**
     * clear foor pairs Table in DB.
     */
    public void clearFoodPairs() {
        database.delete(FoodDBHelper.TABLE_FOOD, "", null);
        Log.w(FoodDBHelper.TAG, "FoodPairs cleared");
    }


    /**
     * Updates foodPair instance from DB.
     *
     * @param foodPair FoodPair to update
     */
    public void updateFoodPair(FoodPair foodPair) {
        long id = foodPair.id;

        ContentValues values = foodPairToContentValues(foodPair);

        database.update(FoodDBHelper.TABLE_FOOD, values, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "FoodPair updated with id: " + id);
    }


    /**
     * @return all food instances found in DB
     */
    public List<FoodPair> getAllFoodPairs() {
        List<FoodPair> foodPairs = new ArrayList<FoodPair>();

        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, FoodDBHelper.COLUMN_USER_FOOD_DATE + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FoodPair foodPair = cursorToFoodPair(cursor);
            foodPairs.add(foodPair);
            cursor.moveToNext();
        }
        cursor.close();
        return foodPairs;
    }

    /**
     * Counts foods amount in DB
     *
     * @return foods amount in DB
     */
    public int getFoodPairsNumber() {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }


    /**
     * Counts not paired foods amount in DB
     *
     * @return not paired foods amount in DB
     */
    public int getNotPairedFoodsNumber() {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, FoodDBHelper.COLUMN_STRANGER_FOOD_ID
                + " is null or " + FoodDBHelper.COLUMN_STRANGER_FOOD_ID + " = ''", null, null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    /**
     * First page is a 0 page!!
     *
     * @return foods of current Page
     */
    public List<FoodPair> getFoodPairsForPage(int page) {
        List<FoodPair> foodPairs = new ArrayList<FoodPair>();

        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, FoodDBHelper.COLUMN_USER_FOOD_DATE + " DESC", Constants.PAGE_SIZE * page + ", " + Constants.PAGE_SIZE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FoodPair foodPair = cursorToFoodPair(cursor);
            foodPairs.add(foodPair);
            cursor.moveToNext();
        }
        cursor.close();
        return foodPairs;
    }

    /**
     * First page is a 0 page!!
     *
     * @return foods of current Page
     */
    public int getPagesNumber() {
        double foodPairsNumber = getFoodPairsNumber();

        return (int) Math.ceil(foodPairsNumber / Constants.PAGE_SIZE);
    }

    /**
     * Inserts list of foodPairs into DB
     *
     * @param foodPairs FoodPair list to insert
     */
    public void insertFoodPairs(List<FoodPair> foodPairs) {
        database.beginTransaction();
        try {
            for (FoodPair foodPair : foodPairs) {
                createFoodPair(foodPair);
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
     * Extracts FoodPair object from Cursor object
     *
     * @param cursor Cursor pointing to row
     * @return food object extracted from cursor
     */

    private FoodPair cursorToFoodPair(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        } else {
            FoodPair foodPair = new FoodPair();
            foodPair.id = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_ID));

            foodPair.user.foodId = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_ID));
            foodPair.user.foodURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_URL));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_DATE));
            foodPair.user.foodDate = userDate != 0 ? new Date(userDate) : null;
            foodPair.user.bonAppetit = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_BON_APPETIT));
            foodPair.user.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_MAP_URL));

            foodPair.stranger.foodId = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_STRANGER_FOOD_ID));
            foodPair.stranger.foodURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_STRANGER_FOOD_URL));
            long strangerDate = cursor.getLong(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_STRANGER_FOOD_DATE));
            foodPair.stranger.foodDate = strangerDate != 0 ? new Date(strangerDate) : null;
            foodPair.stranger.bonAppetit = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_STRANGER_BON_APPETIT));
            foodPair.stranger.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_STRANGER_MAP_URL));

            return foodPair;
        }
    }

    /**
     * Converts FoodPair object into ContentValues object
     *
     * @param foodPair FoodPair to convert
     * @return ContentValues representing foodPair
     */
    private ContentValues foodPairToContentValues(FoodPair foodPair) {
        ContentValues values = new ContentValues();

        values.put(FoodDBHelper.COLUMN_USER_FOOD_ID, foodPair.user.foodId);
        values.put(FoodDBHelper.COLUMN_USER_FOOD_URL, foodPair.user.foodURL);
        values.put(FoodDBHelper.COLUMN_USER_FOOD_DATE, foodPair.user.foodDate.getTime());
        values.put(FoodDBHelper.COLUMN_USER_BON_APPETIT, foodPair.user.bonAppetit);
        values.put(FoodDBHelper.COLUMN_USER_MAP_URL, foodPair.user.mapURL);

        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_ID, foodPair.stranger.foodId);
        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_URL, foodPair.stranger.foodURL);
        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, foodPair.stranger.foodDate != null ? foodPair.stranger.foodDate.getTime() : null);
        values.put(FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, foodPair.stranger.bonAppetit);
        values.put(FoodDBHelper.COLUMN_STRANGER_MAP_URL, foodPair.stranger.mapURL);

        return values;
    }
}
