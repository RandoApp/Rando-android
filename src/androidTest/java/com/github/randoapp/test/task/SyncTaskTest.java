package com.github.randoapp.test.task;

import android.test.AndroidTestCase;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.task.SyncTask;
import com.github.randoapp.test.db.RandoTestHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class SyncTaskTest extends AndroidTestCase {


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

    public void testNotChanged() throws Exception {
        List<Rando> randos = createRandoPairs();

        RandoDAO.insertRandos(randos);

        SyncTask syncTask = new SyncTask(randos);
        syncTask.executeSync();

        RandoTestHelper.checkListsEqual(RandoDAO.getAllRandos(), randos);
    }

    public void testRandoPairUpdated() throws Exception {
        List<Rando> randos = createRandoPairs();

        List<Rando> randosInDB = createRandoPairs();
        randosInDB.get(0).imageURL = "!BLAAAAA!!!!!!!!";
        randosInDB.get(0).mapURL = "!BLAAAAA!!!!!!!!";

        RandoDAO.insertRandos(randosInDB);

        SyncTask syncTask = new SyncTask(randos);
        syncTask.executeSync();

        RandoTestHelper.checkListsEqual(RandoDAO.getAllRandos(), randos);
    }

    public void testRandoPairAdded() throws Exception {
        List<Rando> randos = createRandoPairs();
        List<Rando> forDBList = createRandoPairs();
        forDBList.remove(0);

        RandoDAO.insertRandos(forDBList);

        SyncTask syncTask = new SyncTask(randos);
        syncTask.executeSync();

        RandoTestHelper.checkListsEqual(RandoDAO.getAllRandos(), randos);
    }

    public void testRandoPairRemoved() throws Exception {
        List<Rando> randos = createRandoPairs();
        List<Rando> forDBList = createRandoPairs();

        forDBList.add(RandoTestHelper.getRandomRando(Rando.Status.IN));
        RandoDAO.insertRandos(forDBList);

        SyncTask syncTask = new SyncTask(randos);
        syncTask.executeSync();

        RandoTestHelper.checkListsEqual(RandoDAO.getAllRandos(), randos);
    }

    public void testOkCallbackIsTriggeredWhenDBUpdated() throws Exception {
        List<Rando> randos = new ArrayList<Rando>();

        randos.add(RandoTestHelper.getRandomRando(Rando.Status.IN));

        OnOk okCallback = mock(OnOk.class);

        new SyncTask(randos)
        .onOk(okCallback)
        .executeSync();

        verify(okCallback, times(1)).onOk(anyMap());
    }

    public void testOkCallbackIsNotTriggeredWhenDBNotUpdated() throws Exception {
        List<Rando> randos = createRandoPairs();

        OnOk okCallback = mock(OnOk.class);

        new SyncTask(randos)
        .onOk(okCallback)
        .executeSync();

        verify(okCallback, times(1)).onOk(anyMap());
    }

    private List<Rando> createRandoPairs() {
        Rando rando1 = new Rando();
        rando1.randoId = "ddddcwef3242f32f";
        rando1.imageURL = "http://rando4.me/image/dddd/ddddcwef3242f32f.jpg";
        rando1.date = new Date(1383690800877l);
        rando1.mapURL = "http://rando4.me/map/eeee/eeeewef3242f32f.jpg";
        rando1.status = Rando.Status.IN;

        Rando rando2 = new Rando();
        rando2.randoId = "abcdw0ef3242f32f";
        rando2.imageURL = "http://rando4.me/image/abcd/abcdw0ef3242f32f.jpg";
        rando2.date = new Date(1383670400877l);
        rando2.mapURL = "http://rando4.me/map/bcde/bcdecwef3242f32f.jpg'";
        rando2.status = Rando.Status.IN;

        List<Rando> randos = new ArrayList<Rando>();
        randos.add(rando1);
        randos.add(rando2);
        return randos;
    }
}
