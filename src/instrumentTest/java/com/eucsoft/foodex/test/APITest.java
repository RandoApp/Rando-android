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

}