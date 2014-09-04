package com.github.randoapp.test.api;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.User;
import com.github.randoapp.api.callback.OnFetchUser;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.network.VolleySingleton;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APITest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        App.context = this.getContext();
    }

    private File file = new File(".");

    @SmallTest
    public void testUploadFood() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);

        Rando rando = API.uploadImage(file, locationMock);
        String actual = rando.imageURL;
        assertThat(new Date(1383670800877l).compareTo(rando.date), is(0));
        assertThat(actual, is("http://rando4.me/image/abcd/abcdadfwefwef.jpg"));
    }

    @SmallTest
    public void testUploadFoodWithNullLocation() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        Rando rando = API.uploadImage(file, null);

        String actual = rando.imageURL;
        assertThat(new Date(1383670800877l).compareTo(rando.date), is(0));
        assertThat(actual, is("http://rando4.me/image/abcd/abcdadfwefwef.jpg"));
        //TODO: verify that lat and long is 0.0 in request
    }

    @SmallTest
    public void testUploadFoodWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            API.uploadImage(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testUploadFoodWithUnknownError() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_INTERNAL_SERVER_ERROR, "not a json, that throw JSONException");
        App.context = this.getContext();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            API.uploadImage(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(App.context.getResources().getString(R.string.error_unknown_err)));
        }
    }

    /*@SmallTest
    public void testFetchUser() throws Exception {
        APITestHelper.mockAPIForFetchUserNewAPI(getContext());

        final Semaphore semaphore = new Semaphore(0);

        API.fetchUserAsync(new OnFetchUser() {
            @Override
            public void onFetch(final List<Rando> randos) {
                assertThat(randos.size(), is(2));

                assertThat(randos.get(0).randoId, is("ddddcwef3242f32f"));
                assertThat(randos.get(0).imageURL, is("http://rando4.me/image/dddd/ddddcwef3242f32f.jpg"));
                assertThat(randos.get(0).mapURL, is("http://rando4.me/map/eeee/eeeewef3242f32f.jpg"));
                assertThat(randos.get(0).date.compareTo(new Date(1383690800877l)), is(0));

                assertThat(randos.get(1).randoId, is("abcdw0ef3242f32f"));
                assertThat(randos.get(1).imageURL, is("http://rando4.me/image/abcd/abcdw0ef3242f32f.jpg"));
                assertThat(randos.get(1).mapURL, is("http://rando4.me/map/bcde/bcdecwef3242f32f.jpg"));
                assertThat(randos.get(1).date.compareTo(new Date(1383670400877l)), is(0));
                semaphore.release();
            }
        });
        semaphore.acquire();
    }*/

    @SmallTest
    public void testFetchUserWithEmptyFoods() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_OK, "{'email': 'user@mail.com', 'randos': []}");

        API.fetchUserAsync(new OnFetchUser() {
            @Override
            public void onFetch(final User user) {
                assertThat(user.randosIn.size(), is(0));
            }
        });
    }

    /*@SmallTest
    public void testFetchUserWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.fetchUser();
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testFetchUserWithUnknownError() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_INTERNAL_SERVER_ERROR, "not a json, that throw JSONException");
        App.context = this.getContext();

        try {
            API.fetchUser();
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(App.context.getResources().getString(R.string.error_unknown_err)));
        }
    }*/

    @SmallTest
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

        assertThat(captor.getValue().getURI().toString().contains(Constants.REPORT_URL + "2222"), is(true));
    }

    @SmallTest
    public void testReportWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.report("2222");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testSignup() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_OK, "{}");

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        try {
            API.signup("user@mail.com", "password");
        } catch (Exception uglyException) {
            //DefaultHttpClient cannot be mocked... Just ignore the line with storeSession
        }

        verify(VolleySingleton.getInstance().httpClient).execute(captor.capture());

        assertThat(params(captor.getValue()), is(Constants.SIGNUP_EMAIL_PARAM + "=user%40mail.com&" + Constants.SIGNUP_PASSWORD_PARAM + "=password"));
        assertThat(captor.getValue().getURI().toString(), is(Constants.SIGNUP_URL));
    }

    @SmallTest
    public void testSignupWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.signup("user@mail.com", "password");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
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