package com.eucsoft.foodex.auth;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.HomeWallFragment;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.view.Progress;

import java.util.HashMap;

public abstract class BaseAuth implements View.OnClickListener, TaskResultListener {

    protected final AuthFragment authFragment;

    protected HashMap<String, Object> errors = new HashMap<String, Object>();

    public BaseAuth(AuthFragment authFragment) {
        this.authFragment = authFragment;
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
        if (resultCode == BaseTask.RESULT_ERROR) {
            if (data.get("error") != null) {
                Toast.makeText(authFragment.getActivity(), (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
            }
            return;
        }

        done();
    }

    public void done() {
        Progress.hide();
        FragmentManager fragmentManager = authFragment.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
    }

}
