package com.github.randoapp.api.listeners;

import android.content.Context;

import com.android.volley.Response;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.RandoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.github.randoapp.Constants.EMAIL_PARAM;
import static com.github.randoapp.Constants.IN_RANDOS_PARAM;
import static com.github.randoapp.Constants.OUT_RANDOS_PARAM;

public class UserFetchResultListener implements Response.Listener<JSONObject> {
    private OnFetchUser listener;
    private Context context;

    private UserFetchResultListener() {
        super();
    }

    public UserFetchResultListener(final Context context, final OnFetchUser listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {

            User fetchedUser = new User();

            fetchedUser.email = response.getString(EMAIL_PARAM);
            Preferences.setAccount(this.context, fetchedUser.email);

            JSONArray jsonInRandos = response.getJSONArray(IN_RANDOS_PARAM);
            JSONArray jsonOutRandos = response.getJSONArray(OUT_RANDOS_PARAM);

            List<Rando> inRandos = new ArrayList<Rando>(jsonInRandos.length());
            List<Rando> outRandos = new ArrayList<Rando>(jsonOutRandos.length());

            fetchedUser.randosIn = inRandos;
            fetchedUser.randosOut = outRandos;

            for (int i = 0; i < jsonInRandos.length(); i++) {
                inRandos.add(RandoUtil.parseRando(jsonInRandos.getJSONObject(i), Rando.Status.IN));
            }
            for (int i = 0; i < jsonOutRandos.length(); i++) {
                outRandos.add(RandoUtil.parseRando(jsonOutRandos.getJSONObject(i), Rando.Status.OUT));
            }
            listener.onFetch(fetchedUser);

        } catch (JSONException e) {
            Log.e(UserFetchResultListener.class, "onResponse method", e);
        }
    }
}
