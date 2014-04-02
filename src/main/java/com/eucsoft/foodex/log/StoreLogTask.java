package com.eucsoft.foodex.log;

import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.util.FileUtil;

import java.io.File;

public class StoreLogTask extends BaseTask {

    @Override
    public Integer run() {
        String path = null;
        File file logFile = new File(path);

        return OK;
    }
}
