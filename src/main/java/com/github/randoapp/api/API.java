package com.github.randoapp.api;

import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.api.exception.ForbiddenException;
import com.github.randoapp.api.exception.RequestTooLongException;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.preferences.Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

import static com.github.randoapp.Constants.ANONYMOUS_ID_PARAM;
import static com.github.randoapp.Constants.ANONYMOUS_URL;
import static com.github.randoapp.Constants.CREATION_PARAM;
import static com.github.randoapp.Constants.ERROR_CODE_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_EMAIL_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_ID_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_TOKEN_PARAM;
import static com.github.randoapp.Constants.FACEBOOK_URL;
import static com.github.randoapp.Constants.FETCH_USER_URL;
import static com.github.randoapp.Constants.FORBIDDEN_CODE;
import static com.github.randoapp.Constants.GOOGLE_EMAIL_PARAM;
import static com.github.randoapp.Constants.GOOGLE_FAMILY_NAME_PARAM;
import static com.github.randoapp.Constants.GOOGLE_TOKEN_PARAM;
import static com.github.randoapp.Constants.GOOGLE_URL;
import static com.github.randoapp.Constants.IMAGE_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.LARGE_PARAM;
import static com.github.randoapp.Constants.LATITUDE_PARAM;
import static com.github.randoapp.Constants.LOGOUT_URL;
import static com.github.randoapp.Constants.LONGITUDE_PARAM;
import static com.github.randoapp.Constants.MAP_URL_PARAM;
import static com.github.randoapp.Constants.MAP_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.MEDIUM_PARAM;
import static com.github.randoapp.Constants.RANDOS_PARAM;
import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.REPORT_URL;
import static com.github.randoapp.Constants.SIGNUP_EMAIL_PARAM;
import static com.github.randoapp.Constants.SIGNUP_PASSWORD_PARAM;
import static com.github.randoapp.Constants.SIGNUP_URL;
import static com.github.randoapp.Constants.SMALL_PARAM;
import static com.github.randoapp.Constants.STRANGER_PARAM;
import static com.github.randoapp.Constants.UPLOAD_RANDO_URL;
import static com.github.randoapp.Constants.UNAUTHORIZED_CODE;
import static com.github.randoapp.Constants.USER_PARAM;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_REQUEST_TOO_LONG;

public class API {

