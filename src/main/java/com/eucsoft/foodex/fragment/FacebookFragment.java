package com.eucsoft.foodex.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.FacebookAuthTask;
import com.eucsoft.foodex.view.Progress;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;
import java.util.HashMap;

public class FacebookFragment extends Fragment {

    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook, container, false);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebookButton);
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.setFragment(this);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        uiHelper.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(final Session session, SessionState state) {
        if (state.isOpened()) {
            Progress.showLoading();
            new FacebookAuthTask(new TaskResultListener() {
                @Override
                public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
                    Progress.hide();
                    if (resultCode == BaseTask.RESULT_OK) {
                        done();
                    }
                }
            }).execute(session);
            Progress.showLoading();
        } else if (state.isClosed()) {
            Log.i(FacebookFragment.class, "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state);
        }
    };

    public void done() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment nextFragment;
        FoodDAO foodDAO = new FoodDAO(getActivity().getApplicationContext());
        int foodCount = foodDAO.getFoodPairsNumber();
        foodDAO.close();
        if (foodCount == 0) {
            nextFragment = new EmptyHomeWallFragment();
        } else {
            nextFragment = new HomeWallFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.main_screen, nextFragment).commit();
    }

}
