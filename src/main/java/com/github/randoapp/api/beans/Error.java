package com.github.randoapp.api.beans;

import android.content.Context;

public class Error {
    public String message;
    public int code;

    public Error setMessage(String message) {
        this.message = message;
        return this;
    }

    public Error setCode(int code) {
        this.code = code;
        return this;
    }

    public String buildMessage(Context context) {
        String finalMessage = "";
        if (this.code != 0) {
            finalMessage = context.getString(code);
        }

        if (this.message != null) {
            finalMessage += " " + this.message;
        }
        return finalMessage;
    }
}
