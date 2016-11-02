package com.github.randoapp.network;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.github.randoapp.App;
import com.github.randoapp.cache.LruMemCache;

import java.io.File;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;

import static com.github.randoapp.Constants.UPLOAD_CONNECTION_TIMEOUT;
import static com.github.randoapp.Constants.DEFAULT_CACHE_DIR;
import static com.github.randoapp.Constants.DEFAULT_CACHE_SIZE;
import static com.github.randoapp.Constants.ESTABLISH_CONNECTION_TIMEOUT;

public class VolleySingleton {

    private static VolleySingleton instance = null;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    public HttpClient httpClient;

    private VolleySingleton() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, ESTABLISH_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, UPLOAD_CONNECTION_TIMEOUT);
        HttpProtocolParams.setUseExpectContinue(httpParams, false);

        httpClient = new DefaultHttpClient(httpParams);
        ClientConnectionManager mgr = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
                mgr.getSchemeRegistry()), params);
        requestQueue = createRequestQueue();
        imageLoader = new ImageLoader(this.requestQueue, new LruMemCache());
    }

    public static VolleySingleton getInstance() {
        if (instance == null) {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private RequestQueue createRequestQueue() {
        File cacheDir = new File(App.context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, DEFAULT_CACHE_SIZE), network);
        queue.start();
        return queue;
    }

}
