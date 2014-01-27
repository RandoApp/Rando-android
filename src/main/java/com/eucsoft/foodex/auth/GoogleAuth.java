package com.eucsoft.foodex.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.task.GoogleAuthTask;
import com.eucsoft.foodex.task.callback.OnError;
import com.eucsoft.foodex.task.callback.OnOk;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.Map;

public class GoogleAuth extends BaseAuth {

    public GoogleAuth(AuthFragment authFragment) {
        super(authFragment);
    }

    @Override
    public void onClick(View v) {
        String[] names = getAccountNames();
        if (names.length == 1) {
            fetchUserToken(names[0]);
        } else if (names.length > 1) {
            selectAccount(names);
        } else if (names.length == 0) {
            Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.no_google_account), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchUserToken(String email) {
        new GoogleAuthTask(email)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    String email = (String) data.get("email");
                    String token = (String) data.get("token");
                    if (email != null && token != null) {
                        try {
                            API.google(email, token);
                        } catch (Exception e) {
                            Toast.makeText(authFragment.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            })
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    String error = (String) data.get("error");
                    if (error != null) {
                        Toast.makeText(authFragment.getActivity(), error, Toast.LENGTH_LONG).show();
                    }
                }
            })
        .execute();
    }

    private String[] getAccountNames() {
        AccountManager mAccountManager = AccountManager.get(App.context);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }

    private void selectAccount(final CharSequence[] names) {
        AlertDialog.Builder builder = new AlertDialog.Builder(authFragment.getActivity());
        builder.setTitle(authFragment.getString(R.string.select_account));
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchUserToken((String) names[which]);
            }
        });
        builder.show();
    }
}
