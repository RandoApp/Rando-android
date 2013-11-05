package com.eucsoft.foodex.callback;

import java.util.HashMap;

public interface TaskCallback {

    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data);

}
