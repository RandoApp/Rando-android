package com.github.randoapp.test.util;

import android.test.AndroidTestCase;

import com.github.randoapp.api.beans.User;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.test.db.RandoTestHelper;
import com.github.randoapp.util.RandoUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RandoUtilTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
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
