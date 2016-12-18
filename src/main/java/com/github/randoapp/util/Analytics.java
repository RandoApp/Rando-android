package com.github.randoapp.util;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private static String TAKE_RANDO = "take_rando";
    private static String UPLOAD_RANDO = "upload_rando";
    private static String SHARE_RANDO = "share_rando";
    private static String DELETE_RANDO = "delete_rando";
    private static String TAP_STRANGER_RANDO = "tap_stranger_rando";
    private static String TAP_OWN_RANDO = "tap_own_rando";
    private static String FORCE_SYNC = "force_sync";
    private static String LOGOUT = "logout";
    private static String LOGIN_SKIP = "login_skip";
    private static String LOGIN_GOOGLE = "login_google";
    private static String LOGIN_EMAIL = "login_email";

    private static String OPEN_TAB_OWN_RANDOS = "open_tab_own_randos";
    private static String OPEN_TAB_STRANGER_RANDOS = "open_tab_stranger_randos";
    private static String OPEN_TAB_SETTINGS = "open_tab_settings";

    public static void logTakeRando(FirebaseAnalytics analytics) {
        analytics.logEvent(TAKE_RANDO, null);
    }

    public static void logUploadRando(FirebaseAnalytics analytics) {
        analytics.logEvent(UPLOAD_RANDO, null);
    }

    public static void logShareRando(FirebaseAnalytics analytics) {
        analytics.logEvent(SHARE_RANDO, null);
    }

    public static void logDeleteRando(FirebaseAnalytics analytics) {
        analytics.logEvent(DELETE_RANDO, null);
    }

    public static void logTapStrangerRando(FirebaseAnalytics analytics) {
        analytics.logEvent(TAP_STRANGER_RANDO, null);
    }

    public static void logTapOwnRando(FirebaseAnalytics analytics) {
        analytics.logEvent(TAP_OWN_RANDO, null);
    }

    public static void logForceSync(FirebaseAnalytics analytics) {
        analytics.logEvent(FORCE_SYNC, null);
    }

}
