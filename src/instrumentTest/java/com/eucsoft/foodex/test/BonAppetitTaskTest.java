package com.eucsoft.foodex.test;

import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BonAppetitTask;
import com.eucsoft.foodex.test.util.APITestHelper;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public class BonAppetitTaskTest extends AndroidTestCase {

    private BonAppetitTask bonAppetitTask;
    private CountDownLatch signal;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        bonAppetitTask = new BonAppetitTask();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

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
        MainActivity.context = getContext();
        TaskResultListener listener = new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(taskCode, is(BonAppetitTask.TASK_ID));
                assertThat(resultCode, is(BonAppetitTask.RESULT_OK));
                assertThat(data.get(Constants.FOOD_PAIR), notNullValue());
                assertThat(data.get(Constants.FOOD_PAIR), is(instanceOf(FoodPair.class)));
                FoodPair foodPair1 = (FoodPair) data.get(Constants.FOOD_PAIR);
                assertThat(foodPair1.stranger.isBonAppetit(), is(true));
                foodPair.stranger.bonAppetit = 0;
                assertThat(foodPair1, is(foodPair));
                signal.countDown();
            }
        };
        bonAppetitTask.setTaskResultListener(listener);
        bonAppetitTask.execute(foodPair);
        signal.await(30, TimeUnit.SECONDS);
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
            //APITestHelper.mockAPI(HttpStatus.SC_REQUEST_TIMEOUT, "Bad luck!");
            APITestHelper.mockAPIWithError();
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        MainActivity.context = getContext();
        TaskResultListener listener = new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(taskCode, is(BonAppetitTask.TASK_ID));
                assertThat(resultCode, is(BonAppetitTask.RESULT_ERROR));
                assertThat(data.get(Constants.FOOD_PAIR), notNullValue());
                assertThat(data.get(Constants.FOOD_PAIR), is(instanceOf(FoodPair.class)));
                FoodPair foodPair1 = (FoodPair) data.get(Constants.FOOD_PAIR);
                assertThat(foodPair1.stranger.isBonAppetit(), is(false));
                assertThat(foodPair1, is(foodPair));
                signal.countDown();
            }
        };
        bonAppetitTask.setTaskResultListener(listener);
        bonAppetitTask.execute(foodPair);
        signal.await(30, TimeUnit.SECONDS);
    }

    @MediumTest
    @UiThreadTest
    public void testFoodPaidNull() throws InterruptedException {
        try {
            APITestHelper.mockAPI(HttpStatus.SC_OK, "Everything is OK!");
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        MainActivity.context = getContext();
        TaskResultListener listener = new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(taskCode, is(BonAppetitTask.TASK_ID));
                assertThat(resultCode, is(BonAppetitTask.RESULT_ERROR));
                assertThat(data.get(Constants.FOOD_PAIR), nullValue());
                signal.countDown();
            }
        };
        bonAppetitTask.setTaskResultListener(listener);
        bonAppetitTask.execute(null);
        signal.await(30, TimeUnit.SECONDS);
    }
}
