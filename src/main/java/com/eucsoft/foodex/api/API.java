package com.eucsoft.foodex.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.db.model.Food;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class API {

    public static HttpClient client = new DefaultHttpClient();

    static {
        SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE);
        BasicClientCookie cookie = new BasicClientCookie(Constants.SEESSION_COOKIE_NAME, sharedPref.getString(Constants.SEESSION_COOKIE_NAME, ""));
        ((DefaultHttpClient) client).getCookieStore().addCookie(cookie);
    }

    public static void signup(String email, String password) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.SIGNUP_URL);
            addParamsToRequest(request, Constants.SIGNUP_EMAIL_PARAM, email, Constants.SIGNUP_PASSWORD_PARAM, password);
            HttpResponse response = client.execute(request);
            JSONObject jsonObject = readJSON(response);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(jsonObject);
            }

            storeSession(((DefaultHttpClient) client).getCookieStore());
        } catch (IOException e) {
            //TODO: handle exception
        }
    }

    public static List<Food> fetchUser() throws Exception {
        try {
            HttpGet request = new HttpGet(Constants.FETCH_USER_URL);
            HttpResponse response = client.execute(request);
            JSONObject json = readJSON(response);
            if (response.getStatusLine().getStatusCode() == 200) {
                //TODO: Convert JSONOBject to Food List
                return null;
            } else {
                throw processError(json);
            }
        } catch (IOException e) {
            //TODO: handle exception
        }
        return null;
    }

    public static byte[] downloadFood(String url) throws Exception {
        try {
            HttpGet request = new HttpGet(Constants.DOWNLOAD_FOOD_URL + url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toByteArray(entity);
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            //TODO: handle exception
        }
        return new byte[0];
    }

    public static Food uploadFood(File foodFile) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.ULOAD_FOOD_URL);
            FileEntity fileEntity = new FileEntity(foodFile, Constants.IMAGE_MIME_TYPE);
            request.setEntity(fileEntity);

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject json = readJSON(response);
                Food food = new Food();
                food.setUserPhotoURL(json.getString("foodUrl"));
                return food;
            } else {
                throw processError(readJSON(response));
            }
        } catch (IOException e) {
            throw processError(e);
        }
    }

    public static void report(String id) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.REPORT_URL);
            addParamsToRequest(request, Constants.FOOD_ID_PARAM, id);

            HttpResponse response = client.execute(request);
            JSONObject json = readJSON(response);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(json);
            }
        } catch (UnsupportedEncodingException e) {
            //TODO: handle exception
        } catch (ClientProtocolException e) {
            //TODO: handle exception
        } catch (IOException e) {
            //TODO: handle exception
        }
    }

    public static void bonAppetit(String id) throws Exception {
        try {
            HttpPost request = new HttpPost(Constants.REPORT_URL);
            addParamsToRequest(request, Constants.BON_APPETIT_URL, id);
            HttpResponse response = client.execute(request);
            JSONObject json = readJSON(response);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw processError(json);
            }
        } catch (UnsupportedEncodingException e) {
            //TODO: handle exception
        } catch (ClientProtocolException e) {
            //TODO: handle exception
        } catch (IOException e) {
            //TODO: handle exception
        }
    }

    private static void addParamsToRequest(HttpPost request, String... args) throws UnsupportedEncodingException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        for (int i = 0; i < args.length; i+=2) {
            nameValuePairs.add(new BasicNameValuePair(args[i], args[i+1]));
        }
        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    }

    private static void storeSession(CookieStore cookieStore) {
        SharedPreferences sharedPref = MainActivity.context.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().putString(Constants.SEESSION_COOKIE_NAME, cookieStore.getCookies().get(0).getValue());
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
            return new Exception("NOT IMPLEMENTED");
        }
        if (json instanceof Exception) {
            return new Exception("NOT IMPLEMENTED");
        }
        return new Exception("NOT IMPLEMENTED");
    }

}
