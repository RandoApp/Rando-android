package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;

import java.sql.SQLException;
import java.util.Date;

public class FoodDAOTest extends AndroidTestCase {

    private FoodDAO foodDAO;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        foodDAO = new FoodDAO(getContext());
        foodDAO.beginTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        foodDAO.endTransaction();
        foodDAO.close();
        foodDAO = null;
    }

    public void testCreateNotPairedFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(foodPair.equals(newFoodPair));
    }

    public void testCreatePairedFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        foodPair.stranger.foodURL = "Bla2URL";
        foodPair.stranger.mapURL = "LocalFileStranger";
        foodPair.stranger.bonAppetit = 0;
        foodPair.stranger.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(foodPair.equals(newFoodPair));
    }

    public void testCreateFreshFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(foodPair.equals(newFoodPair));
    }

    public void testDeleteFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(foodPair.equals(newFoodPair));

        long id = newFoodPair.id;
        foodDAO.deleteFood(newFoodPair);
        assertNull(foodDAO.getFoodById(id));
    }

    public void testUpdateFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(foodPair.equals(newFoodPair));
        long id = newFoodPair.id;

        String newMapValue = "MAP1";
        newFoodPair.user.mapURL = newMapValue;
        foodDAO.updateFood(newFoodPair);

        FoodPair updatedFoodPair = foodDAO.getFoodById(id);
        assertNotNull(updatedFoodPair);
        assertEquals(newMapValue, updatedFoodPair.user.mapURL);
    }

    public void testSelectFood() throws SQLException {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        foodPair.stranger.foodURL = "Bla2URL";
        foodPair.stranger.mapURL = "LocalFileStranger";
        foodPair.stranger.bonAppetit = 0;
        foodPair.stranger.foodDate = new Date();

        FoodPair newFoodPair = foodDAO.createFood(foodPair);
        newFoodPair = foodDAO.getFoodById(newFoodPair.id);
        assertTrue(foodPair.equals(newFoodPair));
    }
}
