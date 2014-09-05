package com.github.randoapp.test.service;

import android.test.AndroidTestCase;

import com.github.randoapp.api.beans.User;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.SyncService;
import com.github.randoapp.test.db.RandoTestHelper;

import static com.github.randoapp.test.db.RandoTestHelper.checkListsEqual;
import static com.github.randoapp.test.db.RandoTestHelper.getRandomRando;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class SyncServiceTest extends AndroidTestCase {




    /*public void testOnFetchUserImplNoUpdateZeroBalance() throws Exception {
        User user = new User();
        user.randosIn = RandoTestHelper.getNRandomRandos(10, Rando.Status.IN);
        user.randosOut = RandoTestHelper.getNRandomRandos(10, Rando.Status.OUT);

        RandoDAO.insertRandos(user.randosOut);
        RandoDAO.insertRandos(user.randosIn);

        SyncService.OnFetchUserImpl onFetchUser = new SyncService.OnFetchUserImpl();
        onFetchUser.onFetch(user);

        RandoTestHelper.checkListsEqual(RandoDAO.getAllOutRandos(), user.randosOut);
        RandoTestHelper.checkListsEqual(RandoDAO.getAllInRandos(), user.randosIn);
        assertThat("Rando Balance not 0", Preferences.getRandosBalance(), is(0));
    }*/

}

