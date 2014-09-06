package com.github.randoapp.util;

import com.github.randoapp.Constants;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.preferences.Preferences;

import java.util.List;

public class RandoUtil {

    public enum UserToDBResult {NO_UPDATES, OUT_UPDATED, OUT_ADDED, OUT_DELETED, IN_UPDATED, IN_DELETED, IN_ADDED}

    public static String getUrlByImageSize(int imageSize, Rando.UrlSize urls) {
        if (imageSize >= Constants.SIZE_LARGE) {
            return urls.large;
        } else if (imageSize >= Constants.SIZE_MEDIUM) {
            return urls.medium;
        }
        return urls.small;
    }

    public static boolean areRandoListsEqual(List<Rando> newRandos, List<Rando> oldRandos) {
        return (oldRandos.size() == newRandos.size() && oldRandos.containsAll(newRandos));
    }

    public static UserToDBResult userToDB(User user) {

        UserToDBResult userToDBResult = UserToDBResult.NO_UPDATES;

        //check RandosOut
        List<Rando> outRandosDB = RandoDAO.getAllOutRandos();
        if (!RandoUtil.areRandoListsEqual(outRandosDB, user.randosOut)) {
            RandoDAO.clearOutRandos();
            RandoDAO.insertRandos(user.randosOut);
            userToDBResult = UserToDBResult.OUT_UPDATED;
        }

        //check RandosIn
        List<Rando> inRandosDB = RandoDAO.getAllInRandos();
        if (!RandoUtil.areRandoListsEqual(inRandosDB, user.randosIn)) {
            for (Rando rando : user.randosIn) {
                if (!inRandosDB.contains(rando)) {
                    Preferences.decrementRandosBalance();
                }
            }
            RandoDAO.clearInRandos();
            RandoDAO.insertRandos(user.randosIn);
            userToDBResult = UserToDBResult.IN_UPDATED;
        }

        //Safe check
        if (Preferences.getRandosBalance() < 0) {
            Preferences.zeroRandosBalance();
        }
        return userToDBResult;
    }

}
