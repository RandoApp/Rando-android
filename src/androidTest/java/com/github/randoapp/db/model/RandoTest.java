package com.github.randoapp.db.model;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.Constants;
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
        RandoTestHelper.assertListNaturalOrder(randos);
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

    /*==== isMapEmpty ====*/

    @Test
    public void shouldReturnTrueWhenMapUrlSizeIsNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize = null;

        assertThat(rando.isMapEmpty()).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenMapUrlsAreFilled() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeSmallIsNotNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.large = null;
        rando.mapURLSize.medium = null;

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeSmallIsNotEmpty() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.large = "";
        rando.mapURLSize.medium = "";

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeMediumIsNotNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.large = null;
        rando.mapURLSize.small = null;

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeMediumIsNotEmpty() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.large = "";
        rando.mapURLSize.small = "";

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeLargeIsNotNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.small = null;
        rando.mapURLSize.medium = null;

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenOnlyMapUrlSizeLargeIsNotEmpty() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.small = "";
        rando.mapURLSize.medium = "";

        assertThat(rando.isMapEmpty()).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenOnlyMapUrlSizesAreEmpty() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.small = "";
        rando.mapURLSize.medium = "";
        rando.mapURLSize.large = "";

        assertThat(rando.isMapEmpty()).isTrue();
    }

    @Test
    public void shouldReturnTrueWhenOnlyMapUrlSizesAreNull() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);
        rando.mapURLSize.small = null;
        rando.mapURLSize.medium = null;
        rando.mapURLSize.large = null;

        assertThat(rando.isMapEmpty()).isTrue();
    }

    /* === getBestImageUrlBySize =*/
    @Test
    public void shouldReturnLargeImageSizeWhenImageSizeIsEqualOrBitGreaterThenLarge() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_LARGE)).isEqualTo("imageURLLarge");
        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_LARGE+100)).isEqualTo("imageURLLarge");
    }

    @Test
    public void shouldReturnMediumImageSizeWhenImageSizeIsEqualToMediumOrBitGreaterThenMedium() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_MEDIUM)).isEqualTo("imageURLMedium");
        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_MEDIUM+100)).isEqualTo("imageURLMedium");
    }

    @Test
    public void shouldReturnSmallImageSizeWhenImageSizeIsEqualToSmallOrSmaller() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_SMALL)).isEqualTo("imageURLSmall");
        assertThat(rando.getBestImageUrlBySize(Constants.SIZE_SMALL-100)).isEqualTo("imageURLSmall");
    }

    /* === getBestMapUrlBySize =*/
    @Test
    public void shouldReturnLargeMapSizeWhenMapSizeIsEqualOrBitGreaterThenLarge() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_LARGE)).isEqualTo("mapURLLarge");
        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_LARGE+100)).isEqualTo("mapURLLarge");
    }

    @Test
    public void shouldReturnMediumMapSizeWhenMapSizeIsEqualToMediumOrBitGreaterThenMedium() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_MEDIUM)).isEqualTo("mapURLMedium");
        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_MEDIUM+100)).isEqualTo("mapURLMedium");
    }

    @Test
    public void shouldReturnSmallMapSizeWhenMapSizeIsEqualToSmallOrSmaller() {
        Rando rando = RandoTestHelper.getRandomRando(Rando.Status.IN);

        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_SMALL)).isEqualTo("mapURLSmall");
        assertThat(rando.getBestMapUrlBySize(Constants.SIZE_SMALL-100)).isEqualTo("mapURLSmall");
    }
}