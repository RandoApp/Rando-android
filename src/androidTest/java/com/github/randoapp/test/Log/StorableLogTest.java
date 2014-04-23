package com.github.randoapp.test.Log;

import android.test.AndroidTestCase;

import com.github.randoapp.log.Log;
import com.github.randoapp.task.SendLogTask;

public class StorableLogTest extends AndroidTestCase {

    public void testLog() {
        for (int i = 0; i < 3000; i++) {
            Log.w(StorableLogTest.class, "Ver long message Ver long message Ver long message Ver long message Ver long message " +
                    "vVer long message Ver long message Ver long message Ver long message " +
                    "Ver long message Ver long message Ver long message Ver long message Ver long message " +
                    "Ver long message Ver long message Ver long message " +
                    "Ver long message Ver long message " + i);
        }

        new SendLogTask().executeSync();
    }
}
