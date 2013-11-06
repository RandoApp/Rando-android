package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.eucsoft.foodex.db.model.FoodPair;

public class FoodPairTest extends AndroidTestCase {


    //isBonAppetit Tests
    @SmallTest
    public void testIsBonAppetit0() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 0;
        foodPair.stranger.bonAppetit = 0;
        assertFalse(foodPair.user.isBonAppetit());
        assertFalse(foodPair.stranger.isBonAppetit());
    }

    @SmallTest
    public void testIsBonAppetit1() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 1;
        foodPair.stranger.bonAppetit = 1;
        assertTrue(foodPair.user.isBonAppetit());
        assertTrue(foodPair.stranger.isBonAppetit());
    }

    @SmallTest
    public void testIsBonAppetitDifferentVals() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 0;
        foodPair.stranger.bonAppetit = 1;
        assertFalse(foodPair.user.isBonAppetit());
        assertTrue(foodPair.stranger.isBonAppetit());
    }

    @SmallTest
    public void testIsBonAppetitDifferentVals2() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 1;
        foodPair.stranger.bonAppetit = 0;
        assertTrue(foodPair.user.isBonAppetit());
        assertFalse(foodPair.stranger.isBonAppetit());
    }

    // GetFoodFileName Tests
    @SmallTest
    public void testUserGetFoodFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = "http://stackoverflow.com/questions/1945213/what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg";
        assertEquals("what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg", foodPair.user.getFoodFileName());
    }

    @SmallTest
    public void testStrangerGetFoodFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = "http://stackoverflow.com/questions/1945213/what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg";
        assertEquals("what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg", foodPair.stranger.getFoodFileName());
    }

    @SmallTest
    public void testUserGetFoodFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = null;
        assertNull(foodPair.user.getFoodFileName());
    }

    @SmallTest
    public void testStrangerGetFoodFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = null;
        assertNull(foodPair.stranger.getFoodFileName());
    }

    // GetMapFileName Tests
    @SmallTest
    public void testUserGetMapFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.mapURL = "http://stackoverflow.com/questions/1945213/what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg";
        assertEquals("what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg", foodPair.user.getMapFileName());
    }

    @SmallTest
    public void testStrangerGetMapFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.mapURL = "http://stackoverflow.com/questions/1945213/what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg";
        assertEquals("what-is-eclipses-ctrlo-shortcut-equivalent-in-intellij-idea.jpg", foodPair.stranger.getMapFileName());
    }

    @SmallTest
    public void testUserGetMapFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = null;
        assertNull(foodPair.user.getMapFileName());
    }

    @SmallTest
    public void testStrangerGetMapFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = null;
        assertNull(foodPair.stranger.getMapFileName());
    }
}