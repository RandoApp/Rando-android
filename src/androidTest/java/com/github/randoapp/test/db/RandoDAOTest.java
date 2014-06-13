package com.github.randoapp.test.db;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.db.model.RandoUpload;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class RandoDAOTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RandoDAO.clearRandoPairs();
        RandoDAO.clearRandoToUpload();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RandoDAO.clearRandoPairs();
        RandoDAO.clearRandoToUpload();
    }

    @MediumTest
    public void testCreateNotPairedFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();

        int count = RandoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(RandoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testCreatePairedFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPair();

        int count = RandoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(RandoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testCreateFreshFood() throws SQLException {
        RandoPair randoPair = new RandoPair();

        randoPair.user.imageURL = null;
        randoPair.user.mapURL = "blaFile";
        randoPair.user.date = new Date();

        int count = RandoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);

        assertThat(count + 1, is(RandoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testDeleteFood() throws SQLException {
        RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();

        int count = RandoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);
        assertThat(count + 1, is(RandoDAO.getRandoPairsNumber()));
        assertThat(randoPair, is(newRandoPair));

        long id = newRandoPair.id;
        RandoDAO.deleteRandoPair(newRandoPair);
        assertThat(RandoDAO.getRandoPairById(id), nullValue());
    }

    @MediumTest
    public void testUpdateFood() throws SQLException {
        RandoPair randoPair = new RandoPair();

        randoPair.user.imageURL = null;
        randoPair.user.mapURL = "blaFile";
        randoPair.user.date = new Date();

        int count = RandoDAO.getRandoPairsNumber();
        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);
        assertThat(RandoDAO.getRandoPairsNumber(), is(count + 1));
        assertThat(newRandoPair, is(randoPair));
        long id = newRandoPair.id;

        String newMapValue = "MAP1";
        newRandoPair.user.mapURL = newMapValue;
        RandoDAO.updateRandoPair(newRandoPair);

        RandoPair updatedRandoPair = RandoDAO.getRandoPairById(id);
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

        RandoPair newRandoPair = RandoDAO.createRandoPair(randoPair);
        newRandoPair = RandoDAO.getRandoPairById(newRandoPair.id);

        assertThat(randoPair, is(newRandoPair));
    }

    @MediumTest
    public void testReturnOrder() throws SQLException {
        insertNRandomRandoPairs(55);
        List<RandoPair> randoPairs = RandoDAO.getAllRandoPairs();
        RandoPairTestHelper.checkListNaturalOrder(randoPairs);

    }

    @MediumTest
    public void testGetNotExistingRandoPair() throws SQLException {
        insertNRandomRandoPairs(55);
        RandoPair randoPair = RandoDAO.getRandoPairById(9999L);
        assertThat(randoPair, nullValue());
    }

    @MediumTest
    public void testInsertNullRandoPair() throws SQLException {
        RandoPair randoPair = RandoDAO.createRandoPair(null);
        assertThat(randoPair, nullValue());
    }

    @MediumTest
    public void testClearEmptyTable() throws SQLException {
        RandoDAO.clearRandoPairs();
        assertThat(RandoDAO.getRandoPairsNumber(), is(0));
        assertThat(RandoDAO.getAllRandoPairs().size(), is(0));
        RandoDAO.clearRandoPairs();
        assertThat(RandoDAO.getRandoPairsNumber(), is(0));
        assertThat(RandoDAO.getAllRandoPairs().size(), is(0));
    }

    @MediumTest
    public void testClearNotEmptyDB() throws SQLException {
        insertNRandomRandoPairs(10);
        assertThat(RandoDAO.getRandoPairsNumber(), greaterThan(0));
        assertThat(RandoDAO.getAllRandoPairs().size(), greaterThan(0));
        RandoDAO.clearRandoPairs();
        assertThat(RandoDAO.getRandoPairsNumber(), is(0));
        assertThat(RandoDAO.getAllRandoPairs().size(), is(0));
    }

    public void testGetNotPairedFoodsNumberIdIsNull() {
        RandoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            RandoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 5; i++) {
            RandoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPairNotPaired());
        }
        assertThat("count NOT paired randos failed", RandoDAO.getNotPairedRandosNumber(), is(5));
    }

    public void testGetNotPairedFoodsNumberIdIsEmptyString() {
        RandoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            RandoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 5; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = "";
            RandoDAO.createRandoPair(randoPair);
        }
        assertThat("count NOT paired randos failed", RandoDAO.getNotPairedRandosNumber(), is(5));
    }

    public void testGetNotPairedFoodsNumberIdIsMixed() {
        RandoDAO.clearRandoPairs();
        for (int i = 0; i < 10; i++) {
            RandoDAO.createRandoPair(RandoPairTestHelper.getRandomRandoPair());
        }
        for (int i = 0; i < 3; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = "";
            RandoDAO.createRandoPair(randoPair);
        }
        for (int i = 0; i < 3; i++) {
            RandoPair randoPair = RandoPairTestHelper.getRandomRandoPairNotPaired();
            randoPair.stranger.randoId = null;
            RandoDAO.createRandoPair(randoPair);
        }
        assertThat("count NOT paired randos failed", RandoDAO.getNotPairedRandosNumber(), is(6));
    }

    public void testAddToUploadSuccessfulAdd() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));

        RandoUpload randoUpload = new RandoUpload();
        randoUpload.file = "/path/to/file2";
        randoUpload.longitude = "24.44";
        randoUpload.latitude = "22.55";
        randoUpload.date = new Date();

        RandoDAO.addToUpload(randoUpload);
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 13.33, 14.44, new Date()));
        assertThat("Miss one of rando to upload", RandoDAO.getRandosToUploadNumber(), is(3));
    }

    public void testAddToUploadEmptyRandoUpload() {
        RandoDAO.addToUpload(new RandoUpload());
        assertThat("Empty rando to upload created something in DB", RandoDAO.getRandosToUploadNumber(), is(0));
    }

    public void testGetAllRandosToUploadReturnCorrectOrder() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(10)));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(50)));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date(300)));

        List<RandoUpload> randos = RandoDAO.getAllRandosToUpload();
        for (int i = 0; i < randos.size(); i++) {
            assertThat("Rando order is not correct", randos.get(i).file, is("/path/to/file" + (3 - i)));
        }
        assertThat("Rando number is not correct", randos.size(), is(3));
    }

    public void testGetAllRandosToUploadReturnEmptyCollectionIfTableIsEmpty() {
        List<RandoUpload> randos = RandoDAO.getAllRandosToUpload();
        assertThat("RandoUpload table is not empty", randos.size(), is(0));
    }

    public void testDeleteRandoToUploadSuccessfulDelete() {
        assertThat("Initial RandoUpload table is not empty", RandoDAO.getRandosToUploadNumber(), is(0));

        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(100)));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(200)));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date(300)));


        RandoUpload randoUpload = RandoDAO.getAllRandosToUpload().get(1);
        assertThat("Unexpected second rando", randoUpload.file, is("/path/to/file2"));

        RandoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));
    }

    public void testDeleteRandoToUploadEmptyTableDetele() {
        assertThat("Initial RandoUpload table is not empty", RandoDAO.getRandosToUploadNumber(), is(0));
        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 13.33, 14.44, new Date());
        RandoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(0));
    }

    public void testDeleteRandoToUploadDeleteNotExistsRando() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date()));
        assertThat("Initial delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));

        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 23.33, 24.44, new Date());
        RandoDAO.deleteRandoToUpload(randoUpload);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));
    }

    public void testGetAllRandos() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        insertNRandomRandoPairs(3);
        List<RandoPair> randos = RandoDAO.getAllRandos();
        assertThat("Collection number is not correct", randos.size(), is(4));
    }

    public void testGetAllRandosEmpty() {
        List<RandoPair> randos = RandoDAO.getAllRandos();
        assertThat("Collection number is not correct", randos.size(), is(0));
    }

    public void testGetAllRandosOnlyToUpload() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date()));
        List<RandoPair> randos = RandoDAO.getAllRandos();
        assertThat("Collection number is not correct", randos.size(), is(3));
    }

    public void testGetAllRandosOnlyPairs() {
        insertNRandomRandoPairs(4);
        List<RandoPair> randos = RandoDAO.getAllRandos();
        assertThat("Collection number is not correct", randos.size(), is(4));
    }

    public void testGetAllRandosNumberOnlyToUpload() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date()));
        assertThat("Items number in table is not correct", RandoDAO.getAllRandosNumber(), is(3));
    }

    public void testGetAllRandosNumberOnlyPairs() {
        insertNRandomRandoPairs(4);
        assertThat("Items number in table is not correct", RandoDAO.getAllRandosNumber(), is(4));
    }

    public void testGetAllRandosNumberPairsAndUploads() {
        assertThat("Items number in table is not correct", RandoDAO.getAllRandosNumber(), is(0));
    }

    public void testGetAllRandosNumberEmptyTable() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 34.44, new Date()));
        insertNRandomRandoPairs(3);
        assertThat("Collection number is not correct", RandoDAO.getAllRandosNumber(), is(5));
    }

    public void testGetRandosToUploadNumberEmptyTable() {
        assertThat("Items number in table is not correct", RandoDAO.getAllRandosNumber(), is(0));
    }

    public void testGetRandosToUploadNumber() {
        RandoDAO.addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        RandoDAO.addToUpload(new RandoUpload("/path/to/file2", 23.33, 34.44, new Date()));
        assertThat("Items number in table is not correct", RandoDAO.getAllRandosNumber(), is(2));
    }


    private void insertNRandomRandoPairs(int n) {
        RandoDAO.insertRandoPairs(RandoPairTestHelper.getNRandomRandoPairs(n));
    }

}
