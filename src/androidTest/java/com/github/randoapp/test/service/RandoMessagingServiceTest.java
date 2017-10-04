package com.github.randoapp.test.service;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.Constants;
import com.github.randoapp.service.RandoMessagingService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoMessagingServiceTest {

    @Test
    @Ignore
    public void shouldReturnTrueWhenRatingIsEmpty() throws Exception {
        RandoMessagingService randoMessagingService = new RandoMessagingService();
        Map<String, String> data = new HashMap<>();
        data.put(Constants.NOTIFICATION_TYPE_PARAM, Constants.PUSH_NOTIFICATION_RATED);
        data.put(Constants.RANDO_PARAM, "{}");

        randoMessagingService.processMessage(data);

        assertThat("TODO", true, is(true));
    }

}
