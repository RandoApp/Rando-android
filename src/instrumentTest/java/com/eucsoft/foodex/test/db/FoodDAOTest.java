package com.eucsoft.foodex.test.db;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

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

    @MediumTest
    public void testCreateNotPairedFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = "blaURL";
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getFoodPairsNumber();
        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);
        assertThat(count + 1, is(foodDAO.getFoodPairsNumber()));
        assertThat(foodPair, is(newFoodPair));
    }

    @MediumTest
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

        int count = foodDAO.getFoodPairsNumber();
        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);
        assertThat(count + 1, is(foodDAO.getFoodPairsNumber()));
        assertThat(foodPair, is(newFoodPair));
    }

    @MediumTest
    public void testCreateFreshFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getFoodPairsNumber();
        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);

        assertThat(count + 1, is(foodDAO.getFoodPairsNumber()));
        assertThat(foodPair, is(newFoodPair));
    }

    @MediumTest
    public void testDeleteFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getFoodPairsNumber();
        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);
        assertThat(count + 1, is(foodDAO.getFoodPairsNumber()));
        assertThat(foodPair, is(newFoodPair));

        long id = newFoodPair.id;
        foodDAO.deleteFoodPair(newFoodPair);
        assertThat(foodDAO.getFoodPairById(id), nullValue());
    }

    @MediumTest
    public void testUpdateFood() throws SQLException {
        FoodPair foodPair = new FoodPair();

        foodPair.user.foodURL = null;
        foodPair.user.mapURL = "blaFile";
        foodPair.user.bonAppetit = 0;
        foodPair.user.foodDate = new Date();

        int count = foodDAO.getFoodPairsNumber();
        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);

        assertThat(foodDAO.getFoodPairsNumber(), is(count + 1));

        //assertEquals(count + 1, foodDAO.getFoodPairsNumber());

        assertThat(newFoodPair, is(foodPair));

        //assertTrue(foodPair.equals(newFoodPair));
        long id = newFoodPair.id;

        String newMapValue = "MAP1";
        newFoodPair.user.mapURL = newMapValue;
        foodDAO.updateFoodPair(newFoodPair);

        FoodPair updatedFoodPair = foodDAO.getFoodPairById(id);
        assertThat(updatedFoodPair, notNullValue());
        assertThat(newMapValue, is(updatedFoodPair.user.mapURL));
    }

    @MediumTest
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

        FoodPair newFoodPair = foodDAO.createFoodPair(foodPair);
        newFoodPair = foodDAO.getFoodPairById(newFoodPair.id);

        assertThat(foodPair, is(newFoodPair));
    }

    @MediumTest
    public void testGetFirstPage() throws SQLException {
        insertNRandomFoodPairs(55);

        List<FoodPair> foodPairs = foodDAO.getAllFoodPairs();

        List<FoodPair> firstPage = foodDAO.getFoodPairsForPage(0);

        assertThat("Returned page size is 0 size", firstPage.size(), greaterThan(0));
        assertThat("Returned page size is greater PAGE_SIZE=" + Constants.PAGE_SIZE, firstPage.size(), lessThanOrEqualTo(Constants.PAGE_SIZE));

        FoodPairTestHelper.checkListNaturalOrder(foodPairs);

        for (int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i), is(foodPairs.get(i)));
        }
    }

    @MediumTest
    public void testGetLastPage() throws SQLException {
        insertNRandomFoodPairs(55);

        List<FoodPair> foodPairs = foodDAO.getAllFoodPairs();

        List<FoodPair> lastPage = foodDAO.getFoodPairsForPage(foodDAO.getPagesNumber() - 1);
        assertThat("Returned page size is 0 size", lastPage.size(), greaterThan(0));
        assertThat("Returned page size is greater PAGE_SIZE=" + Constants.PAGE_SIZE, lastPage.size(), lessThanOrEqualTo(Constants.PAGE_SIZE));

        FoodPairTestHelper.checkListNaturalOrder(foodPairs);

        int pagesNumber = foodDAO.getPagesNumber();

        int count = 0;
        for (int i = 0; i < lastPage.size(); i++) {
            int posInList = (pagesNumber - 1) * Constants.PAGE_SIZE + i;
            assertThat(lastPage.get(i), is(foodPairs.get(posInList)));
            count++;
        }
        assertThat(count, is(lastPage.size()));
    }

    @MediumTest
    public void testGetPagesNumber() throws SQLException {
        insertNRandomFoodPairs(55);
        List<FoodPair> foodPairs = foodDAO.getAllFoodPairs();
        int number = foodDAO.getPagesNumber();
        int result = foodPairs.size() - (number - 1) * Constants.PAGE_SIZE;
        assertThat(result, greaterThan(0));
        assertThat(result, lessThanOrEqualTo(Constants.PAGE_SIZE));
    }

    @MediumTest
    public void testReturnOrder() throws SQLException {
        insertNRandomFoodPairs(55);
        List<FoodPair> foodPairs = foodDAO.getAllFoodPairs();
        FoodPairTestHelper.checkListNaturalOrder(foodPairs);

    }

    @MediumTest
    public void testGetNotExistingFoodPair() throws SQLException {
        insertNRandomFoodPairs(55);
        FoodPair foodPair = foodDAO.getFoodPairById(9999L);
        assertThat(foodPair, nullValue());
    }

    @MediumTest
    public void testInsertNullFoodPair() throws SQLException {
        FoodPair foodPair = foodDAO.createFoodPair(null);
        assertThat(foodPair, nullValue());
    }

    @MediumTest
    public void testClearEmptyTable() throws SQLException {
        foodDAO.clearFoodPairs();
        assertThat(foodDAO.getFoodPairsNumber(), is(0));
        assertThat(foodDAO.getAllFoodPairs().size(), is(0));
        foodDAO.clearFoodPairs();
        assertThat(foodDAO.getFoodPairsNumber(), is(0));
        assertThat(foodDAO.getAllFoodPairs().size(), is(0));
    }

    @MediumTest
    public void testClearNotEmptyDB() throws SQLException {
        insertNRandomFoodPairs(10);
        assertThat(foodDAO.getFoodPairsNumber(), greaterThan(0));
        assertThat(foodDAO.getAllFoodPairs().size(), greaterThan(0));
        foodDAO.clearFoodPairs();
        assertThat(foodDAO.getFoodPairsNumber(), is(0));
        assertThat(foodDAO.getAllFoodPairs().size(), is(0));
    }


    private void insertNRandomFoodPairs(int n) {
        foodDAO.insertFoodPairs(FoodPairTestHelper.getNRandomFoodPairs(n));
    }


}
