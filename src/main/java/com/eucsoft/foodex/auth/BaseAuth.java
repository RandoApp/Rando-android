package com.eucsoft.foodex.auth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.EmptyHomeWallFragment;
import com.eucsoft.foodex.fragment.HomeWallFragment;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.service.SyncService;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.view.Progress;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseAuth implements View.OnClickListener, TaskResultListener {

    protected final AuthFragment authFragment;

    protected HashMap<String, Object> errors = new HashMap<String, Object>();

    public BaseAuth(AuthFragment authFragment) {
        this.authFragment = authFragment;
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data) {
        if (resultCode == BaseTask.RESULT_ERROR) {
            if (data.get(Constants.ERROR) != null) {
                Toast.makeText(authFragment.getActivity(), (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
            }
            return;
        }

        done();
    }

    public void done() {
        Progress.hide();
        FragmentManager fragmentManager = authFragment.getActivity().getSupportFragmentManager();
        Fragment nextFragment;

        FoodDAO foodDAO = new FoodDAO(authFragment.getActivity().getApplicationContext());
        int foodCount = foodDAO.getFoodPairsNumber();
        foodDAO.close();

        if (foodCount == 0) {
            nextFragment = new EmptyHomeWallFragment();
        } else {
            nextFragment = new HomeWallFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.main_screen, nextFragment).commit();

        SyncService.run();
    }

}
