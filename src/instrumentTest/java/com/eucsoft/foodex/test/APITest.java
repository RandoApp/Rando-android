package com.eucsoft.foodex.test;

import android.location.Location;
import android.test.AndroidTestCase;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.db.model.Food;
import com.eucsoft.foodex.test.util.APITestHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static org.mockito.Mockito.*;


public class APITest extends AndroidTestCase {

    private File file = new File(".");

    public void testUploadFood() throws Exception {
        APITestHelper.mockAPIForUploadFood();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);

        Food food = API.uploadFood(file, locationMock);
        String actual = food.getUserPhotoURL();
        assertThat(new Date(1383670800877l).compareTo(food.creation), is(0));
        assertThat(actual, is("http://api.foodex.com/food/abcd/abcdadfwefwef.jpg"));
    }

    public void testUploadFoodWithError() throws Exception {
        APITestHelper.mockAPIWithError();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            Food food = API.uploadFood(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Internal Server Error"));
        }
    }

    public void testUploadFoodWithUnknownError() throws Exception {
        APITestHelper.mockAPI(500, "not a json, that throw JSONException");
        MainActivity.context = this.getContext();

        Location locationMock = mock(Location.class);
        when(locationMock.getLatitude()).thenReturn(123.45);
        when(locationMock.getLongitude()).thenReturn(567.89);
        try {
            Food food = API.uploadFood(file, locationMock);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), is(MainActivity.context.getResources().getString(R.string.error_unknown_err)));
        }
    }

}
