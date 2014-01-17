package com.eucsoft.foodex.auth;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.EmptyHomeWallFragment;
import com.eucsoft.foodex.service.SyncService;
import com.eucsoft.foodex.view.Progress;

public abstract class BaseAuth implements View.OnClickListener {

    protected final AuthFragment authFragment;

    public BaseAuth(AuthFragment authFragment) {
        this.authFragment = authFragment;
    }

    public static void done(FragmentActivity fragmentActivity) {
        Progress.hide();

        hideSoftKeyboard(fragmentActivity);
        clearDBForChangeAccount(fragmentActivity.getApplicationContext());

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, new EmptyHomeWallFragment()).commit();

        SyncService.run();
    }

    private static void hideSoftKeyboard(FragmentActivity fragmentActivit) {
        InputMethodManager inputManager = (InputMethodManager) fragmentActivit.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(fragmentActivit.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static void clearDBForChangeAccount(Context context) {
        FoodDAO foodDAO = new FoodDAO(context);
        foodDAO.clearFoodPairs();
        foodDAO.close();
    }

}
