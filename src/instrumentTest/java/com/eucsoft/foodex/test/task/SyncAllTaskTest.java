package com.eucsoft.foodex.test.task;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.SyncAllTask;
import com.eucsoft.foodex.test.api.APITestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class SyncAllTaskTest extends AndroidTestCase {

    private SyncAllTask syncAllTask;
    private CountDownLatch signal;
    private FoodDAO foodDAO;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        syncAllTask = new SyncAllTask();
        APITestHelper.mockAPIForFetchUser();
        foodDAO = new FoodDAO(getContext());
        foodDAO.clearFoodPairs();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        foodDAO.close();
    }

    public void testNothingToSync() throws Exception {
        List<FoodPair> foodPairs;
        foodPairs = API.fetchUser();
        assertThat(foodPairs, notNullValue());
        assertThat(foodPairs.size(), greaterThan(1));
        foodDAO.insertFoodPairs(foodPairs);
        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(resultCode, is(SyncAllTask.RESULT_OK));
                signal.countDown();
            }
        });
        syncAllTask.execute();
        signal.await(30, TimeUnit.SECONDS);
    }
}
