package com.github.randoapp.util;

import com.github.randoapp.Constants;
import com.github.randoapp.db.model.RandoPair;

public class RandoPairUtil {

    public static String getUrlByImageSize(int imageSize, RandoPair.User.UrlSize urls) {
        if (imageSize >= Constants.SIZE_LARGE) {
            return urls.large;
        } else if (imageSize >= Constants.SIZE_MEDIUM) {
            return urls.medium;
        }
        return urls.small;
    }
}
