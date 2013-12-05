package com.eucsoft.foodex.test.api;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.model.FoodPair;

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

        FoodPair foodPair = API.uploadFood(file, locationMock);
        String actual = foodPair.user.foodURL;
        assertThat(new Date(1383670800877l).compareTo(foodPair.user.foodDate), is(0));
        assertThat(actual, is("http://api.foodex.com/food/abcd/abcdadfwefwef.jpg"));
    }

    @SmallTest
    public void testUploadFoodWithNullLocation() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        FoodPair foodPair = API.uploadFood(file, null);

        String actual = foodPair.user.foodURL;
        assertThat(new Date(1383670800877l).compareTo(foodPair.user.foodDate), is(0));
        assertThat(actual, is("http://api.foodex.com/food/abcd/abcdadfwefwef.jpg"));
        //TODO: verify that lat and long is 0.0 in request
    }

    @SmallTest
    public void testUploadFoodWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            API.uploadFood(file, locationMock);
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
            API.uploadFood(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(App.context.getResources().getString(R.string.error_unknown_err)));
        }
    }

    @SmallTest
    public void testFetchUser() throws Exception {
        APITestHelper.mockAPIForFetchUser();

        List<FoodPair> foods = API.fetchUser();

        assertThat(foods.size(), is(2));

        assertThat(foods.get(0).user.foodId, is("ddddcwef3242f32f"));
        assertThat(foods.get(0).user.foodURL, is("http://api.foodex.com/food/dddd/ddddcwef3242f32f.jpg"));
        assertThat(foods.get(0).user.mapURL, is("http://api.foodex.com/map/eeee/eeeewef3242f32f.jpg"));
        assertThat(foods.get(0).user.bonAppetit, is(0));
        assertThat(foods.get(0).user.foodDate.compareTo(new Date(1383690800877l)), is(0));
        assertThat(foods.get(0).stranger.foodId, is("abcwef3242f32f"));
        assertThat(foods.get(0).stranger.foodURL, is("http://api.foodex.com/food/abc/abcwef3242f32f.jpg"));
        assertThat(foods.get(0).stranger.mapURL, is("http://api.foodex.com/map/azca/azcacwef3242f32f.jpg"));
        assertThat(foods.get(0).stranger.bonAppetit, is(1));

        assertThat(foods.get(1).user.foodId, is("abcdw0ef3242f32f"));
        assertThat(foods.get(1).user.foodURL, is("http://api.foodex.com/food/abcd/abcdw0ef3242f32f.jpg"));
        assertThat(foods.get(1).user.mapURL, is("http://api.foodex.com/map/bcde/bcdecwef3242f32f.jpg"));
        assertThat(foods.get(1).user.bonAppetit, is(1));
        assertThat(foods.get(1).user.foodDate.compareTo(new Date(1383670400877l)), is(0));
        assertThat(foods.get(1).stranger.foodId, is("abcd3cwef3242f32f"));
        assertThat(foods.get(1).stranger.foodURL, is("http://api.foodex.com/food/abcd/abcd3cwef3242f32f.jpg"));
        assertThat(foods.get(1).stranger.mapURL, is("http://api.foodex.com/map/abcd/abcd5wef3242f32f.jpg"));
        assertThat(foods.get(1).stranger.bonAppetit, is(0));
    }

    @SmallTest
    public void testFetchUserWithEmptyFoods() throws Exception {
        APITestHelper.mockAPI(HttpStatus.SC_OK, "{'email': 'user@mail.com', 'foods': []}");

        List<FoodPair> foods = API.fetchUser();

        assertThat(foods.size(), is(0));
    }

    @SmallTest
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
    }

    @SmallTest
    public void testReport() throws Exception {
        API.client = mock(HttpClient.class);
        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getStatusLine()).thenReturn(statusLineMock);
        when(API.client.execute(isA(HttpUriRequest.class))).thenReturn(responseMock);
        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        API.report("2222");

        verify(API.client).execute(captor.capture());

        assertThat(captor.getValue().getURI().toString(), is(Constants.REPORT_URL + "2222"));
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
    public void testBonAppetit() throws Exception {
        API.client = mock(HttpClient.class);
        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        HttpResponse responseMock = mock(HttpResponse.class);
        when(responseMock.getStatusLine()).thenReturn(statusLineMock);
        when(API.client.execute(isA(HttpUriRequest.class))).thenReturn(responseMock);
        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        API.bonAppetit("3333");

        verify(API.client).execute(captor.capture());

        assertThat(captor.getValue().getURI().toString(), is(Constants.BON_APPETIT_URL + "3333"));
    }

    @SmallTest
    public void testBonAppetitWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.bonAppetit("3333");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testDownloadFood() throws Exception {
        APITestHelper.mockAPIForDownloadFood();

        byte[] image = API.downloadFood("http://api.foodex.com/foods/abcd/abcdeffweijfwe.jpg");

        assertThat(image.length, is("jpg file".getBytes().length));
    }

    @SmallTest
    public void testDownloadFoodWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            API.downloadFood("http://api.foodex.com/foods/abcd/abcdeffweijfwe.jpg");
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

        verify(API.client).execute(captor.capture());

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