package com.github.randoapp.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.api.listeners.ErrorResponseListener;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.api.listeners.UploadRandoListener;
import com.github.randoapp.api.listeners.UserFetchResultListener;
import com.github.randoapp.api.request.BackgroundPreprocessedRequest;
import com.github.randoapp.api.request.VolleyMultipartRequest;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.notification.Notification;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.randoapp.Constants.ANONYMOUS_ID_PARAM;
import static com.github.randoapp.Constants.ANONYMOUS_URL;
import static com.github.randoapp.Constants.API_CONNECTION_TIMEOUT;
import static com.github.randoapp.Constants.AUTHORIZATION_HEADER;
import static com.github.randoapp.Constants.DELETE_URL;
import static com.github.randoapp.Constants.ERROR_CODE_PARAM;
import static com.github.randoapp.Constants.FETCH_USER_URL;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID_HEADER;
import static com.github.randoapp.Constants.FIREBASE_INSTANCE_ID_PARAM;
import static com.github.randoapp.Constants.GOOGLE_EMAIL_PARAM;
import static com.github.randoapp.Constants.GOOGLE_TOKEN_PARAM;
import static com.github.randoapp.Constants.GOOGLE_URL;
import static com.github.randoapp.Constants.IMAGE_MIME_TYPE;
import static com.github.randoapp.Constants.IMAGE_PARAM;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOGOUT_URL;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.NOT_UPDATED;
import static com.github.randoapp.Constants.RATE_URL;
import static com.github.randoapp.Constants.REPORT_URL;
import static com.github.randoapp.Constants.SIGNUP_EMAIL_PARAM;
import static com.github.randoapp.Constants.SIGNUP_PASSWORD_PARAM;
import static com.github.randoapp.Constants.SIGNUP_URL;
import static com.github.randoapp.Constants.UPDATED;
import static com.github.randoapp.Constants.UPLOAD_CONNECTION_TIMEOUT;
import static com.github.randoapp.Constants.UPLOAD_RANDO_URL;

public class API {

    protected static final String PROTOCOL_CHARSET = "utf-8";

    public static void signup(final String email, final String password, final Context context, final NetworkResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(SIGNUP_EMAIL_PARAM, email);
        params.put(SIGNUP_PASSWORD_PARAM, password);
        params.put(FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId(context));

        VolleySingleton.getInstance(context).getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String authToken = response.getString(Constants.AUTH_TOKEN_PARAM);
                    Preferences.setAuthToken(context, authToken);
                } catch (JSONException e) {
                    Log.e(API.class, "Parse signup response failed", e);
                }

