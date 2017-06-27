package com.github.randoapp.api.listeners;

import org.json.JSONObject;

public interface NetworkResultListener {

    void onOk(JSONObject response);

    void onError(JSONObject error);
}
