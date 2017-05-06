package com.github.randoapp.api.callback;

import com.github.randoapp.api.beans.User;

public interface OnFetchUser {
    void onFetch(User user);
}
