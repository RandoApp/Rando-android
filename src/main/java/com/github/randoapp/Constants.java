package com.github.randoapp;

import android.app.AlarmManager;

import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String ALBUM_NAME = "Rando";

    public static final String FILEPATH = "FILEPATH";
    public static final String TOTAL_RANDOS_NUMBER = "TOTAL_RANDOS_NUMBER";
    public static final String UPDATE_STATUS = "UPDATE_STATUS";
    public static final String UPDATED = "UPDATED";
    public static final String NOT_UPDATED = "NOT_UPDATED";

    /**
     * Default on-disk cache directory.
     */
    public static final String DEFAULT_CACHE_DIR = "volley";
    public static final int DEFAULT_CACHE_SIZE = 20 * 1024 * 1024;

    public static final String CAMERA_BROADCAST_EVENT = "RANDO_CAMERA_EVENT";
    public static final String SYNC_BROADCAST_EVENT = "Rando4MeSyncEvent";
    public static final String UPLOAD_SERVICE_BROADCAST_EVENT = "Rando4MeUploadDoneEvent";
    public static final String AUTH_FAILURE_BROADCAST_EVENT = "Rando4MeAuthFailureEvent";
    public static final String LOGOUT_BROADCAST_EVENT = "Rando4MeLogoutEvent";
    public static final String AUTH_SUCCCESS_BROADCAST_EVENT = "Rando4MeAuthSuccessEvent";
    public static final String PUSH_NOTIFICATION_BROADCAST_EVENT = "Rando4MePushNotificationEvent";

    public static final String RANDO_PHOTO_PATH = "RANDO_PHOTO_PATH";

    public static final String IMAGE_FILTER = "image/*";
    public static final String IMAGE_PREFIX = "IMG_";
    public static final String IMAGE_POSTFIX = ".jpg";

    public static final long LOCATION_DETECT_TIMEOUT = 5 * 60 * 1000; //in milliseconds

    public static final String ERROR = "error";
    public static final int UNAUTHORIZED_CODE = 400;
    public static final int FORBIDDEN_CODE = 411;


    public static final int DESIRED_PICTURE_SIZE = 1200;
    public static final int SIZE_SMALL = 480;
    public static final int SIZE_MEDIUM = 800;
    public static final int SIZE_LARGE = 1200;

    public static final String SERVER_HOST = BuildConfig.RANDO_HOST;
    public static final String SERVER_URL = "https://" + SERVER_HOST;
    public static final int ESTABLISH_CONNECTION_TIMEOUT = 5 * 60 * 1000;
    public static final int UPLOAD_CONNECTION_TIMEOUT = (int) TimeUnit.MINUTES.toMillis(5);
    public static final int API_CONNECTION_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(20);

    public static final int UPLOAD_RETRY_TIMEOUT = 5 * 60 * 1000;

    public static final int UPLOAD_SERVICE_ATTEMPTS_FAIL = 5;
    public static final long UPLOAD_SERVICE_SHORT_PAUSE = 5 * 1000;
    public static final long UPLOAD_SERVICE_LONG_PAUSE = AlarmManager.INTERVAL_HOUR * 2;

    //Shared Preferences
    public static final String PREFERENCES_FILE_NAME = "rando.prefs";
    public static final String AUTH_TOKEN = "auth.token";
    public static final String FIREBASE_INSTANCE_ID = "firebase.instance.id";
    public static final String UPDATE_PLAY_SETVICES_DIALOG_SHOWN_DATE = "play.services.update.dialog.shown.date";
    public static final String RANDOS_BALANCE = "randos.balance";
    public static final String CAMERA_FACING = "camera.facing";
    public static final String CAMERA_FLASH_MODE = "camera.flash.mode";
    public static final String ACCOUNT = "account";
    public static final String TRAINING_FRAGMENT_SHOWN = "training.fragment.shown";

    public static final String AUTH_TOKEN_PARAM = "token";
    public static final String SIGNUP_EMAIL_PARAM = "email";
    public static final String SIGNUP_PASSWORD_PARAM = "password";
    public static final String FACEBOOK_EMAIL_PARAM = "email";
    public static final String FACEBOOK_ID_PARAM = "id";
    public static final String FACEBOOK_TOKEN_PARAM = "token";
    public static final String GOOGLE_EMAIL_PARAM = "email";
    public static final String GOOGLE_TOKEN_PARAM = "token";
    public static final String GOOGLE_FAMILY_NAME_PARAM = "family_name";
    public static final String ANONYMOUS_ID_PARAM = "id";
    public static final String IMAGE_URL_PARAM = "imageURL";
    public static final String IMAGE_URL_SIZES_PARAM = "imageSizeURL";
    public static final String MAP_URL_SIZES_PARAM = "mapSizeURL";
    public static final String SMALL_PARAM = "small";
    public static final String MEDIUM_PARAM = "medium";
    public static final String LARGE_PARAM = "large";
    public static final String RANDO_ID_PARAM = "randoId";
    public static final String MAP_URL_PARAM = "mapURL";
    public static final String CREATION_PARAM = "creation";
    public static final String RANDO_PARAM = "rando";
    public static final String IN_RANDOS_PARAM = "in";
    public static final String OUT_RANDOS_PARAM = "out";
    public static final String EMAIL_PARAM = "email";
    public static final String LOCATION = "location";
    public static final String LATITUDE_PARAM = "latitude";
    public static final String LONGITUDE_PARAM = "longitude";
    public static final String FIREBASE_INSTANCE_ID_PARAM = "firebaseInstanceId";
    public static final String FIREBASE_INSTANCE_ID_HEADER = "FirebaseInstanceId";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String PAGE = "page";

    public static final String ERROR_MESSAGE_PARAM = "message";
    public static final String ERROR_CODE_PARAM = "code";
    public static final String ERROR_STATUS_PARAM = "status";

    public static final String IMAGE_PARAM = "image";

    public static final String IMAGE_MIME_TYPE = "image/jpeg";
    public static final String SIGNUP_URL = SERVER_URL + "/user";
    public static final String FACEBOOK_URL = SERVER_URL + "/facebook";
    public static final String GOOGLE_URL = SERVER_URL + "/google";
    public static final String ANONYMOUS_URL = SERVER_URL + "/anonymous";
    public static final String LOGOUT_URL = SERVER_URL + "/logout";
    public static final String FETCH_USER_URL = SERVER_URL + "/user";
    public static final String UPLOAD_RANDO_URL = SERVER_URL + "/image";
    public static final String DELETE_URL = SERVER_URL + "/delete/";
    public static final String LOG_URL = SERVER_URL + "/log";
    public static final String SHARE_URL = SERVER_URL + "/s/%s";

    public static final String REPORT_BROADCAST = "Report";

    public static final String GOOGLE_AUTH_SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";

    private static final int NUMBER_OF_IMAGES_IN_ONE_SET = 4; //stranger's + user's images and maps
    public static final int NUMBER_OF_IMAGES_FOR_CACHING = NUMBER_OF_IMAGES_IN_ONE_SET * 2; //2 visible image sets per screen

    public static final int GOOGLE_ACTIVITIES_AUTH_REQUEST_CODE = 483; //value is not matter

    public static final int CAMERA_ACTIVITY_UPLOAD_PRESSED_RESULT_CODE = 628;
    public static final int CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE = 623;
    public static final int UPDATE_PLAY_SERVICES_REQUEST_CODE = 2404;
    public static final int CAMERA_ACTIVITY_CAMERA_PERMISSION_REQUIRED = 629;


    public static final int ALL_PERMISSIONS_REQUEST_CODE = 701;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 702;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 703;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 704;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 705;

    public static final String PUSH_NOTIFICATION_LANDED = "landed";
    public static final String PUSH_NOTIFICATION_RECEIVED = "received";

    public static final String TO_UPLOAD_RANDO_ID = "randoIdToUpload";
    public static final String UPLOAD_RANDO = "randoUpload";

}

