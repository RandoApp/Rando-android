package com.eucsoft.foodex.auth;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.fragment.EmptyHomeWallFragment;
import com.eucsoft.foodex.service.SyncService;
import com.eucsoft.foodex.view.Progress;

import java.util.HashMap;

public abstract class BaseAuth implements View.OnClickListener {

    protected final AuthFragment authFragment;

    protected HashMap<String, Object> errors = new HashMap<String, Object>();

    public BaseAuth(AuthFragment authFragment) {
        this.authFragment = authFragment;
    }

    public static void done(FragmentActivity fragmentActivity) {
        Progress.hide();

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_screen, new EmptyHomeWallFragment()).commit();

        SyncService.run();
    }

}
