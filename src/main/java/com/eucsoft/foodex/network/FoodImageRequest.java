package com.eucsoft.foodex.network;

import android.graphics.Bitmap;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

public class FoodImageRequest extends ImageRequest {

    public Priority foodPriority;

    public FoodImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, Response.ErrorListener errorListener, Priority priority) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
        this.foodPriority = priority;
    }

    @Override
    public Priority getPriority() {
        return foodPriority;
    }
}
