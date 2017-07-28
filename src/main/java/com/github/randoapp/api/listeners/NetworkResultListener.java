package com.github.randoapp.api.listeners;

import com.github.randoapp.api.beans.Error;

public interface NetworkResultListener {

    void onOk();

    void onError(Error error);
}
