package com.github.randoapp.task;

import android.os.AsyncTask;

import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTask extends AsyncTask<Void, Integer, Integer> {

    public static final int OK = 0;
    public static final int ERROR = 1;
    protected Map<String, Object> data = new HashMap<String, Object>();

    public abstract Integer run();

    private OnDone doneCallback;
    private OnOk okCallback;
    private OnError errorCallback;

    public BaseTask onDone(OnDone callback) {
        this.doneCallback = callback;
        return this;
    }

    public BaseTask onOk(OnOk callback) {
        this.okCallback = callback;
        return this;
    }

    public BaseTask onError(OnError callback) {
        this.errorCallback = callback;
        return this;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return run();
    }

    @Override
    protected void onPostExecute(Integer result) {
        triggerCallback(result);

    }

    //Use executeSync method only in tests, if you need verify task results in callback!
    public void executeSync() {
        int result = run();
        triggerCallback(result);
    }

    protected void triggerCallback(int result) {
        if (result == OK && okCallback != null) {
            okCallback.onOk(data);
        } else if (result == ERROR && errorCallback != null) {
            errorCallback.onError(data);
        }

        if (doneCallback != null) {
            doneCallback.onDone(data);
        }
    }

}
