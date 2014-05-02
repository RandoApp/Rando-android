package com.github.randoapp.task;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.task.callback.OnDone;

import org.apache.http.auth.AuthenticationException;

import java.util.Map;

public class BonAppetitTask extends BaseTask {
    public static final int TASK_ID = 300;

    private RandoPair randoPair;

    public BonAppetitTask(RandoPair randoPair) {
        this.randoPair = randoPair;
    }

    @Override
    public Integer run() {
        Log.d(BonAppetitTask.class, "Task start");

        if (randoPair == null) {
            return ERROR;
        }

        randoPair.stranger.bonAppetit = 1;

        data.put(Constants.RANDO_PAIR, randoPair);

        try {
            API.bonAppetit(String.valueOf(randoPair.stranger.randoId));
        } catch (AuthenticationException exc) {
            new LogoutTask()
                .onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {
                        FragmentManager fragmentManager = ((FragmentActivity) MainActivity.activity).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                    }
                })
                .execute();
            randoPair.stranger.bonAppetit = 0;
            return ERROR;
        } catch (Exception e) {
            Log.w(BonAppetitTask.class, "Failed to say Bon Appetit.");
            randoPair.stranger.bonAppetit = 0;
            return ERROR;
        }

        RandoDAO randoDAO = new RandoDAO(App.context);
        randoDAO.updateRandoPair(randoPair);
        randoDAO.close();

        return OK;
    }

}
