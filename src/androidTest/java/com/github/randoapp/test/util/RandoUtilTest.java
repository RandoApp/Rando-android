package com.github.randoapp.test.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.util.RandoUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.github.randoapp.test.db.RandoTestHelper.getRandomRando;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoUtilTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void shouldReturnTrueWhenRatingIsEmpty() throws Exception {
        Rando mockedRando = getRandomRando(Rando.Status.OUT);
        mockedRando.rating = 0;
        RandoDAO.createRando(context, mockedRando);

        boolean isRatedFirstTime = RandoUtil.isRatedFirstTime(mockedRando.randoId, context);
        assertThat("Rando detected as first time rated", isRatedFirstTime, is(true));
    }

    @Test
    public void shouldReturnFalseWhenRatingIsNotEmpty() throws Exception {
        Rando mockedRando = getRandomRando(Rando.Status.OUT);
        mockedRando.rating = 3;
        RandoDAO.createRando(context, mockedRando);

        boolean isRatedFirstTime = RandoUtil.isRatedFirstTime(mockedRando.randoId, context);
        assertThat("Rando detected as first time rated when rando already rated", isRatedFirstTime, is(false));
    }

    @Test
    public void shouldReturnFalseWhenRandoIdIsNull() throws Exception {
        boolean isRatedFirstTime = RandoUtil.isRatedFirstTime(null, context);
        assertThat("Rando detected as first time rated when randoId is null", isRatedFirstTime, is(false));
    }

    @Test
    public void shouldReturnFalseWhenContextIsNull() throws Exception {
        boolean isRatedFirstTime = RandoUtil.isRatedFirstTime(null, context);
        assertThat("Rando detected as first time rated when context is null", isRatedFirstTime, is(false));
    }

    @Test
    public void shouldReturnFalseWhenRandoDoesNotExistInDB() throws Exception {
        boolean isRatedFirstTime = RandoUtil.isRatedFirstTime("123", context);
        assertThat("Rando detected as first time rated when rando does not exist in db", isRatedFirstTime, is(false));
    }

}
