package com.github.randoapp.test.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.message.BasicHttpResponse;
import cz.msebera.android.httpclient.message.BasicStatusLine;

public class HttpStackMock implements HttpStack {
    private static final String DEFAULT_STRING_RESPONSE = "Hello";
    private static final String DEFAULT_JSON_RESPONSE = " {\"a\":1,\"b\":2,\"c\":3}";
    private static final String LOGGER_TAG = "STACK_OVER_FLOW";

    private static final int SIMULATED_DELAY_MS = 500;
    private final Context context;
    private final String response;

    HttpStackMock(Context context, String response) {
        this.response = response;
        this.context = context;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> stringStringMap)
            throws IOException, AuthFailureError {
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
        HttpResponse response
                = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        List<Header> headers = defaultHeaders();
        response.setHeaders(headers.toArray(new Header[0]));
        response.setLocale(Locale.JAPAN);
        response.setEntity(createEntity(request));
        return response;
    }

    private List<Header> defaultHeaders() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd mmm yyyy HH:mm:ss zzz");
        return Lists.<Header>newArrayList(
                new BasicHeader("Date", dateFormat.format(new Date())),
                new BasicHeader("Server",
                        /* Data below is header info of my server */
                        "Apache/1.3.42 (Unix) mod_ssl/2.8.31 OpenSSL/0.9.8e")
        );
    }

    /**
     * returns the fake content using resource file in res/raw. fake_res_foo.txt is used for
     * request to http://example.com/foo
     */
    private HttpEntity createEntity(Request request) throws UnsupportedEncodingException {
        String resourceName = constructFakeResponseFileName();
        int resourceId = context.getResources().getIdentifier(
                resourceName, "raw", context.getApplicationContext().getPackageName());
        if (resourceId == 0) {
            Log.w(LOGGER_TAG, "No fake file named " + resourceName
                    + " found. default fake response should be used.");
        } else {
            InputStream stream = context.getResources().openRawResource(resourceId);
            try {
                String string = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
                return new StringEntity(string);
            } catch (IOException e) {
                Log.e(LOGGER_TAG, "error reading " + resourceName, e);
            }
        }

        // Return default value since no fake file exists for given URL.
        if (request instanceof StringRequest) {
            return new StringEntity(DEFAULT_STRING_RESPONSE);
        }
        return new StringEntity(DEFAULT_JSON_RESPONSE);
    }

    /**
     * Map request URL to fake file name
     */
    private String constructFakeResponseFileName() {
        return response;
    }
}