                if (resultListener != null) {
                    resultListener.onOk();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        }));
    }

    public static void google(String email, String token, final Context context, final NetworkResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(GOOGLE_EMAIL_PARAM, email);
        params.put(GOOGLE_TOKEN_PARAM, token);
        params.put(FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId(context));

        VolleySingleton.getInstance(context).getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, GOOGLE_URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String authToken = response.getString(Constants.AUTH_TOKEN_PARAM);
                    Preferences.setAuthToken(context, authToken);
                } catch (JSONException e) {
                    Log.e(API.class, "Parse google login response failed", e);
                }
                if (resultListener != null) {
                    resultListener.onOk();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        }));
    }

    public static void anonymous(String uuid, final Context context, final NetworkResultListener resultListener) {
        Map<String, String> params = new HashMap<>();
        params.put(ANONYMOUS_ID_PARAM, uuid);
        params.put(FIREBASE_INSTANCE_ID_PARAM, Preferences.getFirebaseInstanceId(context));

        VolleySingleton.getInstance(context).getRequestQueue().add(new JsonObjectRequest(Request.Method.POST, ANONYMOUS_URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String authToken = response.getString(Constants.AUTH_TOKEN_PARAM);
                    Preferences.setAuthToken(context, authToken);
                } catch (JSONException e) {
                    Log.e(API.class, "Parse anonymous login response failed", e);
                }
                if (resultListener != null) {
                    resultListener.onOk();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        }));
    }

    public static void logout(final Context context, final NetworkResultListener resultListener) {
        BackgroundPreprocessedRequest request = new BackgroundPreprocessedRequest(Request.Method.POST, LOGOUT_URL, null, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (resultListener != null) {
                    resultListener.onOk();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        });

        request.setHeaders(getHeaders(context));
        request.setRetryPolicy(new DefaultRetryPolicy(API_CONNECTION_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void syncUserAsync(final Context context, final Response.Listener<JSONObject> syncListener, ErrorResponseListener errorResponseListener) {
        Log.d(API.class, "API.syncUserAsync");
        BackgroundPreprocessedRequest request = new BackgroundPreprocessedRequest(Request.Method.GET, FETCH_USER_URL, null, new UserFetchResultListener(context, new OnFetchUser() {
            @Override
            public void onFetch(User user) {
                Log.d(API.class, "Fetched ", user.toString(), " user. and procesing it in background thread.");
                List<Rando> dbRandos = RandoDAO.getAllRandos(context);
                int totalUserRandos = user.randosIn.size() + user.randosOut.size();
                if (totalUserRandos != dbRandos.size()
                        || !(dbRandos.containsAll(user.randosIn)
                        && dbRandos.containsAll(user.randosOut))) {
                    RandoDAO.clearRandos(context);
                    RandoDAO.insertRandos(context, user.randosIn);
                    RandoDAO.insertRandos(context, user.randosOut);
                    Notification.sendSyncNotification(context, totalUserRandos, UPDATED);
                } else {
                    Notification.sendSyncNotification(context, totalUserRandos, NOT_UPDATED);
                }
            }
        }), syncListener, errorResponseListener);
        request.setHeaders(getHeaders(context));

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void uploadImage(final RandoUpload randoUpload, final Context context, final UploadRandoListener uploadListener, final Response.ErrorListener errorListener) {

        VolleyMultipartRequest uploadMultipart = new VolleyMultipartRequest(UPLOAD_RANDO_URL, getHeaders(context), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.d(API.class, "Rando Uploaded Successfully:", randoUpload.toString());
                String resultResponse = new String(response.data);
                if (uploadListener != null) {
                    uploadListener.onUpload(Rando.fromJSON(resultResponse, Rando.Status.OUT));
                }
            }
        }, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>(2);
                params.put(LATITUDE_PARAM, randoUpload.latitude);
                params.put(LONGITUDE_PARAM, randoUpload.longitude);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>(1);
                File randoFile = new File(randoUpload.file);
                params.put(IMAGE_PARAM, new DataPart(randoFile.getName(), FileUtil.readFile(randoFile), IMAGE_MIME_TYPE));

                return params;
            }

            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        uploadMultipart.setRetryPolicy(new DefaultRetryPolicy(UPLOAD_CONNECTION_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).getRequestQueue().add(uploadMultipart);
    }

    public static void delete(final String randoId, final Context context, final NetworkResultListener resultListener) throws Exception {
        Log.d(API.class, "Deleting Rando:", randoId);
        BackgroundPreprocessedRequest request = new BackgroundPreprocessedRequest(Request.Method.POST, DELETE_URL + randoId, null, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("delete".equals(response.getString("command")) &&
                            "done".equals(response.getString("result"))) {
                        Log.d(API.class, "Deleted Rando:", randoId);
                        resultListener.onOk();
                    }
                } catch (JSONException e) {
                    Log.e(API.class, "Error Deleting Rando", e);
                    resultListener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(API.class, "Error Deleting Rando", error);
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        });

        request.setHeaders(getHeaders(context));
        request.setRetryPolicy(new DefaultRetryPolicy(API_CONNECTION_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void report(final String randoId, final Context context, final NetworkResultListener resultListener) throws Exception {
        Log.d(API.class, "Reporting Rando:", randoId);
        BackgroundPreprocessedRequest request = new BackgroundPreprocessedRequest(Request.Method.POST, REPORT_URL + randoId, null, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("report".equals(response.getString("command")) &&
                            "done".equals(response.getString("result"))) {
                        Log.d(API.class, "Reported Rando:", randoId);
                        resultListener.onOk();
                    }
                } catch (JSONException e) {
                    Log.e(API.class, "Error Reporting Rando", e);
                    resultListener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(API.class, "Error Reporting Rando", error);
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        });

        request.setHeaders(getHeaders(context));
        request.setRetryPolicy(new DefaultRetryPolicy(API_CONNECTION_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void rate(final String randoId, final Context context, final int rating, final NetworkResultListener resultListener) {
        Log.d(API.class, "Rate Rando:", randoId);
        BackgroundPreprocessedRequest request = new BackgroundPreprocessedRequest(Request.Method.POST, RATE_URL + randoId + "?rating=" + rating, null, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if ("rate".equals(response.getString("command")) &&
                            "done".equals(response.getString("result"))) {
                        Log.d(API.class, "Rated Rando:", randoId);
                        resultListener.onOk();
                    }
                } catch (JSONException e) {
                    Log.e(API.class, "Error Rating Rando", e);
                    resultListener.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(API.class, "Error Rating Rando", error);
                Response<JSONObject> resp = parseNetworkResponse(error.networkResponse);
                if (resultListener != null) {
                    resultListener.onError(processServerError(resp.result));
                }
            }
        });

        request.setHeaders(getHeaders(context));
        request.setRetryPolicy(new DefaultRetryPolicy(API_CONNECTION_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    private static Map<String, String> getHeaders(Context context) {
        Map<String, String> headers = new HashMap<>(2);
        headers.put(AUTHORIZATION_HEADER, "Token " + Preferences.getAuthToken(context));
        if (!Preferences.getFirebaseInstanceId(context).isEmpty()) {
            headers.put(FIREBASE_INSTANCE_ID_HEADER, Preferences.getFirebaseInstanceId(context));
        }
        return headers;
    }

    private static Error processServerError(JSONObject json) {
        if (json == null) {
            return new Error().setCode(R.string.error_no_network);
        }

        try {
            switch (json.getInt(ERROR_CODE_PARAM)) {
                case Constants.UNAUTHORIZED_CODE:
                    return new Error().setCode(R.string.error_400);
                case Constants.FORBIDDEN_CODE: {
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
                    return new Error().setCode(R.string.error_411).setMessage(resetTime);
                }
                default: {
                    //TODO: implement all code handling in switch and replace server "message" with default value.
                    return new Error().setMessage(json.getString("message"));
                }
            }
        } catch (JSONException exc) {
            return processError(exc);
        }
    }

    private static Error processError(Exception exc) {
        //We don't want to log Connectivity exceptions
        if (exc instanceof UnknownHostException
                || exc instanceof ConnectException) {
            return new Error().setCode(R.string.error_no_network);
        }
        Crashlytics.logException(exc);
        Log.e(API.class, "processError method", exc);
        return new Error().setCode(R.string.error_unknown_err);
    }

    public static Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if (response == null || response.data == null) {
            return Response.error(new ParseError(new Exception("Unknown error or no network")));
        }

        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}
