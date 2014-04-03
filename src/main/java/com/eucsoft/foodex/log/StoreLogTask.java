package com.eucsoft.foodex.log;

import android.os.Environment;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.task.BaseTask;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class StoreLogTask extends BaseTask {

    private List<String> logs;

    public StoreLogTask (List<String> logs) {
        this.logs = logs;
    }

    @Override
    public Integer run() {
        try {
            File logFile = new File(Environment.getExternalStorageDirectory(), Constants.LOG_FILE_NAME);
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(logs.toString());
            fileWriter.close();
        } catch (Exception e) {
            return ERROR;
        }

        return OK;
    }
}
