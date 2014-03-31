package com.eucsoft.foodex.test.task;

import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.task.BonAppetitTask;
import com.eucsoft.foodex.task.callback.OnError;
import com.eucsoft.foodex.task.callback.OnOk;
import com.eucsoft.foodex.test.api.APITestHelper;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public class BonAppetitTaskTest extends AndroidTestCase {

    @MediumTest
    @UiThreadTest
    public void testBonAppetitSuccess() throws InterruptedException {
        final FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        foodPair.stranger.foodURL = "blaURL";
        foodPair.stranger.mapURL = "blaFile";
        foodPair.stranger.bonAppetit = 0;
        foodPair.stranger.foodDate = new Date();

        try {
            APITestHelper.mockAPI(HttpStatus.SC_OK, "Everything is OK!");
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(foodPair)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    assertThat(data.get(Constants.FOOD_PAIR), notNullValue());
                    assertThat(data.get(Constants.FOOD_PAIR), is(instanceOf(FoodPair.class)));
                    FoodPair foodPair1 = (FoodPair) data.get(Constants.FOOD_PAIR);
                    assertThat(foodPair1.stranger.isBonAppetit(), is(true));
                    foodPair.stranger.bonAppetit = 0;
                    assertThat(foodPair1, is(foodPair));
                }
            })
            .executeSync();
    }

    @MediumTest
    @UiThreadTest
    public void testBonAppetitError() throws InterruptedException {
        final FoodPair foodPair = new FoodPair();
        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        foodPair.stranger.foodURL = "blaURL";
        foodPair.stranger.mapURL = "blaFile";
        foodPair.stranger.bonAppetit = 0;
        foodPair.stranger.foodDate = new Date();

        try {
            APITestHelper.mockAPIWithError();
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(foodPair)
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    assertThat(data.get(Constants.FOOD_PAIR), notNullValue());
                    assertThat(data.get(Constants.FOOD_PAIR), is(instanceOf(FoodPair.class)));
                    FoodPair foodPair1 = (FoodPair) data.get(Constants.FOOD_PAIR);
                    assertThat(foodPair1.stranger.isBonAppetit(), is(false));
                    assertThat(foodPair1, is(foodPair));
                }
            })
            .executeSync();
    }

    @MediumTest
    @UiThreadTest
    public void testFoodPaidNull() throws InterruptedException {
        try {
            APITestHelper.mockAPI(HttpStatus.SC_OK, "Everything is OK!");
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(null)
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    assertThat(data.get(Constants.FOOD_PAIR), nullValue());
                }
            })
            .executeSync();
    }
}
