package com.github.randoapp.test.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.test.db.RandoTestHelper;
import com.github.randoapp.util.RandoUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoUtilTest {

    Context context;

    @Before
    protected void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }

    @After
    protected void tearDown() throws Exception {
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }


    public void testAreRandoListsEqual() throws Exception {
        List<Rando> randos1 = RandoTestHelper.getNRandomRandos(3, Rando.Status.OUT);
        List<Rando> randos2 = new ArrayList<Rando>();
        for (Rando rando : randos1) {
            randos2.add(new Rando(rando));
        }

        assertThat("Lists are not equal", RandoUtil.areRandoListsEqual(randos1, randos2), is(true));
    }

    public void testAreRandoListsNotEqualBySize() throws Exception {
        List<Rando> randos1 = RandoTestHelper.getNRandomRandos(4, Rando.Status.OUT);
        List<Rando> randos2 = RandoTestHelper.getNRandomRandos(3, Rando.Status.OUT);

        assertThat("Lists are not equal", RandoUtil.areRandoListsEqual(randos1, randos2), is(false));
    }

    public void testAreRandoListsNotEqualByContent() throws Exception {
        List<Rando> randos1 = RandoTestHelper.getNRandomRandos(3, Rando.Status.OUT);
        List<Rando> randos2 = RandoTestHelper.getNRandomRandos(3, Rando.Status.IN);

        assertThat("Lists are not equal", RandoUtil.areRandoListsEqual(randos1, randos2), is(false));
    }
}
