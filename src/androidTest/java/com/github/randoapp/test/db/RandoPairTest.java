package com.github.randoapp.test.db;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.github.randoapp.db.model.RandoPair;

import org.hamcrest.Matchers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RandoPairTest extends AndroidTestCase {

    @SmallTest
    public void testRandoPairUsersNotNull() {
        RandoPair RandoPair = new RandoPair();
        assertThat(RandoPair.user, notNullValue());
        assertThat(RandoPair.stranger, notNullValue());
    }

    @SmallTest
    public void testDateEqual() {
        Date date = new Date();
        RandoPair RandoPair1 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair RandoPair2 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair1.user.date = date;
        RandoPair2.user.date = date;
        assertThat("Equal RandoPair dates doesn't return 0 on compare.", new RandoPair.DateComparator().compare(RandoPair2, RandoPair1), is(0));
    }

    @SmallTest
    public void testDateLowerThan() {
        Date date1 = new Date();
        Date date2 = new Date();
        date2.setTime(date1.getTime() - 100);
        RandoPair RandoPair1 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair RandoPair2 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair1.user.date = date1;
        RandoPair2.user.date = date2;
        assertThat("RandoPairs comparation failed", new RandoPair.DateComparator().compare(RandoPair2, RandoPair1), Matchers.greaterThan(0));
    }

    @SmallTest
    public void testDateGreaterThan() {
        Date date1 = new Date();
        Date date2 = new Date();
        date2.setTime(date1.getTime() + 100);
        RandoPair RandoPair1 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair RandoPair2 = RandoPairTestHelper.getRandomRandoPair();
        RandoPair1.user.date = date1;
        RandoPair2.user.date = date2;
        assertThat("RandoPairs comparation failed", new RandoPair.DateComparator().compare(RandoPair2, RandoPair1), Matchers.lessThan(0));
    }

    @SmallTest
    public void testDateSortability() {
        List<RandoPair> RandoPairs = RandoPairTestHelper.getNRandomRandoPairs(100);
        Collections.sort(RandoPairs, new RandoPair.DateComparator());
        RandoPairTestHelper.checkListNaturalOrder(RandoPairs);
    }

    // getRandoFileName Tests
    @SmallTest
    public void testUsergetRandoFileName() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.user.imageURL = "http://cool-projects.com/rando/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(RandoPair.user.getRandoFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testStrangergetRandoFileName() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.stranger.imageURL = "http://cool-projects.com/rando/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(RandoPair.stranger.getRandoFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testUsergetRandoFileNameNull() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.user.imageURL = null;
        assertThat(RandoPair.user.getRandoFileName(), nullValue());
    }

    @SmallTest
    public void testStrangergetRandoFileNameNull() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.stranger.imageURL = null;
        assertThat(RandoPair.stranger.getRandoFileName(), nullValue());
    }

    // GetMapFileName Tests
    @SmallTest
    public void testUserGetMapFileName() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.user.mapURL = "http://cool-projects.com/rando/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(RandoPair.user.getMapFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testStrangerGetMapFileName() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.stranger.mapURL = "http://cool-projects.com/rando/abcd/abcd24jjf4f4f4f.jpg";
        assertThat(RandoPair.stranger.getMapFileName(), is("abcd24jjf4f4f4f.jpg"));
    }

    @SmallTest
    public void testUserGetMapFileNameNull() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.user.imageURL = null;
        assertThat(RandoPair.user.getMapFileName(), nullValue());
    }

    @SmallTest
    public void testStrangerGetMapFileNameNull() {
        RandoPair RandoPair = new RandoPair();
        RandoPair.stranger.imageURL = null;
        assertThat(RandoPair.stranger.getMapFileName(), nullValue());
    }
}