package com.github.randoapp.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.task.GoogleAuthTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.Progress;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class GoogleAuth extends BaseAuth implements View.OnTouchListener {

    private Button googleButton;
    private AuthFragment authFragment;

    public GoogleAuth(@NonNull AuthFragment authFragment, Button googleButton) {
        super(authFragment);
        this.authFragment = authFragment;
        this.googleButton = googleButton;
    }

    private void setButtonFocused() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_focused));
    }

    private void setButtonPressed() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_pressed));
    }

    private void setButtonNormal() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
    }

    @Override
    public void onClick(View v) {
        Analytics.logLoginGoogle(FirebaseAnalytics.getInstance(authFragment.getActivity()));
        if (!PermissionUtils.checkAndRequestMissingPermissions(authFragment.getActivity(), Constants.CONTACTS_PERMISSION_REQUEST_CODE, android.Manifest.permission.GET_ACCOUNTS)) {
            String[] names = AccountUtil.getAccountNames();
            if (names.length == 1) {
                fetchUserToken(names[0]);
            } else if (names.length > 1) {
                selectAccount(names);
            } else if (names.length == 0) {
                Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.no_google_account), Toast.LENGTH_LONG).show();
                setButtonNormal();
            }
        } else {
            authFragment.isGoogleLoginPressed = true;
        }
    }

    private void fetchUserToken(String email) {
        Progress.showLoading();
        new GoogleAuthTask(email, authFragment)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        done(authFragment.getActivity());
                    }
                })
                .onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                        Progress.hide();
                        String error = (String) data.get("error");
                        if (error != null) {
                            Toast.makeText(authFragment.getActivity(), error, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .onDone(new OnDone() {
                    @Override
                    public void onDone(Map<String, Object> data) {
                        setButtonNormal();
                    }
                })
                .execute();
    }

    private void selectAccount(final CharSequence[] names) {
        AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder(authFragment.getActivity());
        selectAccountDialog.setTitle(authFragment.getString(R.string.select_account));
        selectAccountDialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchUserToken((String) names[which]);
            }
        });
        selectAccountDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setButtonPressed();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            setButtonNormal();
        } else {
            setButtonFocused();
        }
        return false;
    }
}
