package com.github.randoapp.util;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private static String TAKE_RANDO = "take_rando";
    private static String UPLOAD_RANDO = "upload_rando";
    private static String SHARE_RANDO = "share_rando";
    private static String DELETE_RANDO = "delete_rando";
    private static String REPORT_RANDO = "report_rando";
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
    private static String SWITCH_CAMERA_TO_FRONT = "switch_camera_to_front";
    private static String SWITCH_CAMERA_TO_BACK = "switch_camera_to_back";

    //unwanted rando events
    private static String CLICK_UNWANTED_RANDO = "click_unwanted_rando";
    private static String DELETE_UNWANTED_RANDO_DIALOG = "delete_unwanted_rando_dialog";
    private static String CANCEL_UNWANTED_RANDO_DIALOG = "cancel_unwanted_rando_dialog";

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

    public static void logReportRando(FirebaseAnalytics analytics) {
        analytics.logEvent(REPORT_RANDO, null);
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

    public static void logLogout(FirebaseAnalytics analytics) {
        analytics.logEvent(LOGOUT, null);
    }

    public static void logLoginSkip(FirebaseAnalytics analytics) {
        analytics.logEvent(LOGIN_SKIP, null);
    }

    public static void logLoginEmail(FirebaseAnalytics analytics) {
        analytics.logEvent(LOGIN_EMAIL, null);
    }

    public static void logLoginGoogle(FirebaseAnalytics analytics) {
        analytics.logEvent(LOGIN_GOOGLE, null);
    }

    public static void logOpenTabOwnRandos(FirebaseAnalytics analytics) {
        analytics.logEvent(OPEN_TAB_OWN_RANDOS, null);
    }

    public static void logOpenTabStrangerRandos(FirebaseAnalytics analytics) {
        analytics.logEvent(OPEN_TAB_STRANGER_RANDOS, null);
    }

    public static void logOpenTabSettings(FirebaseAnalytics analytics) {
        analytics.logEvent(OPEN_TAB_SETTINGS, null);
    }

    public static void logClickUnwantedRando(FirebaseAnalytics analytics) {
        analytics.logEvent(CLICK_UNWANTED_RANDO, null);
    }

    public static void logDeleteUnwantedRandoDialog(FirebaseAnalytics analytics) {
        analytics.logEvent(DELETE_UNWANTED_RANDO_DIALOG, null);
    }

    public static void logCancelUnwantedRandoDialog(FirebaseAnalytics analytics) {
        analytics.logEvent(CANCEL_UNWANTED_RANDO_DIALOG, null);
    }

    public static void logSwitchCameraToFront(FirebaseAnalytics analytics) {
        analytics.logEvent(SWITCH_CAMERA_TO_FRONT, null);
    }

    public static void logSwitchCameraToBack(FirebaseAnalytics analytics) {
        analytics.logEvent(SWITCH_CAMERA_TO_BACK, null);
    }

}
