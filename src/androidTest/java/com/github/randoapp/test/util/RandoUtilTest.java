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
        Preferences.zeroRandosBalance();
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

    public void testOnFetchUserImplNoUpdateZeroBalance() throws Exception {
        User user = new User();
        user.randosIn = RandoTestHelper.getNRandomRandos(10, Rando.Status.IN);
        user.randosOut = RandoTestHelper.getNRandomRandos(10, Rando.Status.OUT);

        RandoDAO.insertRandos(user.randosOut);
        RandoDAO.insertRandos(user.randosIn);

        assertThat("Not 10 Out Randos", RandoDAO.getAllInRandos().size(), is(10));
        assertThat("Not 10 IN Randos", RandoDAO.getAllOutRandos().size(), is(10));

        assertThat("", RandoUtil.userToDB(user), is(RandoUtil.UserToDBResult.NO_UPDATES));

        assertThat("Not 10 Out Randos", RandoDAO.getAllInRandos().size(), is(10));
        assertThat("Not 10 IN Randos", RandoDAO.getAllOutRandos().size(), is(10));

        RandoTestHelper.checkListsEqual(RandoDAO.getAllOutRandos(), user.randosOut);
        RandoTestHelper.checkListsEqual(RandoDAO.getAllInRandos(), user.randosIn);
        assertThat("Rando Balance not 0", Preferences.getRandosBalance(), is(0));
    }

    public void testOnFetchUserImplInUpdateBalance2To1() throws Exception {
        Preferences.incrementRandosBalance();
        Preferences.incrementRandosBalance();
        User user = new User();
        user.randosIn = RandoTestHelper.getNRandomRandos(9, Rando.Status.IN);
        user.randosOut = RandoTestHelper.getNRandomRandos(10, Rando.Status.OUT);

        RandoDAO.insertRandos(user.randosOut);
        RandoDAO.insertRandos(user.randosIn);
        user.randosIn.add(RandoTestHelper.getRandomRando(Rando.Status.IN));

        assertThat("Not 9 Out Randos", RandoDAO.getAllInRandos().size(), is(9));
        assertThat("Not 10 IN Randos", RandoDAO.getAllOutRandos().size(), is(10));

        assertThat("", RandoUtil.userToDB(user), is(RandoUtil.UserToDBResult.IN_UPDATED));

        assertThat("Not 10 Out Randos", RandoDAO.getAllInRandos().size(), is(10));
        assertThat("Not 10 IN Randos", RandoDAO.getAllOutRandos().size(), is(10));

        RandoTestHelper.checkListsEqual(RandoDAO.getAllInRandos(), user.randosIn);
        RandoTestHelper.checkListsEqual(RandoDAO.getAllOutRandos(), user.randosOut);

        assertThat("Rando Balance not 1", Preferences.getRandosBalance(), is(1));
    }


    public void testOnFetchUserImplOutUpdateBalanceSame() throws Exception {
        Preferences.incrementRandosBalance();
        Preferences.incrementRandosBalance();
        User user = new User();
        user.randosIn = RandoTestHelper.getNRandomRandos(10, Rando.Status.IN);
        user.randosOut = RandoTestHelper.getNRandomRandos(9, Rando.Status.OUT);

        RandoDAO.insertRandos(user.randosOut);
        RandoDAO.insertRandos(user.randosIn);
        user.randosOut.add(RandoTestHelper.getRandomRando(Rando.Status.OUT));

        assertThat("Not 10 Out Randos", RandoDAO.getAllInRandos().size(), is(10));
        assertThat("Not 9 IN Randos", RandoDAO.getAllOutRandos().size(), is(9));

        assertThat("", RandoUtil.userToDB(user), is(RandoUtil.UserToDBResult.OUT_UPDATED));

        assertThat("Not 10 Out Randos", RandoDAO.getAllInRandos().size(), is(10));
        assertThat("Not 10 IN Randos", RandoDAO.getAllOutRandos().size(), is(10));

        RandoTestHelper.checkListsEqual(RandoDAO.getAllInRandos(), user.randosIn);
        RandoTestHelper.checkListsEqual(RandoDAO.getAllOutRandos(), user.randosOut);

        assertThat("Rando Balance not 2", Preferences.getRandosBalance(), is(2));
    }

}
