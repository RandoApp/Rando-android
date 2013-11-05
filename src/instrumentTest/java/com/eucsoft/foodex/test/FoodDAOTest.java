package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.Food;

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
        Food food = new Food();

        food.user.foodURL = "blaURL";
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testCreatePairedFood() throws SQLException {
        Food food = new Food();

        food.user.foodURL = "blaURL";
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        food.stranger.foodURL = "Bla2URL";
        food.stranger.mapURL = "LocalFileStranger";
        food.stranger.bonAppetit = 0;
        food.stranger.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testCreateFreshFood() throws SQLException {
        Food food = new Food();

        food.user.foodURL = null;
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testDeleteFood() throws SQLException {
        Food food = new Food();

        food.user.foodURL = null;
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));

        long id = newFood.id;
        foodDAO.deleteFood(newFood);
        assertNull(foodDAO.getFoodById(id));
    }

    public void testUpdateFood() throws SQLException {
        Food food = new Food();

        food.user.foodURL = null;
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
        long id = newFood.id;

        String newMapValue = "MAP1";
        newFood.user.mapURL = newMapValue;
        foodDAO.updateFood(newFood);

        Food updatedFood = foodDAO.getFoodById(id);
        assertNotNull(updatedFood);
        assertEquals(newMapValue, updatedFood.user.mapURL);
    }

    public void testSelectFood() throws SQLException {
        Food food = new Food();
        food.user.foodURL = "blaURL";
        food.user.mapURL = "blaFile";
        food.user.bonAppetit = 0;
        food.user.foodDate = new Date();

        food.stranger.foodURL = "Bla2URL";
        food.stranger.mapURL = "LocalFileStranger";
        food.stranger.bonAppetit = 0;
        food.stranger.foodDate = new Date();

        Food newFood = foodDAO.createFood(food);
        newFood = foodDAO.getFoodById(newFood.id);
        assertTrue(food.equals(newFood));
    }
}
