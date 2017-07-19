package com.github.randoapp.test.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
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

import static com.github.randoapp.db.RandoDAO.addToUpload;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoDAOUploadTableTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        RandoDAO.clearRandoToUpload(context);
    }

    @After
    public void tearDown() throws Exception {
        RandoDAO.clearRandoToUpload(context);
    }

    @Test
    public void shouldReturnNullWhenUploadTableIsEmpty() {
        assertThat(RandoDAO.getNextRandoToUpload(context)).isNull();
    }

    @Test
    public void shouldReturnNullWhenAllItemsHaveLastTryEarlierThanLimit() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.lastTry = new Date(System.currentTimeMillis());
        addToUpload(context, randoUpload);

        assertThat(RandoDAO.getNextRandoToUpload(context)).isNull();
    }

    @Test
    public void shouldReturnWhenThereIsOneMatching() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.file = "/dd/d/d/";
        randoUpload.lastTry = new Date(System.currentTimeMillis() - Constants.UPLOAD_RETRY_TIMEOUT - 10000);
        addToUpload(context, randoUpload);

        assertThat(RandoDAO.getAllRandosToUpload(context, "ASC")).isNotNull().hasSize(1);
        assertThat(RandoDAO.getNextRandoToUpload(context)).isNotNull().isEqualToComparingFieldByField(randoUpload);
    }

    @Test
    public void shouldReturnFirstOneByDateWhenThereIsTwoMatching() {
        RandoUpload randoUpload = new RandoUpload();
        randoUpload.date = new Date();
        randoUpload.file = "/dd/d/d/";
        randoUpload.lastTry = new Date(System.currentTimeMillis() - Constants.UPLOAD_RETRY_TIMEOUT - 1000);
        randoUpload = addToUpload(context, randoUpload);

        RandoUpload randoUpload1 = new RandoUpload();
        randoUpload1.date = new Date(randoUpload.date.getTime() - 1000);
        randoUpload1.file = "/dd/d/dd/";
        randoUpload1.lastTry = new Date(System.currentTimeMillis() - Constants.UPLOAD_RETRY_TIMEOUT - 1000);
        randoUpload1 = addToUpload(context, randoUpload1);

        assertThat(RandoDAO.getAllRandosToUpload(context, "ASC")).isNotNull().hasSize(2);
        assertThat(RandoDAO.getNextRandoToUpload(context)).isNotNull().isEqualToComparingFieldByField(randoUpload1);
    }
}
