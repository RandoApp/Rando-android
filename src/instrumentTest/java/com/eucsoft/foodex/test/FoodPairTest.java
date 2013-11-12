package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.eucsoft.foodex.db.model.FoodPair;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FoodPairTest extends AndroidTestCase {

    @SmallTest
    public void testFoodPairUsersNotNull() {
        FoodPair foodPair = new FoodPair();
        assertThat(foodPair.user, notNullValue());
        assertThat(foodPair.stranger, notNullValue());
    }

    //isBonAppetit Tests
    @SmallTest
    public void testIsBonAppetit0() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 0;
        foodPair.stranger.bonAppetit = 0;
        assertThat(foodPair.user.isBonAppetit(), is(false));
        assertThat(foodPair.stranger.isBonAppetit(), is(false));
    }

    @SmallTest
    public void testIsBonAppetit1() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 1;
        foodPair.stranger.bonAppetit = 1;
        assertThat(foodPair.user.isBonAppetit(), is(true));
        assertThat(foodPair.stranger.isBonAppetit(), is(true));
    }

    @SmallTest
    public void testIsBonAppetitDifferentVals() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 0;
        foodPair.stranger.bonAppetit = 1;
        assertThat(foodPair.user.isBonAppetit(), is(false));
        assertThat(foodPair.stranger.isBonAppetit(), is(true));
    }

    @SmallTest
    public void testIsBonAppetitDifferentVals2() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.bonAppetit = 1;
        foodPair.stranger.bonAppetit = 0;
        assertThat(foodPair.user.isBonAppetit(), is(true));
        assertThat(foodPair.stranger.isBonAppetit(), is(false));
    }

    // GetFoodFileName Tests
    @SmallTest
    public void testUserGetFoodFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(foodPair.user.getFoodFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testStrangerGetFoodFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(foodPair.stranger.getFoodFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testUserGetFoodFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = null;
        assertThat(foodPair.user.getFoodFileName(), nullValue());
    }

    @SmallTest
    public void testStrangerGetFoodFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = null;
        assertThat(foodPair.stranger.getFoodFileName(), nullValue());
    }

    // GetMapFileName Tests
    @SmallTest
    public void testUserGetMapFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.mapURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(foodPair.user.getMapFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testStrangerGetMapFileName() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.mapURL = "http://cool-projects.com/foodex/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(foodPair.stranger.getMapFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testUserGetMapFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = null;
        assertThat(foodPair.user.getMapFileName(), nullValue());
    }

    @SmallTest
    public void testStrangerGetMapFileNameNull() {
        FoodPair foodPair = new FoodPair();
        foodPair.stranger.foodURL = null;
        assertThat(foodPair.stranger.getMapFileName(), nullValue());
    }
}