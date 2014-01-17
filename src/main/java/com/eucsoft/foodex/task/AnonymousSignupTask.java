package com.eucsoft.foodex.task;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;

public class AnonymousSignupTask extends BaseTask {

    private String uuid;

    public AnonymousSignupTask(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Integer run() {
        try {
            API.anonymous(uuid);
            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
        }
    }

}