    public static void signup(String email, String password) throws Exception {
        try {
            HttpPost request = new HttpPost(SIGNUP_URL);
            addParamsToRequest(request, SIGNUP_EMAIL_PARAM, email, SIGNUP_PASSWORD_PARAM, password);
            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void facebook(String id, String email, String token) throws Exception {
        try {
            HttpPost request = new HttpPost(FACEBOOK_URL);
            addParamsToRequest(request, FACEBOOK_ID_PARAM, id, FACEBOOK_EMAIL_PARAM, email, FACEBOOK_TOKEN_PARAM, token);
            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void google(String email, String token, String familyName) throws Exception {
        try {
            HttpPost request = new HttpPost(GOOGLE_URL);
            addParamsToRequest(request, GOOGLE_EMAIL_PARAM, email, GOOGLE_TOKEN_PARAM, token, GOOGLE_FAMILY_NAME_PARAM, familyName);
            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void anonymous(String uuid) throws Exception {
        try {
            HttpPost request = new HttpPost(ANONYMOUS_URL);
            addParamsToRequest(request, ANONYMOUS_ID_PARAM, uuid);

            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                String authToken = readJSON(response).getString(Constants.AUTH_TOKEN_PARAM);
                Preferences.setAuthToken(authToken);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void logout() throws AuthenticationException, Exception {
        try {
            HttpPost request = new HttpPost(getUrl(LOGOUT_URL));
            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void fetchUserAsync(final OnFetchUser listener) {
        Log.i(API.class, "API.fetchUser");

        VolleySingleton.getInstance().getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, getUrl(FETCH_USER_URL), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonRandos = response.getJSONArray(RANDOS_PARAM);
                    List<RandoPair> randos = new ArrayList<RandoPair>(jsonRandos.length());

                    for (int i = 0; i < jsonRandos.length(); i++) {
                        RandoPair rando = new RandoPair();
                        JSONObject jsonRando = jsonRandos.getJSONObject(i);
                        JSONObject user = jsonRando.getJSONObject(USER_PARAM);
                        JSONObject userRandoUrlSizes = user.getJSONObject(IMAGE_URL_SIZES_PARAM);
                        JSONObject userMapUrlSizes = user.getJSONObject(MAP_URL_SIZES_PARAM);

                        JSONObject stranger = jsonRando.getJSONObject(STRANGER_PARAM);
                        JSONObject strangerRandoUrlSizes = stranger.getJSONObject(IMAGE_URL_SIZES_PARAM);
                        JSONObject strangerMapUrlSizes = stranger.getJSONObject(MAP_URL_SIZES_PARAM);

                        rando.user.randoId = user.getString(RANDO_ID_PARAM);
                        rando.user.imageURL = user.getString(IMAGE_URL_PARAM);
                        rando.user.imageURLSize.small = userRandoUrlSizes.getString(SMALL_PARAM);
                        rando.user.imageURLSize.medium = userRandoUrlSizes.getString(MEDIUM_PARAM);
                        rando.user.imageURLSize.large = userRandoUrlSizes.getString(LARGE_PARAM);

                        rando.user.mapURL = user.getString(MAP_URL_PARAM);
                        rando.user.mapURLSize.small = userMapUrlSizes.getString(SMALL_PARAM);
                        rando.user.mapURLSize.medium = userMapUrlSizes.getString(MEDIUM_PARAM);
                        rando.user.mapURLSize.large = userMapUrlSizes.getString(LARGE_PARAM);

                        rando.user.date = new Date(user.getLong(CREATION_PARAM));

                        rando.stranger.randoId = stranger.getString(RANDO_ID_PARAM);
                        rando.stranger.imageURL = stranger.getString(IMAGE_URL_PARAM);
                        rando.stranger.imageURLSize.small = strangerRandoUrlSizes.getString(SMALL_PARAM);
                        rando.stranger.imageURLSize.medium = strangerRandoUrlSizes.getString(MEDIUM_PARAM);
                        rando.stranger.imageURLSize.large = strangerRandoUrlSizes.getString(LARGE_PARAM);

                        rando.stranger.mapURL = stranger.getString(MAP_URL_PARAM);
                        rando.stranger.mapURLSize.small = strangerMapUrlSizes.getString(SMALL_PARAM);
                        rando.stranger.mapURLSize.medium = strangerMapUrlSizes.getString(MEDIUM_PARAM);
                        rando.stranger.mapURLSize.large = strangerMapUrlSizes.getString(LARGE_PARAM);

                        randos.add(rando);
                    }
                    listener.onFetch(randos);
                } catch (JSONException e) {
                    Log.e(API.class, "onResponse method", e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e(API.class, " onErrorResponse method", e);
            }
        }
        ));
    }

    public static List<RandoPair> fetchUser() throws AuthenticationException, Exception {
        try {
            HttpGet request = new HttpGet(getUrl(FETCH_USER_URL));
            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);


            if (response.getStatusLine().getStatusCode() == SC_OK) {
                JSONObject json = readJSON(response);
                JSONArray jsonRandos = json.getJSONArray(RANDOS_PARAM);

                List<RandoPair> randos = new ArrayList<RandoPair>(jsonRandos.length());

                for (int i = 0; i < jsonRandos.length(); i++) {
                    RandoPair rando = new RandoPair();
                    JSONObject jsonRando = jsonRandos.getJSONObject(i);
                    JSONObject user = jsonRando.getJSONObject(USER_PARAM);
                    JSONObject stranger = jsonRando.getJSONObject(STRANGER_PARAM);
                    rando.user.randoId = user.getString(RANDO_ID_PARAM);
                    rando.user.imageURL = user.getString(IMAGE_URL_PARAM);
                    rando.user.mapURL = user.getString(MAP_URL_PARAM);
                    rando.user.date = new Date(user.getLong(CREATION_PARAM));

                    rando.stranger.randoId = stranger.getString(RANDO_ID_PARAM);
                    rando.stranger.imageURL = stranger.getString(IMAGE_URL_PARAM);
                    rando.stranger.mapURL = stranger.getString(MAP_URL_PARAM);

                    randos.add(rando);
                }
                return randos;
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static RandoPair uploadImage(File randoFile, Location location) throws AuthenticationException, Exception {
        Log.i(API.class, "uploadImage");
        try {
            String latitude = "0.0";
            String longitude = "0.0";
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            HttpPost request = new HttpPost(getUrl(UPLOAD_RANDO_URL));

            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(IMAGE_PARAM, new FileBody(randoFile));
            multipartEntity.addTextBody(LATITUDE_PARAM, latitude);
            multipartEntity.addTextBody(LONGITUDE_PARAM, longitude);
            request.setEntity(multipartEntity.build());

            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                JSONObject json = readJSON(response);
                RandoPair randoPair = new RandoPair();
                randoPair.user.imageURL = json.getString(IMAGE_URL_PARAM);
                randoPair.user.date = new Date(json.getLong(CREATION_PARAM));
                return randoPair;
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
        }
    }

    public static void report(String id) throws AuthenticationException, Exception {
        try {
            HttpPost request = new HttpPost(getUrl(REPORT_URL + id));

            HttpResponse response = VolleySingleton.getInstance().getHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw processServerError(readJSON(response));
            }
        } catch (UnsupportedEncodingException e) {
            throw processError(e);
        } catch (ClientProtocolException e) {
            throw processError(e);
        } catch (IOException e) {
            throw processError(e);
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
        if(exc instanceof UnknownHostException
                || exc instanceof ConnectException){
            return new Exception(App.context.getResources().getString(R.string.error_no_network));
        }
        Log.e(API.class, "processError method", exc);
        return new Exception(App.context.getResources().getString(R.string.error_unknown_err));
    }

}
