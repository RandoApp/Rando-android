package com.github.randoapp.api;

import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.api.exception.ForbiddenException;
import com.github.randoapp.api.exception.RequestTooLongException;
import com.github.randoapp.api.listeners.ErrorResponseListener;
import com.github.randoapp.api.listeners.UserFetchResultListener;
import com.github.randoapp.api.request.BackgroundPreprocessRequest;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.notification.Notification;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.RandoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.NoHttpResponseException;
import cz.msebera.android.httpclient.auth.AuthenticationException;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.RedirectException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.conn.ConnectionPoolTimeoutException;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.github.randoapp.Constants.ANONYMOUS_ID_PARAM;
import static com.github.randoapp.Constants.ANONYMOUS_URL;
import static com.github.randoapp.Constants.AUTHORIZATION_HEADER;
import static com.github.randoapp.Constants.ERROR_CODE_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_EMAIL_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_ID_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_TOKEN_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_URL;
import static com.github.randoapp.Constants.FETCH_USER_URL;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID_HEADER;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID_PARAM;
import static com.github.randoapp.Constants.FORBIDDEN_CODE;
import static com.github.randoapp.Constants.GOOGLE_EMAIL_PARAM;
import static com.github.randoapp.Constants.GOOGLE_FAMILY_NAME_PARAM;
import static com.github.randoapp.Constants.GOOGLE_TOKEN_PARAM;
import static com.github.randoapp.Constants.GOOGLE_URL;
import static com.github.randoapp.Constants.IMAGE_PARAM;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOGOUT_URL;
import static com.github.randoapp.Constants.LOG_URL;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.RANDO_PARAM;
import static com.github.randoapp.Constants.REPORT_URL;
import static com.github.randoapp.Constants.SIGNUP_EMAIL_PARAM;
import static com.github.randoapp.Constants.SIGNUP_PASSWORD_PARAM;
import static com.github.randoapp.Constants.SIGNUP_URL;
import static com.github.randoapp.Constants.UNAUTHORIZED_CODE;
import static com.github.randoapp.Constants.UPLOAD_RANDO_URL;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_REQUEST_TOO_LONG;

public class API {

    public static void signup(String email, String password) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(SIGNUP_URL);
            addParamsToRequest(request, SIGNUP_EMAIL_PARAM, email, SIGNUP_PASSWORD_PARAM, password, FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId());
            response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void facebook(String id, String email, String token) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(FACEBOOK_URL);
            addParamsToRequest(request, FACEBOOK_ID_PARAM, id, FACEBOOK_EMAIL_PARAM, email, FACEBOOK_TOKEN_PARAM, token, FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId());
            response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void google(String email, String token, String familyName) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(GOOGLE_URL);
            addParamsToRequest(request, GOOGLE_EMAIL_PARAM, email, GOOGLE_TOKEN_PARAM, token, GOOGLE_FAMILY_NAME_PARAM, familyName, FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId());

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void anonymous(String uuid) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(ANONYMOUS_URL);
            addParamsToRequest(request, ANONYMOUS_ID_PARAM, uuid, FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId());

            response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void logout() throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(LOGOUT_URL);
            addAuthTokenHeader(request);
            addFirebaseInstanceIdHeader(request);

