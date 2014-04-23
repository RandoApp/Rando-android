package com.github.randoapp.util;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.github.randoapp.App;
import com.google.android.gms.auth.GoogleAuthUtil;

public class AccountUtil {

    public static String[] getAccountNames() {
        AccountManager accountManager = AccountManager.get(App.context);
        Account[] accounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }
}
