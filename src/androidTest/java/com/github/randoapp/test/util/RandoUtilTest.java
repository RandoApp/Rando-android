package com.github.randoapp.test.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.db.RandoDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RandoUtilTest {

    private Context context;

    @Before
    public void setUp() {
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
    public void dummy(){
        
    }
}
