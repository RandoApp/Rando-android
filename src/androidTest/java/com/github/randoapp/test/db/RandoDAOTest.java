package com.github.randoapp.test.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
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
import java.util.List;

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

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }

    @After
    public void tearDown() throws Exception {
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }

    @Test
    public void testSelectRando() throws SQLException {
        Rando rando = getRandomRando(Rando.Status.IN);

        Rando newRando = RandoDAO.createRando(context, rando);
        newRando = RandoDAO.getRandoById(context, newRando.id);

        assertThat(rando, is(newRando));
    }

    @Test
    public void testReturnOrder() throws SQLException {
        insertNRandomRandoPairs(55);
        List<Rando> randos = RandoDAO.getAllRandos(context);
        org.assertj.core.api.Assertions.assertThat(randos).isSortedAccordingTo(new Rando.DateComparator());

    }

    @Test
    public void testGetNotExistingRandoPair() throws SQLException {
        insertNRandomRandoPairs(55);
        Rando rando = RandoDAO.getRandoById(context, 9999L);
        assertThat(rando, nullValue());
    }

    @Test
    public void testInsertNullRandoPair() throws SQLException {
        Rando rando = RandoDAO.createRando(context, null);
        assertThat(rando, nullValue());
    }


    private void insertNRandomRandoPairs(int n) {
        RandoDAO.insertRandos(context, RandoTestHelper.getNRandomRandos(n, Rando.Status.IN));
    }

}