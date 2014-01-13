package com.eucsoft.foodex.api;

import com.eucsoft.foodex.db.model.FoodPair;

import java.util.List;

public interface OnFetchUser {
    public void onFetch(List<FoodPair> foodPairs);
}
