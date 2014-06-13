package com.github.randoapp.test.task;

import android.test.AndroidTestCase;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.task.SyncTask;
import com.github.randoapp.test.db.RandoPairTestHelper;

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
        RandoDAO.clearRandoPairs();
        RandoDAO.clearRandoToUpload();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RandoDAO.clearRandoPairs();
        RandoDAO.clearRandoToUpload();
    }

    public void testNotChanged() throws Exception {
        List<RandoPair> RandoPairs = createRandoPairs();

        RandoDAO.insertRandoPairs(RandoPairs);

        SyncTask syncTask = new SyncTask(RandoPairs);
        syncTask.executeSync();

        RandoPairTestHelper.checkListsEqual(RandoDAO.getAllRandoPairs(), RandoPairs);
    }

    public void testRandoPairUpdated() throws Exception {
        List<RandoPair> RandoPairs = createRandoPairs();

        List<RandoPair> RandoPairsInDB = createRandoPairs();
        RandoPairsInDB.get(0).user.imageURL = "!BLAAAAA!!!!!!!!";
        RandoPairsInDB.get(0).user.mapURL = "!BLAAAAA!!!!!!!!";

        RandoDAO.insertRandoPairs(RandoPairsInDB);

        SyncTask syncTask = new SyncTask(RandoPairs);
        syncTask.executeSync();

        RandoPairTestHelper.checkListsEqual(RandoDAO.getAllRandoPairs(), RandoPairs);
    }

    public void testRandoPairAdded() throws Exception {
        List<RandoPair> RandoPairs = createRandoPairs();
        List<RandoPair> forDBList = createRandoPairs();
        forDBList.remove(0);

        RandoDAO.insertRandoPairs(forDBList);

        SyncTask syncTask = new SyncTask(RandoPairs);
        syncTask.executeSync();

        RandoPairTestHelper.checkListsEqual(RandoDAO.getAllRandoPairs(), RandoPairs);
    }

    public void testRandoPairRemoved() throws Exception {
        List<RandoPair> RandoPairs = createRandoPairs();
        List<RandoPair> forDBList = createRandoPairs();

        forDBList.add(RandoPairTestHelper.getRandomRandoPair());
        RandoDAO.insertRandoPairs(forDBList);

        SyncTask syncTask = new SyncTask(RandoPairs);
        syncTask.executeSync();

        RandoPairTestHelper.checkListsEqual(RandoDAO.getAllRandoPairs(), RandoPairs);
    }

    public void testOkCallbackIsTriggeredWhenDBUpdated() throws Exception {
        List<RandoPair> RandoPairs = new ArrayList<RandoPair>();

        RandoPairs.add(RandoPairTestHelper.getRandomRandoPairNotPaired());

        OnOk okCallback = mock(OnOk.class);

        new SyncTask(RandoPairs)
        .onOk(okCallback)
        .executeSync();

        verify(okCallback, times(1)).onOk(anyMap());
    }

    public void testOkCallbackIsNotTriggeredWhenDBNotUpdated() throws Exception {
        List<RandoPair> RandoPairs = createRandoPairs();

        OnOk okCallback = mock(OnOk.class);

        new SyncTask(RandoPairs)
        .onOk(okCallback)
        .executeSync();

        verify(okCallback, times(1)).onOk(anyMap());
    }

    private List<RandoPair> createRandoPairs() {
        RandoPair RandoPair1 = new RandoPair();
        RandoPair1.user.randoId = "ddddcwef3242f32f";
        RandoPair1.user.imageURL = "http://rando4.me/image/dddd/ddddcwef3242f32f.jpg";
        RandoPair1.user.date = new Date(1383690800877l);
        RandoPair1.user.mapURL = "http://rando4.me/map/eeee/eeeewef3242f32f.jpg";
        RandoPair1.stranger.randoId = "abcwef3242f32f";
        RandoPair1.stranger.imageURL = "http://rando4.me/image/abc/abcwef3242f32f.jpg";
        RandoPair1.stranger.mapURL = "http://rando4.me/map/azca/azcacwef3242f32f.jpg";

        RandoPair RandoPair2 = new RandoPair();
        RandoPair2.user.randoId = "abcdw0ef3242f32f";
        RandoPair2.user.imageURL = "http://rando4.me/image/abcd/abcdw0ef3242f32f.jpg";
        RandoPair2.user.date = new Date(1383670400877l);
        RandoPair2.user.mapURL = "http://rando4.me/map/bcde/bcdecwef3242f32f.jpg'";
        RandoPair2.stranger.randoId = "abcd3cwef3242f32f";
        RandoPair2.stranger.imageURL = "http://rando4.me/image/abcd/abcd3cwef3242f32f.jpg";
        RandoPair2.stranger.mapURL = "http://rando4.me/map/abcd/abcd5wef3242f32f.jpg";

        List<RandoPair> RandoPairs = new ArrayList<RandoPair>();
        RandoPairs.add(RandoPair1);
        RandoPairs.add(RandoPair2);
        return RandoPairs;
    }
}
