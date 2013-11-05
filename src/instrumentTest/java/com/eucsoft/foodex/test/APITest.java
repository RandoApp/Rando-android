package com.eucsoft.foodex.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.model.Food;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class APITest extends AndroidTestCase {

    private File file = new File(".");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MainActivity.context = mockContext();
    }

    public void testUploadFood() throws Exception {
        String expected = "http://api.foodex.com/food/abcd/abcdadfwefwef.jpeg";

        API.client = mockClient(200,
            "{" +
                "\"creation\": \"1383670800877\"," +
                 "\"foodUrl\": \"" + expected + "\"," +
                    "\"mapURL\": \"\"" +
                    "}");

        Food food = API.uploadFood(file);
        String actual = food.getUserPhotoURL();
        assertThat(actual, is(expected));
    }

    private Context mockContext () {
        SharedPreferences sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        when(sharedPreferencesMock.getString(Constants.SEESSION_COOKIE_NAME, "")).thenReturn("123456789");

        Context contextMock = mock(Context.class);
        when(contextMock.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        return contextMock;
    }

    private HttpClient mockClient(int statusCode, String response) throws IOException {
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

    private File createFile() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/tmp.png");
        FileOutputStream fileStream = new FileOutputStream(file);
        fileStream.write("This is a stub food file".getBytes());
        fileStream.flush();
        fileStream.close();
        return file;
    }
}
