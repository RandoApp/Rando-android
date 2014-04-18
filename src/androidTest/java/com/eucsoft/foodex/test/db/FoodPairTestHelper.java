package com.eucsoft.foodex.test.db;

import com.eucsoft.foodex.db.model.FoodPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

public class FoodPairTestHelper {
    private static Random random = new Random();

    public static List<FoodPair> getNRandomFoodPairs(int n) {
        Date baseDate = new Date();

        List<FoodPair> foodPairs = new ArrayList<FoodPair>();
        for (int i = 0; i < n; i++) {
            FoodPair foodPair;
            Date userDate = new Date();
            userDate.setTime(baseDate.getTime() + random.nextInt(1000000));

            foodPair = new FoodPair();
            foodPair.user.foodId = UUID.randomUUID().toString();
            foodPair.user.foodURL = "blaURL" + i;
            foodPair.user.foodUrlSize.small  = "blaURL" + i;
            foodPair.user.foodUrlSize.medium = "blaURL" + i;
            foodPair.user.foodUrlSize.large = "blaURL" + i;
            foodPair.user.mapURL = "blaFile" + i;
            foodPair.user.mapUrlSize.small  = "blaURL" + i;
            foodPair.user.mapUrlSize.medium = "blaURL" + i;
            foodPair.user.mapUrlSize.large = "blaURL" + i;
            foodPair.user.bonAppetit = 0;
            foodPair.user.foodDate = userDate;

            Date strangerDate = new Date();
            strangerDate.setTime(baseDate.getTime() + random.nextInt(1000000));
            foodPair.stranger.foodId = UUID.randomUUID().toString();
            foodPair.stranger.foodURL = "Bla2URL" + i;
            foodPair.stranger.foodUrlSize.small = "Bla2URL" + i;
            foodPair.stranger.foodUrlSize.medium = "Bla2URL" + i;
            foodPair.stranger.foodUrlSize.large = "Bla2URL" + i;
            foodPair.stranger.mapURL = "LocalFileStranger" + i;
            foodPair.stranger.mapUrlSize.small = "Bla2URL" + i;
            foodPair.stranger.mapUrlSize.medium = "Bla2URL" + i;
            foodPair.stranger.mapUrlSize.large = "Bla2URL" + i;
            foodPair.stranger.bonAppetit = 0;
            foodPair.stranger.foodDate = strangerDate;
            foodPairs.add(foodPair);
        }

        return foodPairs;
    }

    public static FoodPair getRandomFoodPair() {

        FoodPair foodPair;
        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        foodPair = new FoodPair();
        foodPair.user.foodId = UUID.randomUUID().toString();
        foodPair.user.foodURL = "blaURL";
        foodPair.user.foodUrlSize.small = "blaURL";
        foodPair.user.foodUrlSize.medium = "blaURL";
        foodPair.user.foodUrlSize.large = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.mapUrlSize.small = "blaURL";
        foodPair.user.mapUrlSize.medium = "blaURL";
        foodPair.user.mapUrlSize.large = "blaURL";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = userDate;

        Date strangerDate = new Date();
        strangerDate.setTime(new Date().getTime() + random.nextInt(1000000));
        foodPair.stranger.foodId = UUID.randomUUID().toString();
        foodPair.stranger.foodURL = "Bla2URL";
        foodPair.stranger.foodUrlSize.small = "Bla2URL";
        foodPair.stranger.foodUrlSize.medium = "Bla2URL";
        foodPair.stranger.foodUrlSize.large = "Bla2URL";
        foodPair.stranger.mapURL = "LocalFileStranger";
        foodPair.stranger.mapUrlSize.small = "Bla2URL";
        foodPair.stranger.mapUrlSize.medium = "Bla2URL";
        foodPair.stranger.mapUrlSize.large = "Bla2URL";
        foodPair.stranger.bonAppetit = 0;
        foodPair.stranger.foodDate = strangerDate;
        return foodPair;
    }

    public static FoodPair getRandomFoodPairNotPaired() {

        FoodPair foodPair;
        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        foodPair = new FoodPair();
        foodPair.user.foodId = UUID.randomUUID().toString();
        foodPair.user.foodURL = "blaURL";
        foodPair.user.foodUrlSize.small = "blaURL";
        foodPair.user.foodUrlSize.medium = "blaURL";
        foodPair.user.foodUrlSize.large = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.mapUrlSize.small = "blaURL";
        foodPair.user.mapUrlSize.medium = "blaURL";
        foodPair.user.mapUrlSize.large = "blaURL";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = userDate;

        return foodPair;
    }

    public static void checkListNaturalOrder(List<FoodPair> foodPairs) {
        FoodPair prevPair = null;
        for (FoodPair foodPair : foodPairs) {
            if (prevPair != null) {
                assertThat("Order is broken: " + foodPair.user.foodDate.toString() + " is less than " + prevPair.user.foodDate.toString(), foodPair.user.foodDate, lessThanOrEqualTo(prevPair.user.foodDate));
            } else {
                prevPair = foodPair;
            }
        }
    }

    public static void checkListsEqual(List<FoodPair> foodPairs1, List<FoodPair> foodPairs2) {
        assertThat(foodPairs1, notNullValue());
        assertThat(foodPairs2, notNullValue());
        assertThat("Sizes not equal", foodPairs1.size(), is(foodPairs2.size()));
        Collections.sort(foodPairs1, new FoodPair.DateComparator());
        Collections.sort(foodPairs2, new FoodPair.DateComparator());
        for (int i = 0; i < foodPairs1.size(); i++) {
            assertThat("FoodPairs not equal: " + foodPairs1.get(i).toString() + " != " + foodPairs2.get(i).toString(), foodPairs1.get(i), is(foodPairs2.get(i)));
        }
    }

}
