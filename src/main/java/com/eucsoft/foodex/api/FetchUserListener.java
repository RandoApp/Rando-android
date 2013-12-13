package com.eucsoft.foodex.api;

import com.eucsoft.foodex.db.model.FoodPair;
import java.util.List;

public interface FetchUserListener {
    public void userFetched(List<FoodPair> foodPairs);
}
