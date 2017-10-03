package com.github.randoapp.util;

import android.content.Context;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;

public class RandoUtil {

    public static boolean isRatedFirstTime(String randoId, Context context) {
        if (randoId != null && context != null) {
            Rando randoToUpdate = RandoDAO.getRandoByRandoId(context, randoId);
            return randoToUpdate != null && randoToUpdate.rating == 0;
        }
        return false;
    }
}
