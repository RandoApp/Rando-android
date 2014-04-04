package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SendLogTask extends BaseTask {
    @Override
    public Integer run() {
        try {
            File logFile = new File(App.context.getExternalCacheDir(), Constants.LOG_FILE_NAME);
            FileReader fileReader = new FileReader(logFile);
            BufferedReader bufferedReader = new BufferedReader((fileReader));
            StringBuilder logs = new StringBuilder("{\"log\":\"");
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                logs.append(line);
            }
            logs.append("\"}");
            bufferedReader.close();
            logFile.delete();

            API.uploadLog(logs.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OK;
    }
}
