package com.eucsoft.foodex.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.util.List;

public class API {

    public static HttpClient client = new DefaultHttpClient();

    static {
        try {
            SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE);
            BasicClientCookie cookie = new BasicClientCookie(Constants.SEESSION_COOKIE_NAME, sharedPref.getString(Constants.SEESSION_COOKIE_NAME, ""));
            ((DefaultHttpClient) client).getCookieStore().addCookie(cookie);
        } catch (Exception e) {
            //Why is the world so cruel?
        }
    }

    public static void signup(String email, String password) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.SIGNUP_URL);
            addParamsToRequest(request, Constants.SIGNUP_EMAIL_PARAM, email, Constants.SIGNUP_PASSWORD_PARAM, password);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void facebook(String id, String email, String token) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.FACEBOOK_URL);
            addParamsToRequest(request, Constants.FACEBOOK_ID_PARAM, id, Constants.FACEBOOK_EMAIL_PARAM, email, Constants.FACEBOOK_TOKEN_PARAM, token);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void anonymous(String uuid) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.ANONYMOUS_URL);
            addParamsToRequest(request, Constants.ANONYMOUS_ID_PARAM, uuid);

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                storeSession(((DefaultHttpClient) client).getCookieStore());
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void logout() throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.LOGOUT_URL);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static List<FoodPair> fetchUser() throws Exception {
        try {
            HttpGet request = new HttpGet(Constants.FETCH_USER_URL);
            HttpResponse response = client.execute(request);


            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject json = readJSON(response);
                JSONArray jsonFoods = json.getJSONArray(Constants.FOODS_PARAM);

                List<FoodPair> foods = new ArrayList<FoodPair>(jsonFoods.length());

                for (int i = 0; i < jsonFoods.length(); i++) {
                    FoodPair food = new FoodPair();
                    JSONObject jsonFood = jsonFoods.getJSONObject(i);
                    JSONObject user = jsonFood.getJSONObject(Constants.USER_PARAM);
                    JSONObject stranger = jsonFood.getJSONObject(Constants.STRANGER_PARAM);
                    food.user.foodId = user.getString(Constants.FOOD_ID_PARAM);
                    food.user.foodURL = user.getString(Constants.FOOD_URL_PARAM);
                    food.user.mapURL = user.getString(Constants.MAP_URL_PARAM);
                    food.user.bonAppetit = user.getInt(Constants.BON_APPETIT_PARAM);
                    food.user.foodDate = new Date(user.getLong(Constants.CREATION_PARAM));

                    food.stranger.foodId = stranger.getString(Constants.FOOD_ID_PARAM);
                    food.stranger.foodURL = stranger.getString(Constants.FOOD_URL_PARAM);
                    food.stranger.mapURL = stranger.getString(Constants.MAP_URL_PARAM);
                    food.stranger.bonAppetit = stranger.getInt(Constants.BON_APPETIT_PARAM);

                    foods.add(food);
                }
                return foods;
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static byte[] downloadFood(String url) throws Exception {
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toByteArray(entity);
            } else {
                throw processError(readJSON(response));
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

            HttpPost request = new HttpPost(Constants.ULOAD_FOOD_URL);

            MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
            multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart(Constants.IMAGE_PARAM, new FileBody(foodFile));
            multipartEntity.addTextBody(Constants.LATITUDE_PARAM, latitude);
            multipartEntity.addTextBody(Constants.LONGITUDE_PARAM, longitude);
            request.setEntity(multipartEntity.build());

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject json = readJSON(response);
                FoodPair foodPair = new FoodPair();
                foodPair.user.foodURL = json.getString(Constants.FOOD_URL_PARAM);
                foodPair.user.foodDate = new Date(json.getLong(Constants.CREATION_PARAM));
                return foodPair;
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void report(String id) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.REPORT_URL + id);

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(readJSON(response));
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
            HttpPost request = new HttpPost(Constants.BON_APPETIT_URL + id);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(readJSON(response));
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
        SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().putString(Constants.SEESSION_COOKIE_NAME, cookieStore.getCookies().get(0).getValue()).commit();
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

    private static Exception processError(Object json) {
        if (json instanceof JSONObject) {
            try {
                return new Exception(((JSONObject) json).getString(Constants.ERROR_MESSAGE_PARAM));
            } catch (JSONException e) {
            }
        }
        return new Exception(MainActivity.context.getResources().getString(R.string.error_unknown_err));
    }

}