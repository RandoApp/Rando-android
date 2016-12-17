package com.github.randoapp.test.db;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoUpload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoDAOUploadTableTest {

    @Before
    public void setUp() throws Exception {
        RandoDAO.clearRandoToUpload();
    }

    @After
    public void tearDown() throws Exception {
        RandoDAO.clearRandoToUpload();
    }

    @Test
    public void shouldReturnNullWhenUploadTableIsEmpty() {
        assertThat(RandoDAO.getNextRandoToUpload()).isNull();
    }

    @Test
    public void shouldReturnNullWhenAllItemsHaveLastTryEarlierThanLimit() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.lastTry = new Date(System.currentTimeMillis());
        RandoDAO.addToUpload(randoUpload);

        assertThat(RandoDAO.getNextRandoToUpload()).isNull();
    }

    @Test
    public void shouldReturnWhenThereIsOneMatching() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.file = "/dd/d/d/";
        randoUpload.lastTry = new Date(System.currentTimeMillis()- Constants.UPLOAD_RETRY_TIMEOUT - 1000);
        RandoDAO.addToUpload(randoUpload);

        assertThat(RandoDAO.getAllRandosToUpload("ASC")).isNotNull().hasSize(1);
        assertThat(RandoDAO.getNextRandoToUpload()).isNotNull().isEqualToComparingFieldByField(randoUpload);
    }

    @Test
    public void shouldReturnFirstOneByDateWhenThereIsTwoMatching() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.file = "/dd/d/d/";
        randoUpload.lastTry = new Date(System.currentTimeMillis()- Constants.UPLOAD_RETRY_TIMEOUT - 1000);
        RandoDAO.addToUpload(randoUpload);

        RandoUpload randoUpload1 = new RandoUpload();
        randoUpload1.date = new Date(System.currentTimeMillis() - 100);
        randoUpload1.file = "/dd/d/dd/";
        randoUpload1.lastTry = new Date(System.currentTimeMillis()- Constants.UPLOAD_RETRY_TIMEOUT - 1000);
        RandoDAO.addToUpload(randoUpload1);

        assertThat(RandoDAO.getAllRandosToUpload("ASC")).isNotNull().hasSize(2);
        assertThat(RandoDAO.getNextRandoToUpload()).isNotNull().isEqualToComparingFieldByField(randoUpload1);
    }
}
