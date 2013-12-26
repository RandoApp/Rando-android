package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.eucsoft.foodex.service.SyncService;

public class App extends Application {
    private ImageLoader imageLoader;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        startService();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void startService() {
        //App onCreate called twice. Prevent double service run, if it is already created
        if (!SyncService.isRunning()) {
            SyncService.run();
        }
    }

}
