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

    public SyncTask(List<RandoPair> randoPairs) {
        this.randoPairs = randoPairs;
    }

    @Override
    public Integer run() {
        Log.v(SyncTask.class, "OnFetchUser");
        RandoDAO randoDAO = new RandoDAO(App.context);

        if (!isConsistent(randoDAO)) {
            randoDAO.clearRandoPairs();
            randoDAO.insertRandoPairs(randoPairs);
            randoDAO.close();

            data.put(NEED_NOTIFICATION, true);
            return OK;
        }

        return checkEachRandoIfChanged(randoDAO);
    }

    private boolean isConsistent(RandoDAO randoDAO) {
        return randoPairs.size() == randoDAO.getRandoPairsNumber();
    }

    private Integer checkEachRandoIfChanged(RandoDAO randoDAO) {
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
        } catch (IllegalArgumentException e) {
            Log.e(SyncTask.class, "Sync task error: ", e.getMessage());
        } finally {
            randoDAO.close();
        }
        return OK;
    }

}
