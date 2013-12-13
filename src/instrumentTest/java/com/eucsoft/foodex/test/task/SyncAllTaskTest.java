package com.eucsoft.foodex.test.task;

import android.test.AndroidTestCase;

import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.SyncAllTask;
import com.eucsoft.foodex.test.api.APITestHelper;
import com.eucsoft.foodex.test.db.FoodPairTestHelper;

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
    private List<FoodPair> mockedFoodPairs;

   /* @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        syncAllTask = new SyncAllTask();
        APITestHelper.mockAPIForFetchUser();
        mockedFoodPairs = API.fetchUser();
        APITestHelper.mockAPIForFetchUser();
        foodDAO = new FoodDAO(getContext());
        foodDAO.clearFoodPairs();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        foodDAO.close();
    }

    public void testNotChanged() throws Exception {
        assertThat(mockedFoodPairs, notNullValue());
        assertThat(mockedFoodPairs.size(), greaterThan(1));
        foodDAO.insertFoodPairs(mockedFoodPairs);

        FoodPairTestHelper.checkListsEqual(mockedFoodPairs, foodDAO.getAllFoodPairs());

        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(resultCode, is(SyncAllTask.NOT_UPDATED));
                List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
                FoodPairTestHelper.checkListsEqual(mockedFoodPairs, dbFoodPairs);
                signal.countDown();
            }
        });
        syncAllTask.execute();
        signal.await(30, TimeUnit.SECONDS);
    }

    public void testFoodPairUpdated() throws Exception {
        assertThat(mockedFoodPairs, notNullValue());
        assertThat(mockedFoodPairs.size(), greaterThan(1));

        List<FoodPair> forDBList = API.fetchUser();
        APITestHelper.mockAPIForFetchUser();

        forDBList.get(0).user.foodURL = "!BLAAAAA!!!!!!!!";
        forDBList.get(0).user.mapURL = "!BLAAAAA!!!!!!!!";

        foodDAO.insertFoodPairs(forDBList);

        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(resultCode, is(SyncAllTask.FOOD_PAIRS_UPDATED));
                List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
                FoodPairTestHelper.checkListsEqual(mockedFoodPairs, dbFoodPairs);
                signal.countDown();
            }
        });
        syncAllTask.execute();
        signal.await(30, TimeUnit.SECONDS);
    }

    public void testFoodPairAdded() throws Exception {
        assertThat(mockedFoodPairs, notNullValue());
        assertThat(mockedFoodPairs.size(), greaterThan(1));

        List<FoodPair> forDBList = API.fetchUser();
        APITestHelper.mockAPIForFetchUser();

        forDBList.remove(0);
        foodDAO.insertFoodPairs(forDBList);
        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(resultCode, is(SyncAllTask.FOOD_PAIRS_UPDATED));
                List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
                FoodPairTestHelper.checkListsEqual(mockedFoodPairs, dbFoodPairs);
                signal.countDown();
            }
        });
        syncAllTask.execute();
        signal.await(30, TimeUnit.SECONDS);
    }

    public void testFoodPairRemoved() throws Exception {
        assertThat(mockedFoodPairs, notNullValue());
        assertThat(mockedFoodPairs.size(), greaterThan(1));

        List<FoodPair> forDBList = API.fetchUser();
        APITestHelper.mockAPIForFetchUser();

        forDBList.add(FoodPairTestHelper.getRandomFoodPair());
        foodDAO.insertFoodPairs(forDBList);
        syncAllTask.setTaskResultListener(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                assertThat(resultCode, is(SyncAllTask.FOOD_PAIRS_UPDATED));
                List<FoodPair> dbFoodPairs = foodDAO.getAllFoodPairs();
                FoodPairTestHelper.checkListsEqual(mockedFoodPairs, dbFoodPairs);
                signal.countDown();
            }
        });
        syncAllTask.execute();
        signal.await(30, TimeUnit.SECONDS);
    }
*/
}
