package com.eucsoft.foodex.log;

import com.eucsoft.foodex.task.StoreLogTask;
import com.eucsoft.foodex.task.callback.OnDone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorableLog {

    private static List<String> logs = new ArrayList<String>();

    public static void add(String log) {
        if (isNeedStore()) {
            new StoreLogTask(logs)
            .onDone(new OnDone() {
                @Override
                public void onDone(Map<String, Object> data) {
                    logs.clear();
                }
            })
            .execute();
        }

        logs.add(log);
    }

    private static boolean isNeedStore() {
        return logs.size() > 5000;
    }


}
