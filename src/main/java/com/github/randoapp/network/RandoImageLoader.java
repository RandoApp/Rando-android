package com.github.randoapp.network;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public class RandoImageLoader extends ImageLoader {

    /**
     * Constructs a new ImageLoader.
     *
     * @param queue      The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public RandoImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
    }

    @Override
    public ImageContainer get(String requestUrl, ImageListener imageListener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        ImageContainer imageContainer = super.get(requestUrl, imageListener, maxWidth, maxHeight, scaleType);

        return imageContainer;
    }

    @Override
    protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, String cacheKey) {
        return super.makeImageRequest(requestUrl, maxWidth, maxHeight, scaleType, cacheKey);
    }
}
