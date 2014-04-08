package com.eucsoft.foodex.api.callback;

import com.eucsoft.foodex.db.model.FoodPair;

import java.util.List;

public interface OnFetchUser {
    public void onFetch(List<FoodPair> foodPairs);
}
