package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTask2 extends AsyncTask<Void, Integer, Integer> {

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_CANCELLED = 2;
    private Map<String, Object> data = new HashMap<String, Object>();

    public abstract Integer run();

    private OnOk okCallback;
    private OnError errorCallback;


    public BaseTask2 onOk(OnOk callback) {
        this.okCallback = callback;
        return this;
    }

    public BaseTask2 onError(OnError callback) {
        this.errorCallback = callback;
        return this;
    }

    protected Integer done(Object ... data) {
        this.data = null;
        return RESULT_OK;
    }

    protected Integer ok(Object ... data) {
        fillData(data);
        return RESULT_OK;
    }

    protected Integer error(Object ... data) {
        fillData(data);
        return RESULT_ERROR;
    }

    private void fillData(Object ... data) {
        for (int i = 0; i < data.length; i += 2) {
            this.data.put((String) data[i], data[i+1]);
        }
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
        if (result == RESULT_OK && okCallback != null && data != null) {
            okCallback.onOk(data);
        } else if (result == RESULT_ERROR && errorCallback != null && data != null) {
            errorCallback.onError(data);
        }
    }

}
