package com.github.randoapp.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;

public class ContactUsService {

    public void openContactUsActivity(final Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        String versionName = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(ContactUsService.class, "Failed to get version name", e);
        }

        String email = context.getString(R.string.contact_us_email);
        String subject = String.format(context.getString(R.string.contact_us_subject), versionName);
        String body = String.format(context.getString(R.string.contact_us_body), Preferences.getAccount(context));

        String uriText = "mailto:" + Uri.encode(email) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Uri uri = Uri.parse(uriText);

        intent.setData(uri);
        context.startActivity(Intent.createChooser(intent, "Send Support Email"));
    }
}
