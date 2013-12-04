package com.eucsoft.foodex.api;

import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import static com.eucsoft.foodex.Constants.*;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.preferences.Preferences;
import com.eucsoft.foodex.task.LogoutTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import static org.apache.http.HttpStatus.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class API {

    public static HttpClient client = new DefaultHttpClient();

    static {
        try {
            String cookieValue = Preferences.getSessionCookieValue();
            if (!"".equals(cookieValue)) {
                BasicClientCookie cookie = new BasicClientCookie(SEESSION_COOKIE_NAME, cookieValue);
                cookie.setDomain(Preferences.getSessionCookieDomain());
                cookie.setPath(Preferences.getSessionCookiePath());
                ((DefaultHttpClient) client).getCookieStore().addCookie(cookie);
            }
        } catch (Exception e) {
            //Why is the world so cruel?
        }
    }

    public static void signup(String email, String password) throws Exception {
        try {
            HttpPost request = new HttpPost(SIGNUP_URL);
            addParamsToRequest(request, SIGNUP_EMAIL_PARAM, email, SIGNUP_PASSWORD_PARAM, password);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
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
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
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

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void logout() throws Exception {
        try {
            HttpPost request = new HttpPost(LOGOUT_URL);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static List<FoodPair> fetchUser() throws Exception {
        try {
            HttpGet request = new HttpGet(FETCH_USER_URL);
            HttpResponse response = client.execute(request);


            if (response.getStatusLine().getStatusCode() == SC_OK) {
                JSONObject json = readJSON(response);
                JSONArray jsonFoods = json.getJSONArray(FOODS_PARAM);

                List<FoodPair> foods = new ArrayList<FoodPair>(jsonFoods.length());

                for (int i = 0; i < jsonFoods.length(); i++) {
                    FoodPair food = new FoodPair();
                    JSONObject jsonFood = jsonFoods.getJSONObject(i);
                    JSONObject user = jsonFood.getJSONObject(USER_PARAM);
                    JSONObject stranger = jsonFood.getJSONObject(STRANGER_PARAM);
                    food.user.foodId = user.getString(FOOD_ID_PARAM);
                    food.user.foodURL = user.getString(FOOD_URL_PARAM);
                    food.user.mapURL = user.getString(MAP_URL_PARAM);
                    food.user.bonAppetit = user.getInt(BON_APPETIT_PARAM);
                    food.user.foodDate = new Date(user.getLong(CREATION_PARAM));

                    food.stranger.foodId = stranger.getString(FOOD_ID_PARAM);
                    food.stranger.foodURL = stranger.getString(FOOD_URL_PARAM);
                    food.stranger.mapURL = stranger.getString(MAP_URL_PARAM);
                    food.stranger.bonAppetit = stranger.getInt(BON_APPETIT_PARAM);

                    foods.add(food);
                }
                return foods;
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static byte[] downloadFood(String url) throws Exception {
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toByteArray(entity);
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static FoodPair uploadFood(File foodFile, Location location) throws Exception {
        try {
            String latitude = "0.0";
            String longitude = "0.0";
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            HttpPost request = new HttpPost(ULOAD_FOOD_URL);

            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(IMAGE_PARAM, new FileBody(foodFile));
            multipartEntity.addTextBody(LATITUDE_PARAM, latitude);
            multipartEntity.addTextBody(LONGITUDE_PARAM, longitude);
            request.setEntity(multipartEntity.build());

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == SC_OK) {
                JSONObject json = readJSON(response);
                FoodPair foodPair = new FoodPair();
                foodPair.user.foodURL = json.getString(FOOD_URL_PARAM);
                foodPair.user.foodDate = new Date(json.getLong(CREATION_PARAM));
                return foodPair;
            } else {
                throw processServerError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void report(String id) throws Exception {
        try {
            HttpPost request = new HttpPost(REPORT_URL + id);

            HttpResponse response = client.execute(request);
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

    public static void bonAppetit(String id) throws Exception {
        try {
            HttpPost request = new HttpPost(BON_APPETIT_URL + id);
            HttpResponse response = client.execute(request);

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

    private static void storeSession(CookieStore cookieStore) {
        for (Cookie cookie : cookieStore.getCookies()) {
            if (SEESSION_COOKIE_NAME.equals(cookie.getName())) {
                Preferences.setSessionCookie(cookie.getValue(), cookie.getDomain(), cookie.getPath());
                return;
            }
        }
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

    private static Exception processServerError(JSONObject json) {
        try {
            switch (json.getInt(ERROR_CODE_PARAM)) {
                case UNAUTHORIZED_CODE:
                    new LogoutTask(new TaskResultListener() {
                        @Override
                        public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                            FragmentManager fragmentManager = ((ActionBarActivity) MainActivity.activity).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                        }}).execute();
                    return new Exception(MainActivity.context.getResources().getString(R.string.error_400));
            }
            //TODO: implement all code handling in switch and replace server "message" with default value.
            return new Exception(json.getString("message"));
        } catch (JSONException exc) {
            return processError(exc);
        }
    }

    private static Exception processError(Exception exc) {
        Log.e(API.class, "error", exc.getStackTrace().toString());
        return new Exception(MainActivity.context.getResources().getString(R.string.error_unknown_err));
    }

}