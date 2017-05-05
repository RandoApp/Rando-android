package com.github.randoapp.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.github.randoapp.AuthActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.task.GoogleAuthTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.PermissionUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class GoogleAuthService extends BaseAuthService {

    public GoogleAuthService(Activity activity) {
        super(activity);
    }

    public void process() {
        Analytics.logLoginGoogle(FirebaseAnalytics.getInstance(activity));
        if (!PermissionUtils.checkAndRequestMissingPermissions(activity, Constants.CONTACTS_PERMISSION_REQUEST_CODE, android.Manifest.permission.GET_ACCOUNTS)) {
            String[] names = AccountUtil.getAccountNames();
            if (names.length == 1) {
                fetchUserToken(names[0]);
            } else if (names.length > 1) {
                selectAccount(names);
            } else if (names.length == 0) {
                Toast.makeText(activity, activity.getString(R.string.no_google_account), Toast.LENGTH_LONG).show();
                setButtonNormal();
            }
        } else {
            ((AuthActivity) activity).isGoogleLoginPressed = true;
        }
    }

    private void fetchUserToken(String email) {
        showLoginProgress();
        new GoogleAuthTask(email, activity)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        done();
                    }
                })
                .onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                        hideLoginProgress();
                        String error = (String) data.get("error");
                        if (error != null) {
                            Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder(activity);
        selectAccountDialog.setTitle(activity.getString(R.string.select_account));
        selectAccountDialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchUserToken((String) names[which]);
            }
        });
        selectAccountDialog.show();
    }

    private void setButtonNormal() {
        activity.findViewById(R.id.googleAuthButton).setBackgroundDrawable(activity.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
    }
}
