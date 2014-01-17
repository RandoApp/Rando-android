package com.eucsoft.foodex.task;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class FacebookAuthTask extends BaseTask {

    private Session session;

    public FacebookAuthTask(Session session) {
        this.session = session;
    }

    @Override
    public Integer run() {
        try {
            Request request = Request.newMeRequest(session, null);
            Response response = Request.executeAndWait(request);
            GraphObject user = response.getGraphObject();

            String id = (String) user.getProperty("id");
            String email = (String) user.getProperty("email");
            String token = session.getAccessToken();
            if (id != null && email != null && token != null) {
                API.facebook(id, email, token);
                return OK;
            }
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
        }
        return ERROR;
    }
}
