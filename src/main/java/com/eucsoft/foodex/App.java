package com.eucsoft.foodex;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.eucsoft.foodex.service.SyncService;

import java.io.File;

import uk.co.senab.bitmapcache.BitmapLruCache;

public class App extends Application {
    private BitmapLruCache mCache;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        initBitmapCache();
        startSyncService();
        context = getApplicationContext();
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

    private void startSyncService() {
        Intent syncService = new Intent(getApplicationContext(), SyncService.class);
        getApplicationContext().startService(syncService);
    }

}