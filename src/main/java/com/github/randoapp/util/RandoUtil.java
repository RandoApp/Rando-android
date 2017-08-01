package com.github.randoapp.util;

import com.github.randoapp.Constants;
import com.github.randoapp.db.model.Rando;

import org.json.JSONArray;
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
import static com.github.randoapp.Constants.RATING_PARAM;
import static com.github.randoapp.Constants.SMALL_PARAM;

public class RandoUtil {

    public static String getUrlByImageSize(int imageSize, Rando.UrlSize urls) {
        if (imageSize >= Constants.SIZE_LARGE) {
            return urls.large;
        } else if (imageSize >= Constants.SIZE_MEDIUM) {
            return urls.medium;
        }
        return urls.small;
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
            JSONArray detected = jsonRando.getJSONArray(DETECTED_PARAM);
            rando.detected = detected.join(",");
        }

        if (jsonRando.has(RATING_PARAM)){
            rando.rating = jsonRando.getInt(RATING_PARAM);
        }
        return rando;
    }

}
