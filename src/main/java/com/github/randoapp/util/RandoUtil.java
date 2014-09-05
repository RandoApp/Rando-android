package com.github.randoapp.util;

import com.github.randoapp.Constants;
import com.github.randoapp.db.model.Rando;

import java.util.List;

public class RandoUtil {

    public static String getUrlByImageSize(int imageSize, Rando.UrlSize urls) {
        if (imageSize >= Constants.SIZE_LARGE) {
            return urls.large;
        } else if (imageSize >= Constants.SIZE_MEDIUM) {
            return urls.medium;
        }
        return urls.small;
    }

    public static boolean areRandoListsEqual(List<Rando> newRandos, List<Rando> oldRandos) {
        return (oldRandos.size() == newRandos.size() && oldRandos.containsAll(newRandos));
    }
}
