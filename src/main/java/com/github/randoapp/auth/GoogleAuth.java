package com.github.randoapp.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.randoapp.R;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.task.GoogleAuthTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.view.Progress;

import java.util.Map;

public class GoogleAuth extends BaseAuth implements View.OnTouchListener {

    private Button googleButton;

    public GoogleAuth(AuthFragment authFragment, Button googleButton) {
        super(authFragment);
        this.googleButton = googleButton;
    }

    private void setButtonFocused() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_signin_btn_text_focus_dark));
    }

    private void setButtonPressed() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_signin_btn_text_pressed_dark));
    }

    private void setButtonNormal() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_signin_btn_text_normal_dark));
    }

    @Override
    public void onClick(View v) {
        String[] names = AccountUtil.getAccountNames();
        if (names.length == 1) {
            fetchUserToken(names[0]);
        } else if (names.length > 1) {
            selectAccount(names);
        } else if (names.length == 0) {
            Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.no_google_account), Toast.LENGTH_LONG).show();
            setButtonNormal();
        }
    }

    private void fetchUserToken(String email) {
        Progress.showLoading();
        new GoogleAuthTask(email)
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
