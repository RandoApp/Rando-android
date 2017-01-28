package com.github.randoapp.util;

import com.github.randoapp.Constants;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.preferences.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import static com.github.randoapp.Constants.CREATION_PARAM;
import static com.github.randoapp.Constants.DETECTED_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.LARGE_PARAM;
import static com.github.randoapp.Constants.MAP_URL_PARAM;
import static com.github.randoapp.Constants.MAP_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.MEDIUM_PARAM;
import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.SMALL_PARAM;

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

    public static Rando parseRando(String jsonRandoString, Rando.Status status) {
        try {
            return parseRando(new JSONObject(jsonRandoString), status);
        } catch (JSONException e){
            return null;
        }
    }

    public static Rando parseRando(JSONObject jsonRando, Rando.Status status) throws JSONException {
        Rando rando = new Rando();
        JSONObject userRandoUrlSizes = jsonRando.getJSONObject(IMAGE_URL_SIZES_PARAM);
        JSONObject userMapUrlSizes = jsonRando.getJSONObject(MAP_URL_SIZES_PARAM);

        rando.randoId = jsonRando.getString(RANDO_ID_PARAM);
        rando.imageURL = jsonRando.getString(IMAGE_URL_PARAM);
        rando.status = status;
        rando.imageURLSize.small = userRandoUrlSizes.getString(SMALL_PARAM);
        rando.imageURLSize.medium = userRandoUrlSizes.getString(MEDIUM_PARAM);
        rando.imageURLSize.large = userRandoUrlSizes.getString(LARGE_PARAM);

        rando.mapURL = jsonRando.getString(MAP_URL_PARAM);
        rando.mapURLSize.small = userMapUrlSizes.getString(SMALL_PARAM);
        rando.mapURLSize.medium = userMapUrlSizes.getString(MEDIUM_PARAM);
        rando.mapURLSize.large = userMapUrlSizes.getString(LARGE_PARAM);

        rando.date = new Date(jsonRando.getLong(CREATION_PARAM));

        if (jsonRando.has(DETECTED_PARAM)){
            rando.detected = jsonRando.getString(DETECTED_PARAM);
        }
        return rando;
    }

}
