package com.github.randoapp.auth;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.view.Progress;

public abstract class BaseAuth implements View.OnClickListener {

    protected final AuthFragment authFragment;

    public BaseAuth(AuthFragment authFragment) {
        this.authFragment = authFragment;
    }

    public static void done(FragmentActivity fragmentActivity) {
        Progress.hide();

        hideSoftKeyboard(fragmentActivity);
        clearDBForChangeAccount();

        API.syncUserAsync(null, null);
        Intent intent = new Intent(Constants.AUTH_SUCCCESS_BROADCAST_EVENT);
        if (fragmentActivity != null) {
            fragmentActivity.sendBroadcast(intent);
        }
    }

    private static void hideSoftKeyboard(FragmentActivity fragmentActivity) {
        if (fragmentActivity != null) {
            InputMethodManager inputManager = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View textFieldWithSoftKeyboard = fragmentActivity.getCurrentFocus();
            if (textFieldWithSoftKeyboard != null) {
                inputManager.hideSoftInputFromWindow(textFieldWithSoftKeyboard.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private static void clearDBForChangeAccount() {
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
    }

}
