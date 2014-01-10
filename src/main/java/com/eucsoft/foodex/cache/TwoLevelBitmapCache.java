package com.eucsoft.foodex.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;

public class TwoLevelBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    DiskLruImageCache diskLruImageCache;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB


    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 10;
        return cacheSize;
    }

    public TwoLevelBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public TwoLevelBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
        diskLruImageCache = new DiskLruImageCache(App.context, Constants.CACHE_FOLDER, DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 70);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key = String.valueOf(url.hashCode());
        Bitmap result = get(key);
        if (result == null) {
            result = diskLruImageCache.getBitmap(key);
        }
        return result;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        /*url = Utils.md5(url);*/
        String key = String.valueOf(url.hashCode());
        put(key, bitmap);

        if (diskLruImageCache.getBitmap(key) == null) {
            diskLruImageCache.put(key, bitmap);
        }
    }

}
