package com.eucsoft.foodex.test;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.test.util.APITestHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class APITest extends AndroidTestCase {

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
    public void testUploadFoodWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            FoodPair foodPair = API.uploadFood(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testUploadFoodWithUnknownError() throws Exception {
        APITestHelper.mockAPI(500, "not a json, that throw JSONException");
        MainActivity.context = this.getContext();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            FoodPair foodPair = API.uploadFood(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(MainActivity.context.getResources().getString(R.string.error_unknown_err)));
        }
    }

    @SmallTest
    public void testFetchUser() throws Exception {
        APITestHelper.mockAPIForFetchUser();

        List<FoodPair> foods = API.fetchUser();

        assertThat(foods.size(), is(2));

        assertThat(foods.get(0).user.foodURL, is("http://api.foodex.com/food/dddd/ddddcwef3242f32f.jpg"));
        assertThat(foods.get(0).user.mapURL, is("http://api.foodex.com/map/eeee/eeeewef3242f32f.jpg"));
        assertThat(foods.get(0).user.bonAppetit, is(0));
        assertThat(foods.get(0).user.foodDate.compareTo(new Date(1383690800877l)), is(0));
        assertThat(foods.get(0).stranger.foodURL, is("http://api.foodex.com/food/abc/abcwef3242f32f.jpg"));
        assertThat(foods.get(0).stranger.mapURL, is("http://api.foodex.com/map/azca/azcacwef3242f32f.jpg"));
        assertThat(foods.get(0).stranger.bonAppetit, is(1));

        assertThat(foods.get(1).user.foodURL, is("http://api.foodex.com/food/abcd/abcdw0ef3242f32f.jpg"));
        assertThat(foods.get(1).user.mapURL, is("http://api.foodex.com/map/bcde/bcdecwef3242f32f.jpg"));
        assertThat(foods.get(1).user.bonAppetit, is(1));
        assertThat(foods.get(1).user.foodDate.compareTo(new Date(1383670400877l)), is(0));
        assertThat(foods.get(1).stranger.foodURL, is("http://api.foodex.com/food/abcd/abcd3cwef3242f32f.jpg"));
        assertThat(foods.get(1).stranger.mapURL, is("http://api.foodex.com/map/abcd/abcd5wef3242f32f.jpg"));
        assertThat(foods.get(1).stranger.bonAppetit, is(0));
    }

    @SmallTest
    public void testFetchUserWithEmptyFoods() throws Exception {
        APITestHelper.mockAPI(200, "{'email': 'user@mail.com', 'foods': []}");

        List<FoodPair> foods = API.fetchUser();

        assertThat(foods.size(), is(0));
    }

    @SmallTest
    public void testFetchUserWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        try {
            List<FoodPair> foods = API.fetchUser();
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    @SmallTest
    public void testFetchUserWithUnknownError() throws Exception {
        APITestHelper.mockAPI(500, "not a json, that throw JSONException");
        MainActivity.context = this.getContext();

        try {
            API.fetchUser();
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(MainActivity.context.getResources().getString(R.string.error_unknown_err)));
        }
    }




}