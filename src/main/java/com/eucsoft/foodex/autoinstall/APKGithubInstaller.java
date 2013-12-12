package com.eucsoft.foodex.autoinstall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.MainActivity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.eucsoft.foodex.Constants.PREFERENCES_FILE_NAME;
import static com.eucsoft.foodex.Constants.SEESSION_COOKIE_VALUE;
import static java.util.Collections.sort;

public class APKGithubInstaller extends AsyncTask<Void, Void, Void> {

    private static final String RELEASES_URL = "https://api.github.com/repos/USER_REPO/releases";
    private static final String DOWNLOAD_URL = "https://github.com/USER_REPO/releases/download/TAG_NAME/ASSEST_NAME";

    private static final String CURRENT_CREATION_KEY = "install-creation";

    private Long lastCheckUpdate = 0l;
    private static final long UPDATE_TIMER = 10 * 60 * 1000;

    private String userRepository;

    public APKGithubInstaller (String user, String repository) {
        if (user == null || repository == null) {
            throw new IllegalArgumentException("user and repository should exist");
        }

        this.userRepository = user + "/" + repository;
    }

    public APKGithubInstaller (String userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("user/repository should exist");
        }

        this.userRepository = userRepository;
    }


    //githubRepository example: dimhold/scripts
    public void update() {
        long time = new Date().getTime();
        if ((time - lastCheckUpdate) > UPDATE_TIMER) {
            lastCheckUpdate = time;
            execute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            updateIfNeed();
        } catch (Exception e) {
            Log.e("AutoInstallFromGithub", "Exception: " + e);
        }
        return null;
    }


    private void updateIfNeed() throws JSONException, ParseException, IOException {
        APK apk = getLastAPKFromGithub();
        if (apk == null) return;

        Long currentApkCreation = getCurrentVersion();
        if (apk.creation.getTime() > currentApkCreation) {
            update(apk);
        }
    }

    private void update(APK apk) throws IOException {
        File apkFile = downloadApk(apk);
        updateVersion(apk);
        installApk(apkFile);
    }

    private Long getCurrentVersion () {
        SharedPreferences pref = App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return  pref.getLong(CURRENT_CREATION_KEY, 0);
    }

    private APK getLastAPKFromGithub() throws IOException, JSONException, ParseException {
        String url = RELEASES_URL.replace("USER_REPO", userRepository);
        String line = "";
        StringBuilder json = new StringBuilder();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent()));
        while ((line = buffer.readLine()) != null) {
            json.append(line);
        }

        JSONArray jsonArray = new JSONArray(json.toString());
        List<APK> apks = JsonToAPKList(jsonArray);
        if (apks.size() > 0) {
            sort(apks);
            return apks.get(0);
        }
        return null;
    }

    private List<APK> JsonToAPKList(JSONArray json) throws JSONException, ParseException {
        List<APK> apks = new ArrayList<APK>();

        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                JSONObject release = (JSONObject) json.get(i);
                String tagName = release.getString("tag_name");
                JSONArray assets = release.getJSONArray("assets");

                for (int j = 0; j < assets.length(); j++) {
                    JSONObject assest = (JSONObject) assets.get(j);
                    Date creation = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ss'Z'").parse(assest.getString("created_at"));
                    String contentType = assest.getString("content_type");

                    if (APK.CONTENT_TYPE.equals(contentType)) {
                        APK apk = new APK(assest.getString("name"), tagName, creation);
                        apks.add(apk);
                    }
                }
            }
        }

        return apks;
    }

    private void updateVersion(APK apk) {
        SharedPreferences pref = App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putLong(SEESSION_COOKIE_VALUE, apk.creation.getTime()).commit();
    }

    private File downloadApk(APK apk) throws IOException {
        File apkFile = new File(App.context.getExternalCacheDir(), apk.getName());
        new DefaultHttpClient().execute(new HttpGet(apk.getUrl())).getEntity().writeTo(new FileOutputStream(apkFile));
        return apkFile;
    }

    private void installApk(File apk) {
        if (apk == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apk), APK.CONTENT_TYPE);
        App.context.startActivity(intent);

    }

    class APK implements Comparable {
        public static final String CONTENT_TYPE = "application/vnd.android.package-archive";
        private final Pattern pattern = Pattern.compile("(.*)(\\.[^.]+)", Pattern.DOTALL);

        public Date creation;
        private String tag;
        private String name;

        public APK(String name, String tagName, Date creation) {
            this.name = name;
            this.tag = tagName;
            this.creation = creation;
        }

        public String getName () {
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                String name = matcher.group(1);
                String extension = matcher.group(2);
                return name + "-" + tag + extension;
            }
            return name;
        }

        public String getUrl() {
            return DOWNLOAD_URL.replace("USRE_REPO", userRepository).replace("TAG_NAME", tag).replace("ASSEST_NAME", name);
        }

        @Override
        public int compareTo(Object another) {
            APK anotherApk = (APK) another;
            return anotherApk.creation.compareTo(creation);
        }
    }
}
