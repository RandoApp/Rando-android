package com.github.randoapp.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import static com.github.randoapp.Constants.NUMBER_OF_IMAGES_FOR_CACHING;

public class LruMemCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / NUMBER_OF_IMAGES_FOR_CACHING;

        return cacheSize;
    }

    public LruMemCache() {
        this(getDefaultLruCacheSize());
    }

    public LruMemCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}