             response = VolleySingleton.getInstance().getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void fetchUserAsync(final OnFetchUser listener) {
        Log.i(API.class, "API.fetchUser");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getUrl(FETCH_USER_URL), null, new UserFetchResultListener(listener), new ErrorResponseListener());
        VolleySingleton.getInstance().getRequestQueue().add(request);
    }

    public static void syncUserAsync(final Response.Listener<JSONObject> syncListener) {
        Log.d(API.class, "API.syncUserAsync");
        BackgroundPreprocessRequest request = new BackgroundPreprocessRequest(Request.Method.GET, FETCH_USER_URL, null, new UserFetchResultListener(new OnFetchUser() {
            @Override
            public void onFetch(User user) {
                Log.d(API.class, "Fetched ", user.toString(), " user. and procesing it in background thread.");
                List<Rando> dbRandos = RandoDAO.getAllRandos();
                if (user.randosIn.size() + user.randosOut.size() != dbRandos.size()
                        || !(dbRandos.containsAll(user.randosIn)
                        && dbRandos.containsAll(user.randosOut))) {
                    RandoDAO.clearRandos();
                    RandoDAO.insertRandos(user.randosIn);
                    RandoDAO.insertRandos(user.randosOut);
                    //TODO: change 0 to real number
                    Notification.sendSyncNotification(1);
                }
            }
        }), syncListener, new ErrorResponseListener());

        request.addHeader(AUTHORIZATION_HEADER, "Token "+Preferences.getAuthToken());
        if (!Preferences.getFirebaseInstanceId().isEmpty()) {
            request.addHeader(FIREBASE_INSTANCE_ID_HEADER, Preferences.getFirebaseInstanceId());
        }
        VolleySingleton.getInstance().getRequestQueue().add(request);
    }

    public static Rando uploadImage(File randoFile, Location location) throws Exception {
        Log.i(API.class, "uploadImage");
        HttpResponse response = null;
        try {
            String latitude = "0.0";
            String longitude = "0.0";
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            HttpPost request = new HttpPost(UPLOAD_RANDO_URL);
            addAuthTokenHeader(request);
            addFirebaseInstanceIdHeader(request);

            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(IMAGE_PARAM, new FileBody(randoFile));
            multipartEntity.addTextBody(LATITUDE_PARAM, latitude);
            multipartEntity.addTextBody(LONGITUDE_PARAM, longitude);
            request.setEntity(multipartEntity.build());

            response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                JSONObject json = readJSON(response);
                String randoJson = json.getString(RANDO_PARAM);
                Rando rando = null;
                if (randoJson != null) {
                    rando = RandoUtil.parseRando(randoJson, Rando.Status.OUT);
                }
                return rando;
            } else if (response.getStatusLine().getStatusCode() == SC_REQUEST_TOO_LONG) {
                throw new RequestTooLongException(App.context.getResources().getString(R.string.error_image_too_big));
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (SocketTimeoutException e) {
            Log.w(API.class, "No response from server. Timeout exception.");
            throw e;
        } catch (ConnectionPoolTimeoutException e) {
            Log.w(API.class, "Connection manager fails to obtain a free connection from the connection pool within the given period of time");
            throw e;
        } catch (ConnectTimeoutException e) {
            Log.w(API.class, "Unable to establish a connection with the server");
            throw e;
        } catch (FileNotFoundException e) {
            Log.w(API.class, "File to upload not found");
            throw new FileNotFoundException("Image to upload not found");
        } catch (NoHttpResponseException e) {
            Log.w(API.class, "Unable to establish a connection with the server");
            throw e;
        } catch (RedirectException e) {
            Log.w(API.class, "HTTP specification caused by an invalid redirect response");
            throw e;
        } catch (IOException e) {
            Log.w(API.class, "Unknown exception ", e.getMessage());
            throw e;
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void report(String id) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(getUrl(REPORT_URL + id));

            response = VolleySingleton.getInstance().getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw processServerError(readJSON(response));
            }
        } catch (UnsupportedEncodingException e) {
            throw processError(e);
        } catch (ClientProtocolException e) {
            throw processError(e);
        } catch (IOException e) {
            throw processError(e);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public static void uploadLog(String logs) throws Exception {
        Log.i(API.class, "uploadLog");
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(LOG_URL);
            addAuthTokenHeader(request);
            addFirebaseInstanceIdHeader(request);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(logs, "UTF-8"));
            response = VolleySingleton.getInstance().getHttpClient().execute(request);
        } catch (IOException e) {
            Log.e(API.class, "Can not upload logs, because: ", e.getMessage());
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private static void addParamsToRequest(HttpPost request, String... args) throws UnsupportedEncodingException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        for (int i = 0; i < args.length; i += 2) {
            nameValuePairs.add(new BasicNameValuePair(args[i], args[i + 1]));
        }
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    }

    private static JSONObject readJSON(HttpResponse response) throws Exception {
        try {
            String line = "";
            StringBuilder json = new StringBuilder();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            while ((line = buffer.readLine()) != null) {
                json.append(line);
            }

            JSONObject jsonObject = new JSONObject(json.toString());
            return jsonObject;
        } catch (JSONException e) {
            throw processError(e);
        } catch (IOException e) {
            throw processError(e);
        }
    }

    private static String getUrl(String url) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("/");
        urlBuilder.append(Preferences.getAuthToken());
        return urlBuilder.toString();
    }

    private static void addAuthTokenHeader(HttpPost request){
        request.setHeader(AUTHORIZATION_HEADER, "Token " + Preferences.getAuthToken());
    }

    private static void addFirebaseInstanceIdHeader(HttpPost request){
        if (!Preferences.getFirebaseInstanceId().isEmpty()) {
            request.setHeader(FIREBASE_INSTANCE_ID_HEADER, Preferences.getFirebaseInstanceId());
        }
    }

    private static Exception processServerError(JSONObject json) {
        try {
            switch (json.getInt(ERROR_CODE_PARAM)) {
                case UNAUTHORIZED_CODE:
                    return new AuthenticationException(App.context.getResources().getString(R.string.error_400));
                case FORBIDDEN_CODE: {
                    String resetTime = "";
                    try {
                        String message = json.getString("message");
                        Matcher matcher = Pattern.compile("\\d+").matcher(message);
                        if (matcher.find()) {
                            long resetTimeUnix = Long.parseLong(matcher.group());
                            resetTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date(resetTimeUnix));
                        }
                    } catch (Exception e) {
                        Log.w(API.class, "Error when try build ban message for user: ", e.getMessage());
                    }
                    return new ForbiddenException(App.context.getResources().getString(R.string.error_411) + " " + resetTime);
                }
            }
            //TODO: implement all code handling in switch and replace server "message" with default value.
            return new Exception(json.getString("message"));
        } catch (JSONException exc) {
            return processError(exc);
        }
    }


    private static Exception processError(Exception exc) {
        //We don't want to log Connectivity exceptions
        if (exc instanceof UnknownHostException
                || exc instanceof ConnectException) {
            return new Exception(App.context.getResources().getString(R.string.error_no_network));
        }
        Log.e(API.class, "processError method", exc);
        return new Exception(App.context.getResources().getString(R.string.error_unknown_err));
    }

}
