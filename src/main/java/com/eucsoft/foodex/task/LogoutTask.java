package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.preferences.Preferences;
import com.facebook.Session;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;

public class LogoutTask extends AsyncTask<Void, Integer, Long> implements BaseTask   {

    private TaskResultListener taskResultListener;

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    public LogoutTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(Void... params) {
        try {
            if (Session.getActiveSession() != null) {
                Session.getActiveSession().closeAndClearTokenInformation();
            } else {
                Session session = Session.openActiveSessionFromCache(MainActivity.context);
                if (session != null) {
                    session.closeAndClearTokenInformation();
                }
            }

            Preferences.removeSessionCookie();

            API.logout();
            ((DefaultHttpClient) API.client).getCookieStore().clear();
            return RESULT_OK;
        } catch (Exception exc) {
            errors.put(Constants.ERROR, exc.getMessage());
            return RESULT_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(LogoutTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, errors);
    }
}
