package com.github.randoapp.test.db;

import com.github.randoapp.db.model.RandoPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

public class RandoPairTestHelper {
    private static Random random = new Random();

    public static List<RandoPair> getNRandomRandoPairs(int n) {
        Date baseDate = new Date();

        List<RandoPair> RandoPairs = new ArrayList<RandoPair>();
        for (int i = 0; i < n; i++) {
            RandoPair RandoPair;
            Date userDate = new Date();
            userDate.setTime(baseDate.getTime() + random.nextInt(1000000));

            RandoPair = new RandoPair();
            RandoPair.user.randoId = UUID.randomUUID().toString();
            RandoPair.user.imageURL = "blaURL" + i;
            RandoPair.user.imageURLSize.small  = "blaURL" + i;
            RandoPair.user.imageURLSize.medium = "blaURL" + i;
            RandoPair.user.imageURLSize.large = "blaURL" + i;
            RandoPair.user.mapURL = "blaFile" + i;
            RandoPair.user.mapURLSize.small  = "blaURL" + i;
            RandoPair.user.mapURLSize.medium = "blaURL" + i;
            RandoPair.user.mapURLSize.large = "blaURL" + i;
            RandoPair.user.date = userDate;

            Date strangerDate = new Date();
            strangerDate.setTime(baseDate.getTime() + random.nextInt(1000000));
            RandoPair.stranger.randoId = UUID.randomUUID().toString();
            RandoPair.stranger.imageURL = "Bla2URL" + i;
            RandoPair.stranger.imageURLSize.small = "Bla2URL" + i;
            RandoPair.stranger.imageURLSize.medium = "Bla2URL" + i;
            RandoPair.stranger.imageURLSize.large = "Bla2URL" + i;
            RandoPair.stranger.mapURL = "LocalFileStranger" + i;
            RandoPair.stranger.mapURLSize.small = "Bla2URL" + i;
            RandoPair.stranger.mapURLSize.medium = "Bla2URL" + i;
            RandoPair.stranger.mapURLSize.large = "Bla2URL" + i;
            RandoPair.stranger.date = strangerDate;
            RandoPairs.add(RandoPair);
        }

        return RandoPairs;
    }

    public static RandoPair getRandomRandoPair() {

        RandoPair RandoPair;
        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        RandoPair = new RandoPair();
        RandoPair.user.randoId = UUID.randomUUID().toString();
        RandoPair.user.imageURL = "blaURL";
        RandoPair.user.imageURLSize.small = "blaURL";
        RandoPair.user.imageURLSize.medium = "blaURL";
        RandoPair.user.imageURLSize.large = "blaURL";
        RandoPair.user.mapURL = "blaFile";
        RandoPair.user.mapURLSize.small = "blaURL";
        RandoPair.user.mapURLSize.medium = "blaURL";
        RandoPair.user.mapURLSize.large = "blaURL";
        RandoPair.user.date = userDate;

        Date strangerDate = new Date();
        strangerDate.setTime(new Date().getTime() + random.nextInt(1000000));
        RandoPair.stranger.randoId = UUID.randomUUID().toString();
        RandoPair.stranger.imageURL = "Bla2URL";
        RandoPair.stranger.imageURLSize.small = "Bla2URL";
        RandoPair.stranger.imageURLSize.medium = "Bla2URL";
        RandoPair.stranger.imageURLSize.large = "Bla2URL";
        RandoPair.stranger.mapURL = "LocalFileStranger";
        RandoPair.stranger.mapURLSize.small = "Bla2URL";
        RandoPair.stranger.mapURLSize.medium = "Bla2URL";
        RandoPair.stranger.mapURLSize.large = "Bla2URL";
        RandoPair.stranger.date = strangerDate;
        return RandoPair;
    }

    public static RandoPair getRandomRandoPairNotPaired() {

        RandoPair RandoPair;
        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        RandoPair = new RandoPair();
        RandoPair.user.randoId = UUID.randomUUID().toString();
        RandoPair.user.imageURL = "blaURL";
        RandoPair.user.imageURLSize.small = "blaURL";
        RandoPair.user.imageURLSize.medium = "blaURL";
        RandoPair.user.imageURLSize.large = "blaURL";
        RandoPair.user.mapURL = "blaFile";
        RandoPair.user.mapURLSize.small = "blaURL";
        RandoPair.user.mapURLSize.medium = "blaURL";
        RandoPair.user.mapURLSize.large = "blaURL";
        RandoPair.user.date = userDate;

        return RandoPair;
    }

    public static void checkListNaturalOrder(List<RandoPair> RandoPairs) {
        RandoPair prevPair = null;
        for (RandoPair RandoPair : RandoPairs) {
            if (prevPair != null) {
                assertThat("Order is broken: " + RandoPair.user.date.toString() + " is less than " + prevPair.user.date.toString(), RandoPair.user.date, lessThanOrEqualTo(prevPair.user.date));
            } else {
                prevPair = RandoPair;
            }
        }
    }

    public static void checkListsEqual(List<RandoPair> RandoPairs1, List<RandoPair> RandoPairs2) {
        assertThat(RandoPairs1, notNullValue());
        assertThat(RandoPairs2, notNullValue());
        assertThat("Sizes not equal", RandoPairs1.size(), is(RandoPairs2.size()));
        Collections.sort(RandoPairs1, new RandoPair.DateComparator());
        Collections.sort(RandoPairs2, new RandoPair.DateComparator());
        for (int i = 0; i < RandoPairs1.size(); i++) {
            assertThat("RandoPairs not equal: " + RandoPairs1.get(i).toString() + " != " + RandoPairs2.get(i).toString(), RandoPairs1.get(i), is(RandoPairs2.get(i)));
        }
    }

}
