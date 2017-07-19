package com.github.randoapp.test.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.BanService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class BanServiceTest {

    Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Before
    public void resetPreferences() {
        Preferences.setBanResetAt(context, 0L);
    }

    @Test
    public void shouldParseResetTimeInBanMessage() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 1234567890");

        assertThat("Reset time is not equal", resetTime, is(1234567890L));
    }

    @Test
    public void shouldReturn0WhenResetTimeDoesNotExist() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden");

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturn0WhenBanMessageIsNull() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage(null);

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturn0WhenResetTimeIs0() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 0");

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturn0IfFormatIsUnknown() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 1234567890.");

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturn0IfFormatIsUnknownDot() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 24 May");

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturn0IfResetTimeIsTooBig() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 9223372036854775808");

        assertThat("Reset time is not equal", resetTime, is(0L));
    }

    @Test
    public void shouldReturnMaxLongIfResetTimeIsMaxLong() throws Exception {
        BanService banService = new BanService();
        long resetTime = banService.parseResetTimeInBanMessage("Forbidden. Reset: 9223372036854775807");

        assertThat("Reset time is not equal", resetTime, is(9223372036854775807L));
    }

    @Test
    public void shouldPutResetTimeToProperties() throws Exception {
        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(0L));

        BanService banService = new BanService();
        banService.processForbiddenRequest(context, "Forbidden. Reset: 1234567890");

        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(1234567890L));
    }

    @Test
    public void shouldPutNothingToPropertiesWhenMessageIsNull() throws Exception {
        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(0L));

        BanService banService = new BanService();
        banService.processForbiddenRequest(context, null);

        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(0L));
    }

    @Test
    public void shouldPutNothingToPropertiesWhenMessageContains0ResetTime() throws Exception {
        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(0L));

        BanService banService = new BanService();
        banService.processForbiddenRequest(context, "Forbidden. Reset: 0");

        assertThat("Reset time is not equal", Preferences.getBanResetAt(context), is(0L));
    }
}
