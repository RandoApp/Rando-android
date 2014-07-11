package com.github.randoapp.task;

import com.github.randoapp.api.API;
import com.github.randoapp.log.Log;

public class SendLogTask extends BaseTask {

    private String logs;
    public SendLogTask(String logs) {
        this.logs = logs;
    }

    @Override
    public Integer run() {
        try {
            StringBuilder sb = new StringBuilder("{\"log\":\"");
            sb.append(logs).append("\"}");
            API.uploadLog(sb.toString());
        } catch (Exception e) {
            Log.e(SendLogTask.class, "Can not send log, because: ", e.getMessage());
        }
        return OK;
    }
}
