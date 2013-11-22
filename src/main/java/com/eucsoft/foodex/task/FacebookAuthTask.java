package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import java.util.HashMap;

public class FacebookAuthTask extends AsyncTask<Session, Integer, Long> implements BaseTask {

    private TaskResultListener taskResultListener;

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    public FacebookAuthTask(TaskResultListener taskResultListener) {
        this.taskResultListener = taskResultListener;
    }

    @Override
    protected Long doInBackground(Session ... params) {
        try {
            Session session = params[0];
            Request request = Request.newMeRequest(session, null);
            Response response = Request.executeAndWait(request);
            GraphObject user = response.getGraphObject();

            String id = (String) user.getProperty("id");
            String email = (String) user.getProperty("email");
            String token = session.getAccessToken();
            if (id != null && email != null && token != null) {
                API.facebook(id, email, token);
                return RESULT_OK;
            }
        } catch (Exception exc) {
            errors.put(Constants.ERROR, exc.getMessage());
        }
        return RESULT_ERROR;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(SignupTask.class, "onPostExecute", aLong.toString());
        taskResultListener.onTaskResult(0, aLong, errors);
    }
}
