package com.eucsoft.foodex.task;

import android.os.Environment;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;

import java.io.File;

public class SendLogTask extends BaseTask {
    @Override
    public Integer run() {
        File logFile = new File(Environment.getExternalStorageDirectory(), Constants.LOG_FILE_NAME);
        try {
            API.uploadLog(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OK;
    }
}
