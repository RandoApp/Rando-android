package com.eucsoft.foodex.db.model;

import java.util.Comparator;

public class FoodPairDateComparator implements Comparator<FoodPair> {

    @Override
    public int compare(FoodPair lhs, FoodPair rhs) {
        return (int) (rhs.user.foodDate.getTime() - lhs.user.foodDate.getTime());
    }
}
