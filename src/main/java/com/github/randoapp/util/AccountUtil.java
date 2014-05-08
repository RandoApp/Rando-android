package com.github.randoapp.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.github.randoapp.App;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
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

    public static void updateTopPanel(final Fragment fragment) {
        try {
//            fragment.getView().findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    fragment.getActivity().openOptionsMenu();
//                }
//            });

            String name = fragment.getResources().getString(R.string.top_panel_prefix);
            String mode = fragment.getResources().getString(R.string.top_panel_postfix);
            PackageInfo packageInfo = fragment.getActivity().getPackageManager().getPackageInfo(fragment.getActivity().getPackageName(), 0);
            String version = packageInfo.versionName;
            String codeVersion = String.valueOf(packageInfo.versionCode);
            String topPanelText = name + " " + version + ":" + codeVersion + " " + mode;
            TextView topPanelTextView = (TextView) fragment.getView().findViewById(R.id.top_panel_text);
            topPanelTextView.setText(topPanelText);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MainActivity.class, "Can't init top panel, because: ", e.getMessage());
        }
    }
}
