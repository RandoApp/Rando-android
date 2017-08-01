package com.github.randoapp.db.model;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.test.db.RandoTestHelper;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoTest  {

    @Test
    public void shouldReturnTrueFromComparatorWhenDatesEqual() {
        Date date = new Date();
        Rando rando1 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        Rando rando2 = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando1.date = date;
        rando2.date = date;
        assertThat("Equal Rando dates doesn't return 0 on compare.", new Rando.DateComparator().compare(rando2, rando1), is(0));
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
        assertThat("RandoPairs comparation failed", new Rando.DateComparator().compare(rando2, rando1), Matchers.greaterThan(0));
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
        assertThat("RandoPairs comparation failed", new Rando.DateComparator().compare(rando2, rando1), Matchers.lessThan(0));
    }

    @Test
    public void testDateSortability() {
        List<Rando> randos = RandoTestHelper.getNRandomRandos(100, Rando.Status.IN);
        Collections.sort(randos, new Rando.DateComparator());
        RandoTestHelper.checkListNaturalOrder(randos);
    }

}