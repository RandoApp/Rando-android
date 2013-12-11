package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.eucsoft.foodex.service.SyncService;

import java.io.File;

import uk.co.senab.bitmapcache.BitmapLruCache;

public class App extends Application {
    private BitmapLruCache mCache;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        initBitmapCache();
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

    public BitmapLruCache getBitmapCache() {
        return mCache;
    }

    public static App getInstance(Context context) {
        return (App) context.getApplicationContext();
    }

    private void initBitmapCache() {
        File cacheDir = new File(getCacheDir(), "foodex");
        cacheDir.mkdirs();

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(getApplicationContext());
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheDir);

        mCache = builder.build();
    }

    public void startSyncService() {
        Intent syncService = new Intent(getApplicationContext(), SyncService.class);
        getApplicationContext().startService(syncService);
    }

}