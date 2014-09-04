package com.github.randoapp.api.listeners;

import com.android.volley.Response;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.randoapp.Constants.CREATION_PARAM;
import static com.github.randoapp.Constants.EMAIL_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.IN_RANDOS_PARAM;
import static com.github.randoapp.Constants.LARGE_PARAM;
import static com.github.randoapp.Constants.MAP_URL_PARAM;
import static com.github.randoapp.Constants.MAP_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.MEDIUM_PARAM;
import static com.github.randoapp.Constants.OUT_RANDOS_PARAM;
import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.SMALL_PARAM;
import static com.github.randoapp.Constants.USER_PARAM;

public class UserFetchResultListener implements Response.Listener<JSONObject> {
    private OnFetchUser listener;

    public UserFetchResultListener(final OnFetchUser listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {

            User fetchedUser = new User();

            fetchedUser.email = response.getString(EMAIL_PARAM);
            Preferences.setAccount(fetchedUser.email);

            JSONArray jsonInRandos = response.getJSONArray(IN_RANDOS_PARAM);
            JSONArray jsonOutRandos = response.getJSONArray(OUT_RANDOS_PARAM);

            List<Rando> inRandos = new ArrayList<Rando>(jsonInRandos.length());
            List<Rando> outRandos = new ArrayList<Rando>(jsonOutRandos.length());

            fetchedUser.randosIn = inRandos;
            fetchedUser.randosOut = outRandos;

            for (int i = 0; i < jsonInRandos.length(); i++) {
                inRandos.add(parseRando(jsonInRandos.getJSONObject(i)));
            }
            for (int i = 0; i < jsonOutRandos.length(); i++) {
                outRandos.add(parseRando(jsonOutRandos.getJSONObject(i)));
            }
            listener.onFetch(fetchedUser);

        } catch (JSONException e) {
            Log.e(UserFetchResultListener.class, "onResponse method", e);
        }
    }

    private Rando parseRando(JSONObject jsonRando) throws JSONException{
        Rando rando = new Rando();
        JSONObject userRandoUrlSizes = jsonRando.getJSONObject(IMAGE_URL_SIZES_PARAM);
        JSONObject userMapUrlSizes = jsonRando.getJSONObject(MAP_URL_SIZES_PARAM);

        rando.randoId = jsonRando.getString(RANDO_ID_PARAM);
        rando.imageURL = jsonRando.getString(IMAGE_URL_PARAM);
        rando.imageURLSize.small = userRandoUrlSizes.getString(SMALL_PARAM);
        rando.imageURLSize.medium = userRandoUrlSizes.getString(MEDIUM_PARAM);
        rando.imageURLSize.large = userRandoUrlSizes.getString(LARGE_PARAM);

        rando.mapURL = jsonRando.getString(MAP_URL_PARAM);
        rando.mapURLSize.small = userMapUrlSizes.getString(SMALL_PARAM);
        rando.mapURLSize.medium = userMapUrlSizes.getString(MEDIUM_PARAM);
        rando.mapURLSize.large = userMapUrlSizes.getString(LARGE_PARAM);

        rando.date = new Date(jsonRando.getLong(CREATION_PARAM));
        return rando;
    }
}
