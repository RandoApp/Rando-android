package com.eucsoft.foodex.test.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.api.API;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APITestHelper {

    public static void mockAPIWithError() throws IOException {
        MainActivity.context = mockContext();
        API.client = mockClient(500, "{'code': '501'," +
            "'message': 'Internal Server Error'," +
            "'description': 'See https://github.com/dimhold/foodex/wiki/Errors/#system'}");
    }

    public static void mockAPIForUploadFood() throws IOException {
        MainActivity.context = mockContext();
        API.client = mockClient(200, "{" +
            "'creation': '1383670800877'," +
            "'foodUrl': 'http://api.foodex.com/food/abcd/abcdadfwefwef.jpg'," +
            "'mapUrl': ''}");
    }

    public static void mockAPIForDownloadFood() throws IOException {
        MainActivity.context = mockContext();
        API.client = mockClient(200, "jpg file");
    }

    public static void mockAPIForFetchUser() throws IOException {
        MainActivity.context = mockContext();
        API.client = mockClient(200, "{'email': 'user@mail.com'," +
            "'foods': [" +
                "{" +
                    "'user': {" +
                        "'foodId': 'ddddcwef3242f32f'," +
                        "'foodUrl': 'http://api.foodex.com/food/dddd/ddddcwef3242f32f.jpg', " +
                        "'creation': '1383690800877'," +
                        "'mapUrl': 'http://api.foodex.com/map/eeee/eeeewef3242f32f.jpg'," +
                        "'bonAppetit': '0'" +
                    "}," +
                    "'stranger': {" +
                        "'foodId': 'abcwef3242f32f'," +
                        "'foodUrl': 'http://api.foodex.com/food/abc/abcwef3242f32f.jpg', " +
                        "'mapUrl': 'http://api.foodex.com/map/azca/azcacwef3242f32f.jpg'," +
                        "'bonAppetit': '1'" +
                    "}" +
                "},{" +
                "'user': {" +
                    "'foodId': 'abcdw0ef3242f32f'," +
                    "'foodUrl': 'http://api.foodex.com/food/abcd/abcdw0ef3242f32f.jpg', " +
                    "'creation': '1383670400877'," +
                    "'mapUrl': 'http://api.foodex.com/map/bcde/bcdecwef3242f32f.jpg'," +
                    "'bonAppetit': '1'" +
                "}," +
                "'stranger': {" +
                    "'foodId': 'abcd3cwef3242f32f'," +
                    "'foodUrl': 'http://api.foodex.com/food/abcd/abcd3cwef3242f32f.jpg', " +
                    "'mapUrl': 'http://api.foodex.com/map/abcd/abcd5wef3242f32f.jpg'," +
                    "'bonAppetit': '0'" +
                "}" +
            "}]}");
    }

    public static void mockAPI (int statusCode, String response) throws IOException {
        MainActivity.context = mockContext();
        API.client = mockClient(statusCode, response);
    }

    private static Context mockContext () {
        SharedPreferences sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        when(sharedPreferencesMock.getString(Constants.SEESSION_COOKIE_NAME, "")).thenReturn("123456789");

        Context contextMock = mock(Context.class);
        when(contextMock.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        return contextMock;
    }

    private static HttpClient mockClient(int statusCode, String response) throws IOException {
        HttpEntity entityMock = mock(HttpEntity.class);
        when(entityMock.getContent()).thenReturn(new ByteArrayInputStream(response.getBytes()));

        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(statusCode);

        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getEntity()).thenReturn(entityMock);
        when(responseMock.getStatusLine()).thenReturn(statusLineMock);

        HttpClient clientMock = mock(HttpClient.class);
        when(clientMock.execute(isA(HttpUriRequest.class))).thenReturn(responseMock);

        return clientMock;
    }
}
