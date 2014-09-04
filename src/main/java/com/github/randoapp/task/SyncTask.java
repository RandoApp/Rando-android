package com.github.randoapp.task;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;

import java.util.List;

import static com.github.randoapp.Constants.NEED_NOTIFICATION;
import static com.github.randoapp.Constants.NOT_PAIRED_RANDO_PAIRS_NUMBER;

public class SyncTask extends BaseTask {

    private List<Rando> randos;

    public SyncTask(List<Rando> randos) {
        this.randos = randos;
    }

    @Override
    public Integer run() {
        Log.v(SyncTask.class, "OnFetchUser");

        if (!isConsistent()) {
            RandoDAO.clearRandos();
            RandoDAO.insertRandos(randos);
            data.put(NEED_NOTIFICATION, true);
            //data.put(NOT_PAIRED_RANDO_PAIRS_NUMBER, RandoDAO.getNotPairedRandosNumber());
            return OK;
        }
        return checkEachRandoIfChanged();
    }


    private boolean isConsistent() {
        return randos.size() == RandoDAO.getRandosNumber();
    }

    private Integer checkEachRandoIfChanged() {
        try {
            List<Rando> dbRandos = RandoDAO.getAllRandos();
            for (Rando rando : randos) {
                if (!dbRandos.contains(rando)) {
                    RandoDAO.clearRandos();
                    RandoDAO.insertRandos(randos);
                    data.put(NEED_NOTIFICATION, true);
                    break;
                }
            }
            //data.put(NOT_PAIRED_RANDO_PAIRS_NUMBER, RandoDAO.getNotPairedRandosNumber());
        } catch (IllegalArgumentException e) {
            Log.e(SyncTask.class, "Sync task error: ", e.getMessage());
        }
        return OK;
    }
}
