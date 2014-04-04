package com.eucsoft.foodex.log;

import com.eucsoft.foodex.task.StoreLogTask;

import java.util.LinkedList;
import java.util.List;

public class StorableLog {

    private static List<String> logs = new LinkedList<String>();

    private static final int LOG_SIZE = 100000;

    public static void add(String log) {
        if (isNeedStore()) {
            String logsStr = logs.toString();
            logs.clear();
            new StoreLogTask(logsStr).execute();
        }

        logs.add(log);
    }

    private static boolean isNeedStore() {
        return logs.size() > LOG_SIZE;
    }


}
