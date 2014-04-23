package com.github.randoapp.api.callback;

import com.github.randoapp.db.model.RandoPair;

import java.util.List;

public interface OnFetchUser {
    public void onFetch(List<RandoPair> foodPairs);
}
