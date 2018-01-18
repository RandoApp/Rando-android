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
import static org.assertj.core.api.Assertions.tuple;

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

    /****** getNextRandoToUpload ****/

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
    public void shouldReturnNullAndDoNotAddToUploadWhenRandoToUploadIsJustEmpty() {
        assertThat(addToUpload(context, new RandoUpload())).isNull();
    }

    @Test
    public void shouldReturnNullAndDoNotAddToUploadWhenRandoToUploadDateIsNull() {
        RandoUpload randoUpload = new RandoUpload("/path/to/file1", 13.33, 14.44, null);
        assertThat(addToUpload(context, randoUpload)).isNull();
    }

    @Test
    public void shouldReturnNullAndDoNotAddToUploadWhenRandoToUploadFileIsNull() {
        RandoUpload randoUpload = new RandoUpload(null, 13.33, 14.44, new Date());
        assertThat(addToUpload(context, randoUpload)).isNull();
    }
}
