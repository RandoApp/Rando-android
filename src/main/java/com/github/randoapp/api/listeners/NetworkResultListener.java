package com.github.randoapp.api.listeners;

import android.content.Context;
import android.content.Intent;

import com.github.randoapp.AuthActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.beans.Error;

public abstract class NetworkResultListener {

    Context context;

    public NetworkResultListener(Context context) {
        this.context = context;
    }

    public abstract void onOk();

    public void onError(Error error) {
        if (error.code == R.string.error_400) {
            Intent intent = new Intent(context, AuthActivity.class);
            intent.putExtra(Constants.LOGOUT_ACTIVITY, true);
            context.startActivity(intent);
            return;
        }
        this.onFail(error);
    }

    protected abstract void onFail(Error error);
}
