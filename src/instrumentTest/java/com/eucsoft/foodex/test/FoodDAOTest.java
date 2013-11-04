package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.Food;

import java.sql.SQLException;

public class FoodDAOTest extends AndroidTestCase {

    private FoodDAO foodDAO;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        foodDAO = new FoodDAO(getContext());
        foodDAO.open();

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

        food.setUserPhotoURL("blaURL");
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap("MAP0");

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testCreatePairedFood() throws SQLException {
        Food food = new Food();

        food.setUserPhotoURL("blaURL");
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap("MAP0");

        food.setStrangerPhotoURL("Bla2URL");
        food.setStrangerLocalFile("LocalFileStranger");
        food.setStrangerLiked(0);
        food.setStrangerMap("<Map1");

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testCreateFreshFood() throws SQLException {
        Food food = new Food();

        food.setUserPhotoURL(null);
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap(null);

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
    }

    public void testDeletesFood() throws SQLException {
        Food food = new Food();

        food.setUserPhotoURL(null);
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap(null);

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));

        long id = newFood.getId();
        foodDAO.deleteFood(newFood);
        assertNull(foodDAO.getFoodById(id));
    }

    public void testUpdateFood() throws SQLException {
        Food food = new Food();

        food.setUserPhotoURL(null);
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap(null);

        int count = foodDAO.getAllFoodsCount();
        Food newFood = foodDAO.createFood(food);
        assertEquals(count + 1, foodDAO.getAllFoodsCount());
        assertTrue(food.equals(newFood));
        long id = newFood.getId();

        String newMapValue = "MAP1";
        newFood.setUserMap(newMapValue);
        foodDAO.updateFood(newFood);

        Food updatedFood = foodDAO.getFoodById(id);
        assertNotNull(updatedFood);
        assertEquals(newMapValue, updatedFood.getUserMap());
    }

    public void testSelectFood() throws SQLException {
        Food food = new Food();
        food.setUserPhotoURL("blaURL");
        food.setUserLocalFile("blaFile");
        food.setUserLiked(0);
        food.setUserMap("MAP0");

        food.setStrangerPhotoURL("Bla2URL");
        food.setStrangerLocalFile("LocalFileStranger");
        food.setStrangerLiked(0);
        food.setStrangerMap("<Map1");

        Food newFood = foodDAO.createFood(food);
        newFood = foodDAO.getFoodById(newFood.getId());
        assertTrue(food.equals(newFood));
    }
}
