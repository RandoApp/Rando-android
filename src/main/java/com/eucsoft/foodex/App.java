package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.eucsoft.foodex.service.SyncService;

public class App extends Application {
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        startSyncService();

        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    public void startSyncService() {
        Intent syncService = new Intent(context, SyncService.class);
        context.startService(syncService);
    }

}
