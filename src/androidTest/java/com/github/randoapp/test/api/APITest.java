package com.github.randoapp.test.api;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.network.VolleySingleton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.methods.HttpPost;

import static com.github.randoapp.Constants.PREFERENCES_FILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.verify;
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