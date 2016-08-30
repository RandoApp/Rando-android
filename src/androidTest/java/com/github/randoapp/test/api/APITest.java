package com.github.randoapp.test.api;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.network.VolleySingleton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(AndroidJUnit4.class)
@SmallTest
public class APITest {

    @Before
    public void setUp() throws Exception {
        App.context = InstrumentationRegistry.getTargetContext();
        App.context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    private File file = new File(".");

    @Test
    public void shouldUploadRando() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);

        Rando rando = API.uploadImage(file, locationMock);
        String actual = rando.imageURL;
        assertThat(new Date(1471081017405l)).isEqualTo(rando.date);
        assertThat(actual).isEqualToIgnoringCase("http://dev.img.l.rando4me.s3.amazonaws.com/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg");
    }

    @Test
    public void shouldUploadRandoWhenNullLocationIsPassed() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        Rando rando = API.uploadImage(file, null);

        String actual = rando.imageURL;
        assertThat(new Date(1471081017405l)).isEqualTo(rando.date);
        assertThat(actual).isEqualToIgnoringCase("http://dev.img.l.rando4me.s3.amazonaws.com/24975aff328fc4d5cedbb7ca7d235d6f0bfde1781b.jpg");
        //TODO: verify that lat and long is 0.0 in request
    }

    @Test
    public void testUploadRandoWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            API.uploadImage(file, locationMock);
            fail("Exception should be thrown before.");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualToIgnoringCase("Internal Server Error");
        }
    }

    @Test
    public void testUploadRandoWithUnknownError() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_INTERNAL_SERVER_ERROR, "not a json, that throw JSONException");

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            API.uploadImage(file, locationMock);
            fail("Exception should be thrown before.");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo(App.context.getResources().getString(R.string.error_unknown_err));
        }
    }

    @Test
    public void testReport() throws Exception {
        VolleySingleton.getInstance().httpClient = mock(HttpClient.class);
        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getStatusLine()).thenReturn(statusLineMock);
        when(VolleySingleton.getInstance().httpClient.execute(isA(HttpUriRequest.class))).thenReturn(responseMock);
        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        API.report("2222");

        verify(VolleySingleton.getInstance().httpClient).execute(captor.capture());

        assertThat(captor.getValue().getURI().toString()).contains(Constants.REPORT_URL + "2222");
    }

    @Test
    public void testReportWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.report("2222");
            fail("Exception should be thrown before.");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("Internal Server Error");
        }
    }

    @Test
    public void testSignup() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_OK, "{}");

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        try {
            API.signup("user@mail.com", "password");
        } catch (Exception uglyException) {
            //DefaultHttpClient cannot be mocked... Just ignore the line with storeSession
        }

        verify(VolleySingleton.getInstance().httpClient).execute(captor.capture());

        assertThat(params(captor.getValue())).startsWith(Constants.SIGNUP_EMAIL_PARAM + "=user%40mail.com&" + Constants.SIGNUP_PASSWORD_PARAM + "=password");
        assertThat(captor.getValue().getURI().toString()).isEqualTo(Constants.SIGNUP_URL);
    }

    @Test
    public void testSignupWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.signup("user@mail.com", "password");
            fail("Exception should be thrown before.");
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualToIgnoringCase("Internal Server Error");
        }
    }

    private String params(HttpPost request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getEntity().getContent()));
        StringBuilder params = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            params.append(line);
        }
        return params.toString();
    }

}