package com.eucsoft.foodex.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.api.API;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;


public class APITest extends AndroidTestCase {

    public void testSignup() throws Exception {
        SharedPreferences sharedPreferencesMock = Mockito.mock(SharedPreferences.class);
        when(sharedPreferencesMock.getString(Constants.SEESSION_COOKIE_NAME, "")).thenReturn("123456789");

        Context contextMock = mock(Context.class);
        when(contextMock.getSharedPreferences(Constants.SEESSION_COOKIE_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        HttpEntity entityMock = mock(HttpEntity.class);
        when(entityMock.getContent()).thenReturn(new ByteArrayInputStream("Bla".getBytes()));

        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(200);

        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getEntity()).thenReturn(entityMock);
        responseMock.setStatusLine(statusLineMock);

        DefaultHttpClient clientMock =  mock(DefaultHttpClient.class);
        when(clientMock.execute(Matchers.<HttpUriRequest>anyObject())).thenReturn(responseMock);


        MainActivity.context = contextMock;
        API.client = clientMock;

        API.signup("user@mail.com", "password");
    }

}
