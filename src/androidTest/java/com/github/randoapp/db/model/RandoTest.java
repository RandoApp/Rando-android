package com.github.randoapp.db.model;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.test.db.RandoTestHelper;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoTest {

    @Test
    public void shouldReturnTrueFromComparatorWhenDatesEqual() {
        Date date = new Date();
        Rando rando1 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        Rando rando2 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando1.date = date;
        rando2.date = date;
        assertThat(new Rando.DateComparator().compare(rando2, rando1)).isEqualTo(0);
    }

    @Test
    public void testDateLowerThan() {
        Date date1 = new Date();
        Date date2 = new Date();
        date2.setTime(date1.getTime() - 100);
        Rando rando1 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        Rando rando2 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando1.date = date1;
        rando2.date = date2;
        assertThat(new Rando.DateComparator().compare(rando2, rando1)).isGreaterThan(0);
    }

    @Test
    public void testDateGreaterThan() {
        Date date1 = new Date();
        Date date2 = new Date();
        date2.setTime(date1.getTime() + 100);
        Rando rando1 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        Rando rando2 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando1.date = date1;
        rando2.date = date2;
        assertThat(new Rando.DateComparator().compare(rando2, rando1)).isLessThan(0);
    }

    @Test
    public void testDateSortability() {
        List<Rando> randos = RandoTestHelper.getNRandomRandos(100, Rando.Status.IN);
        Collections.sort(randos, new Rando.DateComparator());
        RandoTestHelper.checkListNaturalOrder(randos);
    }

    /* ==Detected == */

    @Test
    public void shouldReturnTrueWhenDetectedAsUnwanted() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.detected = "\"unwanted\",\"bla\"";

        assertThat(rando.isUnwanted()).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenNotDetectedAsUnwanted() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.detected = "\"Not_unwanted\",\"bla\"";

        assertThat(rando.isUnwanted()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenNotDetectedIsNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.detected = null;

        assertThat(rando.isUnwanted()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenNotDetectedIsEmpty() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.detected = null;

        assertThat(rando.isUnwanted()).isFalse();
    }
}