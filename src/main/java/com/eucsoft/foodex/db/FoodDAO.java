package com.eucsoft.foodex.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eucsoft.foodex.db.model.Food;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodDAO {

    // Database fields
    private SQLiteDatabase database;
    private FoodDBHelper foodDBHelper;
    private String[] allColumns = {FoodDBHelper.COLUMN_ID,
            FoodDBHelper.COLUMN_USER_FOOD_URL, FoodDBHelper.COLUMN_USER_FOOD_DATE, FoodDBHelper.COLUMN_USER_BON_APPETIT, FoodDBHelper.COLUMN_USER_MAP_URL,
            FoodDBHelper.COLUMN_STRANGER_FOOD_URL, FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, FoodDBHelper.COLUMN_STRANGER_MAP};

    public FoodDAO(Context context) {
        foodDBHelper = new FoodDBHelper(context);
        database = foodDBHelper.getWritableDatabase();

    }

    public void close() {
        foodDBHelper.close();
    }

    /**
     * Creates food and returns instance of created food.
     *
     * @param food
     * @return returns instance of created food.
     */

    public Food createFood(Food food) {

        ContentValues values = foodToContentValues(food);

        long insertId = database.insert(FoodDBHelper.TABLE_FOOD, null,
                values);
        return getFoodById(insertId);
    }

    /**
     * Finds food instance.
     *
     * @param id
     * @return Food instance or null if food hasn't been found
     */

    public Food getFoodById(long id) {
        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, FoodDBHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Food newFood = cursorToFood(cursor);
        cursor.close();
        return newFood;
    }

    /**
     * Deletes food instance from DB.
     *
     * @param food
     */
    public void deleteFood(Food food) {
        long id = food.id;
        database.delete(FoodDBHelper.TABLE_FOOD, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "Food deleted with id: " + id);
    }


    /**
     * Updates food instance from DB.
     *
     * @param food
     */
    public void updateFood(Food food) {
        long id = food.id;

        ContentValues values = foodToContentValues(food);

        database.update(FoodDBHelper.TABLE_FOOD, values, FoodDBHelper.COLUMN_ID
                + " = " + id, null);
        Log.w(FoodDBHelper.TAG, "Food updated with id: " + id);
    }


    /**
     * @return all food instances found in DB
     */
    public List<Food> getAllFoods() {
        List<Food> foods = new ArrayList<Food>();

        Cursor cursor = database.query(FoodDBHelper.TABLE_FOOD,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Food food = cursorToFood(cursor);
            foods.add(food);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return foods;
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
     * Inserts list of foods into DB
     *
     * @param foods
     */
    public void insertFoods(List<Food> foods) {
        database.beginTransaction();
        try {
            for (Food food : foods) {
                createFood(food);
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
     * Extracts Food object from Cursor object
     *
     * @param cursor
     * @return food object extracted from cursor
     */

    private Food cursorToFood(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        } else {
            Food food = new Food();
            food.id = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_ID));

            food.user.foodURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_URL));
            food.user.foodDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_DATE)));
            food.user.bonAppetit = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_BON_APPETIT));
            food.user.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_MAP_URL));

            food.stranger.foodURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_URL));
            food.stranger.foodDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_FOOD_DATE)));
            food.stranger.bonAppetit = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_BON_APPETIT));
            food.stranger.mapURL = cursor.getString(cursor.getColumnIndexOrThrow(FoodDBHelper.COLUMN_USER_MAP_URL));

            return food;
        }
    }

    /**
     * Converts Food object into ContentValues object
     *
     * @param food
     * @return ContentValues representing food
     */
    private ContentValues foodToContentValues(Food food) {
        ContentValues values = new ContentValues();

        values.put(FoodDBHelper.COLUMN_USER_FOOD_DATE, food.user.foodDate.getTime());
        values.put(FoodDBHelper.COLUMN_USER_FOOD_URL, food.user.foodURL);
        values.put(FoodDBHelper.COLUMN_USER_BON_APPETIT, food.user.bonAppetit);
        values.put(FoodDBHelper.COLUMN_USER_MAP_URL, food.user.mapURL);

        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_URL, food.stranger.foodURL);
        values.put(FoodDBHelper.COLUMN_STRANGER_FOOD_DATE, food.stranger.foodDate.getTime());
        values.put(FoodDBHelper.COLUMN_STRANGER_BON_APPETIT, food.stranger.bonAppetit);
        values.put(FoodDBHelper.COLUMN_STRANGER_MAP, food.stranger.mapURL);

        return values;
    }
}
