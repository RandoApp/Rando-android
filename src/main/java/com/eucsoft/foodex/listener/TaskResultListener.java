package com.eucsoft.foodex.listener;

import java.util.HashMap;

public interface TaskResultListener {

    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data);

}
