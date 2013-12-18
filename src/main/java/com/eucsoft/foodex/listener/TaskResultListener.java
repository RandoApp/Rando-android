package com.eucsoft.foodex.listener;

import java.util.Map;

public interface TaskResultListener {

    public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data);

}
