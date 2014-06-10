package com.github.randoapp.test.db;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.db.model.RandoUpload;

import java.io.File;
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
        randoPair.user.date = new Date();

        randoPair.stranger.imageURL = "Bla2URL";
        randoPair.stranger.mapURL = "LocalFileStranger";
        randoPair.stranger.date = new Date();

        RandoPair newRandoPair = randoDAO.createRandoPair(randoPair);
        newRandoPair = randoDAO.getRandoPairById(newRandoPair.id);

        assertThat(randoPair, is(newRandoPair));
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

    public void testAddToUploadSuccessfulAdd() {
        randoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));

        RandoUpload randoUpload = new RandoUpload();
        randoUpload.file = "/path/to/file2";
        randoUpload.longitude = "24.44";
        randoUpload.latitude = "22.55";
        randoUpload.date = new Date();

        randoDAO.addToUpload(randoUpload);
        randoDAO.addToUpload(new RandoUpload("/path/to/file3", 13.33, 14.44, new Date()));
        assertThat("Miss one of rando to upload", randoDAO.getRandosToUploadNumber(), is(3));
    }

    public void testAddToUploadEmptyRandoUpload() {
        randoDAO.addToUpload(new RandoUpload());
        assertThat("Empty rando to upload created something in DB", randoDAO.getRandosToUploadNumber(), is(0));
    }

    public void testGetAllRandosToUploadReturnCorrectOrder() {
        randoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(10)));
        randoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(50)));
        randoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date(300)));

        List<RandoUpload> randos = randoDAO.getAllRandosToUpload();
        for (int i = 0; i < randos.size(); i++) {
            assertThat("Rando order is not correct", randos.get(i).file, is("/path/to/file" + (3 - i)));
        }
        assertThat("Rando number is not correct", randos.size(), is(3));
    }

    public void testGetAllRandosToUploadReturnEmptyCollectionIfTableIsEmpty() {
        List<RandoUpload> randos = randoDAO.getAllRandosToUpload();
        assertThat("RandoUpload table is not empty", randos.size(), is(0));
    }

    public void testDeleteRandoToUploadSuccessfulDelete() {
        assertThat("Initial RandoUpload table is not empty", randoDAO.getRandosToUploadNumber(), is(0));

        randoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(100)));
        randoDAO.addToUpload(new RandoUpload("/path/to/file2", 13.33, 14.44, new Date(200)));
        randoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date(300)));


        RandoUpload randoUpload = randoDAO.getAllRandosToUpload().get(1);
        assertThat("Unexpected second rando", randoUpload.file, is("/path/to/file2"));

        randoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", randoDAO.getRandosToUploadNumber(), is(2));
    }

    public void testDeleteRandoToUploadEmptyTableDetele() {
        assertThat("Initial RandoUpload table is not empty", randoDAO.getRandosToUploadNumber(), is(0));
        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 13.33, 14.44, new Date());
        randoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", randoDAO.getRandosToUploadNumber(), is(0));
    }

    public void testDeleteRandoToUploadDeleteNotExistsRando() {
        randoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        randoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date()));
        assertThat("Initial delete rando to upload number is not correct", randoDAO.getRandosToUploadNumber(), is(2));

        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 23.33, 24.44, new Date());
        randoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", randoDAO.getRandosToUploadNumber(), is(2));
    }


    private void insertNRandomRandoPairs(int n) {
        randoDAO.insertRandoPairs(RandoPairTestHelper.getNRandomRandoPairs(n));
    }

}
