package com.eucsoft.foodex.util;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.db.model.FoodPair;

public class FoodPairUtil {

    public static String getUrlByImageSize(int foodImageSize, FoodPair.User.UrlSize urls) {
        if (foodImageSize >= Constants.SIZE_LARGE) {
            return urls.large;
        } else if (foodImageSize >= Constants.SIZE_MEDIUM) {
            return urls.medium;
        }
        return urls.small;
    }
}
