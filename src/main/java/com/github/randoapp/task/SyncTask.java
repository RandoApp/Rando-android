package com.github.randoapp.task;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;

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

        if (!isConsistent()) {
            RandoDAO.clearRandoPairs();
            RandoDAO.insertRandoPairs(randoPairs);
            //randoDAO.close();
            data.put(NEED_NOTIFICATION, true);
            data.put(NOT_PAIRED_RANDO_PAIRS_NUMBER, RandoDAO.getNotPairedRandosNumber());
            //randoDAO.close();
            return OK;
        }
        return checkEachRandoIfChanged();
    }


    private boolean isConsistent() {
        return randoPairs.size() == RandoDAO.getRandoPairsNumber();
    }

    private Integer checkEachRandoIfChanged() {
        try {
            List<RandoPair> dbRandoPairs = RandoDAO.getAllRandoPairs();
            for (RandoPair randoPair : randoPairs) {
                if (!dbRandoPairs.contains(randoPair)) {
                    RandoDAO.clearRandoPairs();
                    RandoDAO.insertRandoPairs(randoPairs);
                    data.put(NEED_NOTIFICATION, true);
                    break;
                }
            }
            data.put(NOT_PAIRED_RANDO_PAIRS_NUMBER, RandoDAO.getNotPairedRandosNumber());
        } catch (IllegalArgumentException e) {
            Log.e(SyncTask.class, "Sync task error: ", e.getMessage());
        }
        return OK;
    }
}
