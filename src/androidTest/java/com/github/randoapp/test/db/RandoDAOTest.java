package com.github.randoapp.test.db;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class RandoDAOTest extends AndroidTestCase {

    private RandoDAO randoDAO;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        randoDAO = new RandoDAO(getContext());
        randoDAO.beginTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        randoDAO.endTransaction();
        randoDAO.close();
        randoDAO = null;
    }

    @MediumTest
    public void testCreateNotPairedFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();

        int count = randoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(randoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testCreatePairedFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPair();

        int count = randoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(randoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testCreateFreshFood() throws SQLException {
        RandoPair randoPair = new RandoPair();

        randoPair.user.imageURL = null;
        randoPair.user.mapURL = "blaFile";
        randoPair.user.bonAppetit = 0;
        randoPair.user.date = new Date();

        int count = randoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);

        assertThat(count + 1, is(randoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testDeleteFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();

        int count = randoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(randoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));

        long id = newRandoPair.id;
        randoDAO.deleteRandoPair(newRandoPair);
        assertThat(randoDAO.getRandoPairById(id), nullValue());
    }

    @MediumTest
    public void testUpdateFood() throws SQLException {
        RandoPair randoPair = new RandoPair();

        randoPair.user.imageURL = null;
        randoPair.user.mapURL = "blaFile";
        randoPair.user.bonAppetit = 0;
        randoPair.user.date = new Date();

        int count = randoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        assertThat(randoDAO.getRandoPairsNumber(), is(count + 1));
        assertThat(newRandoPair, is(randoPair));
        long id = newRandoPair.id;

        String newMapValue = "MAP1";
        newRandoPair.user.mapURL = newMapValue;
        randoDAO.updateRandoPair(newRandoPair);

        RandoPair updatedRandoPair = randoDAO.getRandoPairById(id);
        assertThat(updatedRandoPair, notNullValue());
        assertThat(newMapValue, is(updatedRandoPair.user.mapURL));
    }

    @MediumTest
    public void testSelectRando() throws SQLException {
        RandoPair randoPair = new RandoPair();
        randoPair.user.imageURL = "blaURL";
        randoPair.user.mapURL = "blaFile";
        randoPair.user.bonAppetit = 0;
        randoPair.user.date = new Date();

        randoPair.stranger.imageURL = "Bla2URL";
        randoPair.stranger.mapURL = "LocalFileStranger";
        randoPair.stranger.bonAppetit = 0;
        randoPair.stranger.date = new Date();

        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        newRandoPair = randoDAO.getRandoPairById(newRandoPair.id);

        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testGetFirstPage() throws SQLException {
        insertNRandomRandoPairs(55);

        List<RandoPair> randoPairs = randoDAO.getAllRandoPairs();

        List<RandoPair> firstPage = randoDAO.getRandoPairsForPage(0);

        assertThat("Returned page size is 0 size", firstPage.size(), greaterThan(0));
        assertThat("Returned page size is greater PAGE_SIZE=" + Constants.PAGE_SIZE, firstPage.size(), lessThanOrEqualTo(Constants.PAGE_SIZE));

        RandoPairTestHelper.checkListNaturalOrder(randoPairs);

        for (int i = 0; i < firstPage.size(); i++) {
            assertThat(firstPage.get(i), is(randoPairs.get(i)));
        }
    }

    @MediumTest
    public void testGetLastPage() throws SQLException {
        insertNRandomRandoPairs(55);

        List<RandoPair> randoPairs = randoDAO.getAllRandoPairs();

        List<RandoPair> lastPage = randoDAO.getRandoPairsForPage(randoDAO.getPagesNumber() - 1);
        assertThat("Returned page size is 0 size", lastPage.size(), greaterThan(0));
        assertThat("Returned page size is greater PAGE_SIZE=" + Constants.PAGE_SIZE, lastPage.size(), lessThanOrEqualTo(Constants.PAGE_SIZE));

        RandoPairTestHelper.checkListNaturalOrder(randoPairs);

        int pagesNumber = randoDAO.getPagesNumber();

        int count = 0;
        for (int i = 0; i < lastPage.size(); i++) {
            int posInList = (pagesNumber - 1) * Constants.PAGE_SIZE + i;
            assertThat(lastPage.get(i), is(randoPairs.get(posInList)));
            count++;
        }
        assertThat(count, is(lastPage.size()));
    }

    @MediumTest
    public void testGetPagesNumber() throws SQLException {
        insertNRandomRandoPairs(55);
        List<RandoPair> randoPairs = randoDAO.getAllRandoPairs();
        int number = randoDAO.getPagesNumber();
        int result = randoPairs.size() - (number - 1) * Constants.PAGE_SIZE;
        assertThat(result, greaterThan(0));
        assertThat(result, lessThanOrEqualTo(Constants.PAGE_SIZE));
    }

    @MediumTest
    public void testReturnOrder() throws SQLException {
        insertNRandomRandoPairs(55);
        List<RandoPair> randoPairs = randoDAO.getAllRandoPairs();
        RandoPairTestHelper.checkListNaturalOrder(randoPairs);

    }

    @MediumTest
    public void testGetNotExistingRandoPair() throws SQLException {
        insertNRandomRandoPairs(55);
        RandoPair randoPair = randoDAO.getRandoPairById(9999L);
        assertThat(randoPair, nullValue());
    }

    @MediumTest
    public void testInsertNullRandoPair() throws SQLException {
        RandoPair randoPair = randoDAO.createRandoPair(null);
        assertThat(randoPair, nullValue());
    }

    @MediumTest
    public void testClearEmptyTable() throws SQLException {
        randoDAO.clearRandoPairs();
        assertThat(randoDAO.getRandoPairsNumber(), is(0));
        assertThat(randoDAO.getAllRandoPairs().size(), is(0));
        randoDAO.clearRandoPairs();
        assertThat(randoDAO.getRandoPairsNumber(), is(0));
        assertThat(randoDAO.getAllRandoPairs().size(), is(0));
    }

    @MediumTest
    public void testClearNotEmptyDB() throws SQLException {
        insertNRandomRandoPairs(10);
        assertThat(randoDAO.getRandoPairsNumber(), greaterThan(0));
        assertThat(randoDAO.getAllRandoPairs().size(), greaterThan(0));
        randoDAO.clearRandoPairs();
        assertThat(randoDAO.getRandoPairsNumber(), is(0));
        assertThat(randoDAO.getAllRandoPairs().size(), is(0));
    }

    public void testGetNotPairedFoodsNumberIdIsNull() {
        randoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            randoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 5; i++) {
            randoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPairNotPaired());
        }
        assertThat("count NOT paired randos failed", randoDAO.getNotPairedRandosNumber(), is(5));
    }

    public void testGetNotPairedFoodsNumberIdIsEmptyString() {
        randoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            randoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 5; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = "";
            randoDAO.createRandoPair(randoPair);
        }
        assertThat("count NOT paired randos failed", randoDAO.getNotPairedRandosNumber(), is(5));
    }

    public void testGetNotPairedFoodsNumberIdIsMixed() {
        randoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            randoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 3; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = "";
            randoDAO.createRandoPair(randoPair);
        }
        for (int i = 0; i < 3; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = null;
            randoDAO.createRandoPair(randoPair);
        }
        assertThat("count NOT paired randos failed", randoDAO.getNotPairedRandosNumber(), is(6));
    }


    private void insertNRandomRandoPairs(int n) {
        randoDAO.insertRandoPairs(RandoPairTestHelper.getNRandomRandoPairs(n));
    }


}
