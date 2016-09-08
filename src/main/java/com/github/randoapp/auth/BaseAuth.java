package com.github.randoapp.auth;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.fragment.EmptyHomeWallFragment;
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

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, new EmptyHomeWallFragment()).commit();

        API.syncUserAsync(null);
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
