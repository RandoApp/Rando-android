package com.github.randoapp.network;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;


public class RandoImageRequset extends ImageRequest {

    private Priority priority;

    public RandoImageRequset(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener, Priority priority) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
        this.priority = priority;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }
}
