package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.eucsoft.foodex.service.SyncService;

import java.io.File;

public class App extends Application {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        startSyncService();
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
        context = getApplicationContext();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    public void startSyncService() {
        Intent syncService = new Intent(getApplicationContext(), SyncService.class);
        getApplicationContext().startService(syncService);
    }

}