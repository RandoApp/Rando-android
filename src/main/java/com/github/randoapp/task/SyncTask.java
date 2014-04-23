package com.github.randoapp.task;

import com.github.randoapp.App;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;

import java.util.Collections;
import java.util.List;

import static com.github.randoapp.Constants.NEED_NOTIFICATION;
import static com.github.randoapp.Constants.NOT_PAIRED_RANDO_PAIRS_NUMBER;

public class SyncTask extends BaseTask {

    private List<RandoPair> randoPairs;

    public SyncTask(List<RandoPair> randoPairs1) {
        this.randoPairs = randoPairs1;
    }

    @Override
    public Integer run() {
        Log.v(SyncTask.class, "OnFetchUser");
        RandoDAO randoDAO = new RandoDAO(App.context);

        if (randoPairs.size() != randoDAO.getRandoPairsNumber()) {
            randoDAO.clearRandoPairs();
            randoDAO.insertRandoPairs(randoPairs);
            data.put(NEED_NOTIFICATION, true);
        }

        try {
            List<RandoPair> dbRandoPairs = randoDAO.getAllRandoPairs();
            Collections.sort(randoPairs, new RandoPair.DateComparator());


            for (int i = 0; i < dbRandoPairs.size(); i++) {
                if (!dbRandoPairs.get(i).equals(randoPairs.get(i))) {
                    randoDAO.clearRandoPairs();
                    randoDAO.insertRandoPairs(randoPairs);
                    data.put(NEED_NOTIFICATION, true);
                }
            }

            data.put(NOT_PAIRED_RANDO_PAIRS_NUMBER, randoDAO.getNotPairedRandosNumber());
            randoDAO.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return OK;
    }

}
