package com.github.randoapp.test.db;

import com.github.randoapp.db.model.Rando;

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

public class RandoTestHelper {
    private static Random random = new Random();

    public static List<Rando> getNRandomRandos(int n, Rando.Status status) {
        Date baseDate = new Date();

        List<Rando> randos = new ArrayList<Rando>();
        for (int i = 0; i < n; i++) {
            Rando Rando;
            Date userDate = new Date();
            userDate.setTime(baseDate.getTime() + random.nextInt(1000000));

            Rando = new Rando();
            Rando.randoId = UUID.randomUUID().toString();
            Rando.imageURL = "blaURL" + i;
            Rando.imageURLSize.small  = "blaURL" + i;
            Rando.imageURLSize.medium = "blaURL" + i;
            Rando.imageURLSize.large = "blaURL" + i;
            Rando.mapURL = "blaFile" + i;
            Rando.mapURLSize.small  = "blaURL" + i;
            Rando.mapURLSize.medium = "blaURL" + i;
            Rando.mapURLSize.large = "blaURL" + i;
            Rando.date = userDate;
            Rando.status = status;

            randos.add(Rando);
        }

        return randos;
    }

    public static Rando getRandomRando(Rando.Status status) {

        Rando Rando;
        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        Rando = new Rando();
        Rando.randoId = UUID.randomUUID().toString();
        Rando.imageURL = "imageURL";
        Rando.imageURLSize.small = "imageURLSmall";
        Rando.imageURLSize.medium = "imageURLMedium";
        Rando.imageURLSize.large = "imageURLLarge";
        Rando.mapURL = "mapURLe";
        Rando.mapURLSize.small = "mapURLSmall";
        Rando.mapURLSize.medium = "mapURLMedium";
        Rando.mapURLSize.large = "mapURLLarge";
        Rando.date = userDate;
        Rando.status = status;
        Rando.rating = random.nextInt(4);

        return Rando;
    }

    public static void assertListNaturalOrder(List<Rando> randos) {
        Rando prevPair = null;
        for (Rando Rando : randos) {
            if (prevPair != null) {
                assertThat("Order is broken: " + Rando.date.toString() + " is less than " + prevPair.date.toString(), Rando.date, lessThanOrEqualTo(prevPair.date));
            } else {
                prevPair = Rando;
            }
        }
    }

    public static void checkListsEqual(List<Rando> randos1, List<Rando> randos2) {
        assertThat(randos1, notNullValue());
        assertThat(randos2, notNullValue());
        assertThat("Sizes not equal", randos1.size(), is(randos2.size()));
        Collections.sort(randos1, new Rando.DateComparator());
        Collections.sort(randos2, new Rando.DateComparator());
        for (int i = 0; i < randos1.size(); i++) {
            assertThat("RandoPairs not equal: " + randos1.get(i).toString() + " != " + randos2.get(i).toString(), randos1.get(i), is(randos2.get(i)));
        }
    }

}
