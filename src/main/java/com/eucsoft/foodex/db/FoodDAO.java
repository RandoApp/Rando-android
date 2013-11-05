package com.eucsoft.foodex.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eucsoft.foodex.db.model.FoodPair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodDAO {

    // Database fields
    private SQLiteDatabase database;
    private FoodDBHelper foodDBHelper;
    private String[] allColumns = {FoodDBHelper.COLUMN_ID,
            FoodDBHelper.COLUMN_USER_FOOD_URL, FoodDBHelper.COLUMN_USER_FOOD_DATE, FoodDBHelper.COLUMN_USER_BON_APPETIT, FoodDBHelper.COLUMN_USER_MAP_URL,
            FoodDBHelper.COLUMN_STRANGER_FOOD_URL, FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, FoodDBHelper.COLUMN_STRANGER_MAP_URL};

    public FoodDAO(Context context) {
        foodDBHelper = new FoodDBHelper(context);
        database = foodDBHelper.getWritableDatabase();

    }

    public void close() {
        foodDBHelper.close();
    }

    /**
     * Creates foodPair and returns instance of created foodPair.
     *
     * @param foodPair
     * @return returns instance of created foodPair.
     */

    public FoodPair createFood(FoodPair foodPair) {

        ContentValues values = foodToContentValues(foodPair);

        long insertId = database.insert(FoodDBHelper.TABLE_FOOD, null,
                values);
        return getFoodById(insertId);
    }

    /**
     * Finds food instance.
     *
     * @param id
     * @return FoodPair instance or null if food hasn't been found
     */

    public FoodPair getFoodById(long id) {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, FoodDBHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        FoodPair newFoodPair = cursorToFood(cursor);
        cursor.close();
        return newFoodPair;
    }

    /**
     * Deletes foodPair instance from DB.
     *
     * @param foodPair
     */
    public void deleteFood(FoodPair foodPair) {
        long id = foodPair.id;
        database.delete(FoodDBHelper.TABLE_FOOD, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "FoodPair deleted with id: " + id);
    }


    /**
     * Updates foodPair instance from DB.
     *
     * @param foodPair
     */
    public void updateFood(FoodPair foodPair) {
        long id = foodPair.id;

        ContentValues values = foodToContentValues(foodPair);

        database.update(FoodDBHelper.TABLE_FOOD, values, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "FoodPair updated with id: " + id);
    }


    /**
     * @return all food instances found in DB
     */
    public List<FoodPair> getAllFoods() {
        List<FoodPair> foodPairs = new ArrayList<FoodPair>();

        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FoodPair foodPair = cursorToFood(cursor);
            foodPairs.add(foodPair);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return foodPairs;
    }

    /**
     * Counts foods amount in DB
     *
     * @return foods amount in DB
     */
    public int getAllFoodsCount() {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, null);
        int result = cursor.getCount();

        // make sure to close the cursor
        cursor.close();
        return result;
    }

    /**
     * Inserts list of foodPairs into DB
     *
     * @param foodPairs
     */
    public void insertFoods(List<FoodPair> foodPairs) {
        database.beginTransaction();
        try {
            for (FoodPair foodPair : foodPairs) {
                createFood(foodPair);
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
     * @param cursor
     * @return food object extracted from cursor
     */

    private FoodPair cursorToFood(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        } else {
            FoodPair foodPair = new FoodPair();
            foodPair.id = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_ID));

            foodPair.user.foodURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_URL));
            long userDate = cursor.getLong(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_DATE));
            foodPair.user.foodDate = userDate != 0 ? new Date(userDate) : null;
            foodPair.user.bonAppetit = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_BON_APPETIT));
            foodPair.user.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_MAP_URL));

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
     * @param foodPair
     * @return ContentValues representing foodPair
     */
    private ContentValues foodToContentValues(FoodPair foodPair) {
        ContentValues values = new ContentValues();

        values.put(FoodDBHelper.COLUMN_USER_FOOD_URL, foodPair.user.foodURL);
        values.put(FoodDBHelper.COLUMN_USER_FOOD_DATE, foodPair.user.foodDate.getTime());
        values.put(FoodDBHelper.COLUMN_USER_BON_APPETIT, foodPair.user.bonAppetit);
        values.put(FoodDBHelper.COLUMN_USER_MAP_URL, foodPair.user.mapURL);

        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_URL, foodPair.stranger.foodURL);
        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, foodPair.stranger.foodDate != null ? foodPair.stranger.foodDate.getTime() : null);
        values.put(FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, foodPair.stranger.bonAppetit);
        values.put(FoodDBHelper.COLUMN_STRANGER_MAP_URL, foodPair.stranger.mapURL);

        return values;
    }
}
