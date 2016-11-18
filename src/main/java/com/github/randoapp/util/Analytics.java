package com.github.randoapp.util;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private static String TAKE_RANDO = "take_rando";
    private static String UPLOAD_RANDO = "upload_rando";

    public static void logTakeRando(FirebaseAnalytics analytics) {
        analytics.logEvent(TAKE_RANDO, null);
    }

    public static void logUploadRando(FirebaseAnalytics analytics) {
        analytics.logEvent(UPLOAD_RANDO, null);
    }

}
