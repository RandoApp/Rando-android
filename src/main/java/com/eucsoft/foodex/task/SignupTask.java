package com.eucsoft.foodex.task;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;

public class SignupTask extends BaseTask {

    private String email;
    private String password;

    public SignupTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Integer run() {
        try {
            API.signup(email, password);
            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
        }
    }

}
