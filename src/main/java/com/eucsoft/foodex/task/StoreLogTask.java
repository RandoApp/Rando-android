package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;

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
            File logFile = new File(App.context.getExternalCacheDir(), Constants.LOG_FILE_NAME);
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(logs);
            fileWriter.close();
        } catch (Exception e) {
            return ERROR;
        }

        return OK;
    }
}
