package com.github.randoapp.log;

import com.github.randoapp.task.StoreLogTask;

import java.util.LinkedList;
import java.util.List;

import static com.github.randoapp.Constants.LOG_BUFFER_SIZE;

public class StorableLog {

    private static List<String> logs = new LinkedList<String>();

    public static void add(String log) {
        if (isNeedStore()) {
            String logsStr = logs.toString();
            logs.clear();
            new StoreLogTask(logsStr).execute();
        }

        logs.add(log);
    }

    private static boolean isNeedStore() {
        return logs.size() > LOG_BUFFER_SIZE;
    }


}
