package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.log.Log;

import java.io.File;
import java.io.FileWriter;

public class StoreLogTask extends BaseTask {

    String logs;

    public StoreLogTask (String logs) {
        this.logs = logs;
    }

    @Override
    public Integer run() {
        try {
            Log.d(StoreLogTask.class, "Try read log file");
            File logFile = new File(App.context.getExternalCacheDir(), Constants.LOG_FILE_NAME);
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(logs);
            Log.d(StoreLogTask.class, "Log file read");
            fileWriter.close();
        } catch (Exception e) {
            Log.e(StoreLogTask.class, "Can not read log file, because: ", e.getMessage());
            return ERROR;
        }

        return OK;
    }
}
