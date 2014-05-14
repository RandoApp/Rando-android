package com.github.randoapp.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.github.randoapp.App;
import com.github.randoapp.cache.LruMemCache;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import static com.github.randoapp.Constants.CONNECTION_TIMEOUT;

public class VolleySingleton {

    private static VolleySingleton instance = null;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    public HttpClient httpClient;

    private VolleySingleton() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
        
        httpClient = new DefaultHttpClient(httpParams);
        ClientConnectionManager mgr = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
                    mgr.getSchemeRegistry()), params);

        requestQueue = Volley.newRequestQueue(App.context, new HttpClientStack(httpClient));
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

}
