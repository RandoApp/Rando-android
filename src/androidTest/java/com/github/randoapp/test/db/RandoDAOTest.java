package com.github.randoapp.test.db;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.randoapp.db.RandoDAO.addToUpload;
import static com.github.randoapp.test.db.RandoTestHelper.checkListsEqual;
import static com.github.randoapp.test.db.RandoTestHelper.getRandomRando;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoDAOTest {

    @Before
    public void setUp() throws Exception {
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
    }

    @After
    public void tearDown() throws Exception {
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
    }

    @Test
    public void testCreateRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        int count = RandoDAO.getRandosNumber();
        Rando newRando = RandoDAO.createRando(rando);
        assertThat(count + 1, is(RandoDAO.getRandosNumber()));
        assertThat(rando, is(newRando));
    }

    @Test
    public void testCreateFreshRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        int count = RandoDAO.getRandosNumber();
        Rando newRando = RandoDAO.createRando(rando);

        assertThat(count + 1, is(RandoDAO.getRandosNumber()));
        assertThat(rando, is(newRando));
    }

    @Test
    public void testDeleteRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        int count = RandoDAO.getRandosNumber();
        Rando newRando = RandoDAO.createRando(rando);
        assertThat(count + 1, is(RandoDAO.getRandosNumber()));
        assertThat(rando, is(newRando));

        long id = newRando.id;
        RandoDAO.deleteRando(newRando);
        assertThat(RandoDAO.getRandoById(id), nullValue());
    }

    @Test
    public void testUpdateRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        int count = RandoDAO.getRandosNumber();
        Rando newRando = RandoDAO.createRando(rando);
        assertThat(RandoDAO.getRandosNumber(), is(count + 1));
        assertThat(newRando, is(rando));
        long id = newRando.id;

        String newMapValue = "MAP1";
        newRando.mapURL = newMapValue;
        RandoDAO.updateRando(newRando);

        Rando updatedRando = RandoDAO.getRandoById(id);
        assertThat(updatedRando, notNullValue());
        assertThat(newMapValue, is(updatedRando.mapURL));
    }

    @Test
    public void testSelectRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        Rando newRando = RandoDAO.createRando(rando);
        newRando = RandoDAO.getRandoById(newRando.id);

        assertThat(rando, is(newRando));
    }

    @Test
    public void testReturnOrder() throws SQLException {
        insertNRandomRandoPairs(55);
        List<Rando> randos = RandoDAO.getAllRandos();
        RandoTestHelper.checkListNaturalOrder(randos);

    }

    @Test
    public void testGetNotExistingRandoPair() throws SQLException {
        insertNRandomRandoPairs(55);
        Rando rando = RandoDAO.getRandoById(9999L);
        assertThat(rando, nullValue());
    }

    @Test
    public void testInsertNullRandoPair() throws SQLException {
        Rando rando = RandoDAO.createRando(null);
        assertThat(rando, nullValue());
    }

    @Test
    public void testClearEmptyTable() throws SQLException {
        RandoDAO.clearRandos();
        assertThat(RandoDAO.getRandosNumber(), is(0));
        assertThat(RandoDAO.getAllRandos().size(), is(0));
        RandoDAO.clearRandos();
        assertThat(RandoDAO.getRandosNumber(), is(0));
        assertThat(RandoDAO.getAllRandos().size(), is(0));
    }

    @Test
    public void testClearNotEmptyDB() throws SQLException {
        insertNRandomRandoPairs(10);
        assertThat(RandoDAO.getRandosNumber(), greaterThan(0));
        assertThat(RandoDAO.getAllRandos().size(), greaterThan(0));
        RandoDAO.clearRandos();
        assertThat(RandoDAO.getRandosNumber(), is(0));
        assertThat(RandoDAO.getAllRandos().size(), is(0));
    }

    @Test
    public void testAddToUploadSuccessfulAdd() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));

        RandoUpload randoUpload = new RandoUpload();
        randoUpload.file = "/path/to/file2";
        randoUpload.longitude = "24.44";
        randoUpload.latitude = "22.55";
        randoUpload.date = new Date();

        addToUpload(randoUpload);
        addToUpload(new RandoUpload("/path/to/file3", 13.33, 14.44, new Date()));
        assertThat("Miss one of rando to upload", RandoDAO.getRandosToUploadNumber(), is(3));
    }

    @Test
    public void testUpdateToUploadSuccessful() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));

        RandoUpload randoUpload = RandoDAO.getAllRandosToUpload("DESC").get(0);
        assertThat("Last try is not a default value", randoUpload.lastTry.getTime(), is(0l));

        Date now = new Date();
        randoUpload.lastTry = now;
        RandoDAO.updateRandoToUpload(randoUpload);

        RandoUpload randoUploadAfterUpdate = RandoDAO.getAllRandosToUpload("DESC").get(0);

        assertThat("Unsuccessful update lastTry field", randoUploadAfterUpdate.lastTry.getTime(), is(now.getTime()));
    }

    @Test
    public void testAddToUploadEmptyRandoUpload() {
        addToUpload(new RandoUpload());
        assertThat("Empty rando to upload created something in DB", RandoDAO.getRandosToUploadNumber(), is(0));
    }

    @Test
    public void testGetAllRandosToUploadReturnCorrectOrder() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(10)));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(50)));
        addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date(300)));

        List<RandoUpload> randos = RandoDAO.getAllRandosToUpload("DESC");
        for (int i = 0; i < randos.size(); i++) {
            assertThat("Rando order is not correct", randos.get(i).file, is("/path/to/file" + (3 - i)));
        }
        assertThat("Rando number is not correct", randos.size(), is(3));
    }

    @Test
    public void testGetAllRandosToUploadReturnEmptyCollectionIfTableIsEmpty() {
        List<RandoUpload> randos = RandoDAO.getAllRandosToUpload("DESC");
        assertThat("RandoUpload table is not empty", randos.size(), is(0));
    }

    @Test
    public void testDeleteRandoToUploadSuccessfulDelete() {
        assertThat("Initial RandoUpload table is not empty", RandoDAO.getRandosToUploadNumber(), is(0));

        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(100)));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(200)));
        addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date(300)));


        RandoUpload randoUpload = RandoDAO.getAllRandosToUpload("DESC").get(1);
        assertThat("Unexpected second rando", randoUpload.file, is("/path/to/file2"));

        RandoDAO.deleteRandoToUploadById(randoUpload.id);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));
    }

    @Test
    public void testDeleteRandoToUploadEmptyTableDetele() {
        assertThat("Initial RandoUpload table is not empty", RandoDAO.getRandosToUploadNumber(), is(0));
        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 13.33, 14.44, new Date());
        RandoDAO.deleteRandoToUploadById(randoUpload.id);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(0));
    }

    @Test
    public void testDeleteRandoToUploadDeleteNotExistsRando() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file3", 33.33, 44.44, new Date()));
        assertThat("Initial delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));

        RandoUpload randoUpload = new RandoUpload("/path/to/file2", 23.33, 24.44, new Date());
        RandoDAO.deleteRandoToUploadById(randoUpload.id);
        assertThat("After delete rando to upload number is not correct", RandoDAO.getRandosToUploadNumber(), is(2));
    }

    @Test
    public void testGetAllRandos() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        insertNRandomRandoPairs(3);
        List<Rando> randos = RandoDAO.getAllRandos(false);
        assertThat("Collection number is not correct", randos.size(), is(4));
    }

    @Test
    public void testGetAllRandosEmpty() {
        List<Rando> randos = RandoDAO.getAllRandos(false);
        assertThat("Collection number is not correct", randos.size(), is(0));
    }

    @Test
    public void testGetAllRandosOnlyToUpload() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date()));
        List<Rando> randos = RandoDAO.getAllRandos(false);
        assertThat("Collection number is not correct", randos.size(), is(3));
    }

    @Test
    public void testGetAllRandosOnlyPairs() {
        insertNRandomRandoPairs(4);
        List<Rando> randos = RandoDAO.getAllRandos(false);
        assertThat("Collection number is not correct", randos.size(), is(4));
    }

    @Test
    public void testGetAllRandosNumberOnlyToUpload() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 24.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file3", 33.33, 34.44, new Date()));
        assertThat("Items number in table is not correct", RandoDAO.countAllRandosNumber(), is(3));
    }

    @Test
    public void testGetAllRandosNumberOnlyPairs() {
        insertNRandomRandoPairs(4);
        assertThat("Items number in table is not correct", RandoDAO.countAllRandosNumber(), is(4));
    }

    @Test
    public void testGetAllRandosNumberPairsAndUploads() {
        assertThat("Items number in table is not correct", RandoDAO.countAllRandosNumber(), is(0));
    }

    @Test
    public void testGetAllRandosNumberEmptyTable() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 34.44, new Date()));
        insertNRandomRandoPairs(3);
        assertThat("Collection number is not correct", RandoDAO.countAllRandosNumber(), is(5));
    }

    @Test
    public void testGetRandosToUploadNumberEmptyTable() {
        assertThat("Items number in table is not correct", RandoDAO.countAllRandosNumber(), is(0));
    }

    @Test
    public void testGetRandosToUploadNumber() {
        addToUpload(new RandoUpload("/path/to/file1", 13.33, 14.44, new Date()));
        addToUpload(new RandoUpload("/path/to/file2", 23.33, 34.44, new Date()));
        assertThat("Items number in table is not correct", RandoDAO.countAllRandosNumber(), is(2));
    }

    @Test
    public void testGetRandoByStatusEmptyDB(){
        assertThat("Not empty list returned", RandoDAO.getAllRandosByStatus(Rando.Status.IN).size(), is(0));
        assertThat("Not empty list returned", RandoDAO.getAllRandosByStatus(Rando.Status.OUT).size(), is(0));
    }

    @Test
    public void testGetRandoByStatusNoInRandos(){
        List<Rando> randos = new ArrayList<Rando>(2);
        Rando rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);

        assertThat("Not empty list returned", RandoDAO.getAllRandosByStatus(Rando.Status.IN).size(), is(0));
        assertThat("Not 2 items returned", RandoDAO.getAllRandosByStatus(Rando.Status.OUT).size(), is(2));

        checkListsEqual(randos, RandoDAO.getAllRandosByStatus(Rando.Status.OUT));

    }
    @Test
    public void testGetRandoByStatusNoOutRandos(){
        List<Rando> randos = new ArrayList<Rando>(2);
        Rando rando = getRandomRando(Rando.Status.IN);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.IN);
        randos.add(rando);
        RandoDAO.createRando(rando);

        assertThat("Not 2 items returned", RandoDAO.getAllRandosByStatus(Rando.Status.IN).size(), is(2));
        assertThat("Not empty list returned", RandoDAO.getAllRandosByStatus(Rando.Status.OUT).size(), is(0));

        checkListsEqual(randos, RandoDAO.getAllRandosByStatus(Rando.Status.IN));

    }
    @Test
    public void testGetRandoByStatusMixedRandos(){
        List<Rando> randosIn = new ArrayList<Rando>(2);
        Rando rando = getRandomRando(Rando.Status.IN);
        randosIn.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.IN);
        randosIn.add(rando);
        RandoDAO.createRando(rando);

        List<Rando> randosOut = new ArrayList<Rando>(2);
        rando = getRandomRando(Rando.Status.OUT);
        randosOut.add(rando);
        RandoDAO.createRando(rando);

        assertThat("Not 2 items returned", RandoDAO.getAllRandosByStatus(Rando.Status.IN).size(), is(2));
        assertThat("Not 1 item returned", RandoDAO.getAllRandosByStatus(Rando.Status.OUT).size(), is(1));
        assertThat("Not 3 items returned", RandoDAO.getAllRandos().size(), is(3));

        checkListsEqual(randosIn, RandoDAO.getAllRandosByStatus(Rando.Status.IN));
        checkListsEqual(randosOut, RandoDAO.getAllRandosByStatus(Rando.Status.OUT));

    }

    @Test
    public void testGetInRandosNoInRandos(){
        List<Rando> randos = new ArrayList<>(2);
        Rando rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);

        assertThat("Not empty list returned", RandoDAO.getAllInRandos().size(), is(0));
        assertThat("Not 2 items returned", RandoDAO.getAllOutRandos().size(), is(2));
    }

    @Test
    public void testDeleteInRandos(){
        List<Rando> randos = new ArrayList<Rando>(2);
        Rando rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.OUT);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.IN);
        RandoDAO.createRando(rando);

        assertThat("Not 1 item returned", RandoDAO.getAllInRandos().size(), is(1));
        assertThat("IN Rando not the same", RandoDAO.getAllInRandos().get(0), is(rando));
        assertThat("Not 2 items returned", RandoDAO.getAllOutRandos().size(), is(2));
        checkListsEqual(randos, RandoDAO.getAllOutRandos());

        RandoDAO.clearInRandos();

        assertThat("Not empty list returned", RandoDAO.getAllInRandos().size(), is(0));
        assertThat("Not 2 items returned", RandoDAO.getAllOutRandos().size(), is(2));
        checkListsEqual(randos, RandoDAO.getAllOutRandos());
    }

    @Test
    public void testDeleteOutRandos(){
        List<Rando> randos = new ArrayList<Rando>(2);
        Rando rando = getRandomRando(Rando.Status.IN);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.IN);
        randos.add(rando);
        RandoDAO.createRando(rando);
        rando = getRandomRando(Rando.Status.OUT);
        RandoDAO.createRando(rando);

        assertThat("Not 1 item returned", RandoDAO.getAllOutRandos().size(), is(1));
        assertThat("OUT Rando not the same", RandoDAO.getAllOutRandos().get(0), is(rando));
        assertThat("Not 2 items returned", RandoDAO.getAllInRandos().size(), is(2));
        checkListsEqual(randos, RandoDAO.getAllInRandos());

        RandoDAO.clearOutRandos();

        assertThat("Not empty list returned", RandoDAO.getAllOutRandos().size(), is(0));
        assertThat("Not 2 items returned", RandoDAO.getAllInRandos().size(), is(2));
        checkListsEqual(randos, RandoDAO.getAllInRandos());
    }

    private void insertNRandomRandoPairs(int n) {
        RandoDAO.insertRandos(RandoTestHelper.getNRandomRandos(n, Rando.Status.IN));
    }

}