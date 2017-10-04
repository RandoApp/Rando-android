package com.github.randoapp.test.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.Constants;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.notification.Notification;
import com.github.randoapp.service.RandoMessagingService;
import com.github.randoapp.util.RandoUtil;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static com.github.randoapp.test.db.RandoTestHelper.getRandomRando;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoMessagingServiceTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

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
