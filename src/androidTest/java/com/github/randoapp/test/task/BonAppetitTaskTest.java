package com.github.randoapp.test.task;

import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.task.BonAppetitTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.test.api.APITestHelper;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public class BonAppetitTaskTest extends AndroidTestCase {

    @MediumTest
    @UiThreadTest
    public void testBonAppetitSuccess() throws InterruptedException {
        final RandoPair RandoPair = new RandoPair();
        RandoPair.user.imageURL = "blaURL";
        RandoPair.user.mapURL = "blaFile";
        RandoPair.user.bonAppetit = 0;
        RandoPair.user.date = new Date();

        RandoPair.stranger.imageURL = "blaURL";
        RandoPair.stranger.mapURL = "blaFile";
        RandoPair.stranger.bonAppetit = 0;
        RandoPair.stranger.date = new Date();

        try {
            APITestHelper.mockAPI(HttpStatus.SC_OK, "Everything is OK!");
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(RandoPair)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    assertThat(data.get(Constants.RANDO_PAIR), notNullValue());
                    assertThat(data.get(Constants.RANDO_PAIR), is(instanceOf(RandoPair.class)));
                    RandoPair RandoPair1 = (RandoPair) data.get(Constants.RANDO_PAIR);
                    assertThat(RandoPair1.stranger.isBonAppetit(), is(true));
                    RandoPair.stranger.bonAppetit = 0;
                    assertThat(RandoPair1, is(RandoPair));
                }
            })
            .executeSync();
    }

    @MediumTest
    @UiThreadTest
    public void testBonAppetitError() throws InterruptedException {
        final RandoPair RandoPair = new RandoPair();
        RandoPair.user.imageURL = "blaURL";
        RandoPair.user.mapURL = "blaFile";
        RandoPair.user.bonAppetit = 0;
        RandoPair.user.date = new Date();

        RandoPair.stranger.imageURL = "blaURL";
        RandoPair.stranger.mapURL = "blaFile";
        RandoPair.stranger.bonAppetit = 0;
        RandoPair.stranger.date = new Date();

        try {
            APITestHelper.mockAPIWithError();
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(RandoPair)
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    assertThat(data.get(Constants.RANDO_PAIR), notNullValue());
                    assertThat(data.get(Constants.RANDO_PAIR), is(instanceOf(RandoPair.class)));
                    RandoPair RandoPair1 = (RandoPair) data.get(Constants.RANDO_PAIR);
                    assertThat(RandoPair1.stranger.isBonAppetit(), is(false));
                    assertThat(RandoPair1, is(RandoPair));
                }
            })
            .executeSync();
    }

    @MediumTest
    @UiThreadTest
    public void testRandoPaidNull() throws InterruptedException {
        try {
            APITestHelper.mockAPI(HttpStatus.SC_OK, "Everything is OK!");
        } catch (IOException e) {
            fail("Error while mocking API.client.");
        }
        App.context = getContext();

        new BonAppetitTask(null)
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    assertThat(data.get(Constants.RANDO_PAIR), nullValue());
                }
            })
            .executeSync();
    }
}
