package com.github.randoapp.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.db.model.Rando;
import com.github.randoapp.db.model.RandoUpload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static com.github.randoapp.db.RandoDAO.addToUpload;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoDAOTest_getCursor {

    private static Random random = new Random();

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }

    @After
    public void tearDown() throws Exception {
        RandoDAO.clearRandos(context);
        RandoDAO.clearRandoToUpload(context);
    }


    @Test
    public void shouldReturnInOutRandos() {
        // create 1 out and 2 in randos
        //
        RandoDAO.createRando(context, getRandomRando(Rando.Status.OUT));

        RandoDAO.createRando(context, getRandomRando(Rando.Status.IN));

        RandoDAO.createRando(context, getRandomRando(Rando.Status.IN));

        assertThat(RandoDAO.getCursor(context, true).getCount()).isEqualTo(2);
        assertThat(RandoDAO.getCursor(context, false).getCount()).isEqualTo(1);
    }

    @Test
    public void shouldReturnInOutToUploadRandos() {
        addToUpload(context, new RandoUpload("/path/to/file1", 13.33, 14.44, new Date(10)));
        addToUpload(context, new RandoUpload("/path/to/file2", 23.33, 24.44, new Date(50)));
        addToUpload(context, new RandoUpload("/path/to/file3", 33.33, 44.44, new Date(300)));

        // create 1 out and 2 in randos
        //
        RandoDAO.createRando(context, getRandomRando(Rando.Status.OUT));

        RandoDAO.createRando(context, getRandomRando(Rando.Status.IN));

        RandoDAO.createRando(context, getRandomRando(Rando.Status.IN));

        assertThat(RandoDAO.getCursor(context, true).getCount()).isEqualTo(2);
        assertThat(RandoDAO.getCursor(context, false).getCount()).isEqualTo(4);

    }

    public static Rando getRandomRando(Rando.Status status) {

        Date userDate = new Date();
        userDate.setTime(new Date().getTime() + random.nextInt(1000000));

        Rando Rando = new Rando();
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
}
