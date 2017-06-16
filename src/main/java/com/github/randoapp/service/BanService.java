package com.github.randoapp.service;

import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanService {

    private static final Pattern banPattern = Pattern.compile("^Forbidden\\. Reset: (\\d+)$");
    private static final long SUSPEND_UPLOAD = 24 * 60 * 60 * 1000;


    public long parseResetTimeInBanMessage(String message) {
        if (message == null) {
            return 0L;
        }

        Log.d(BanService.class, "Forbidden message: ", message);
        Matcher matcher = banPattern.matcher(message);
        if (matcher.find()) {
            try {
                String banResetAt = matcher.group(1);
                Log.d(BanService.class, "Parsed banResetAt: ", banResetAt);
                return Long.parseLong(banResetAt);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    public void processForbiddenRequest(String message) {
        long resetBanAt = parseResetTimeInBanMessage(message);
        if (resetBanAt > 0L) {
            Preferences.setBanResetAt(resetBanAt);
        }
    }

    public void showBanMessageIfNeeded(final View banView) {
        if (banView == null) {
            return;
        }

        long banResetAt = Preferences.getBanResetAt();
        if (banResetAt > 0  && !isPermanentBan(banResetAt) && !isSuspendUploadAlreadySetup(banView)) {
            showSuspendUpload(banView, banResetAt);
        } else if (isPermanentBan(banResetAt)) {
            TextView contactUsButton = (TextView) banView.findViewWithTag("contactUsButton");
            if (contactUsButton != null) {
                contactUsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    new ContactUsService().openContactUsActivity(view.getContext());
                    }
                });
            }
            banView.setVisibility(View.VISIBLE);
        } else {
            banView.setVisibility(View.GONE);
        }
    }

    private boolean isPermanentBan(long banResetAt) {
        return banResetAt > 0 && (banResetAt - new Date().getTime()) > SUSPEND_UPLOAD;
    }

    private boolean isSuspendUploadAlreadySetup(final View banView) {
        return banView.getBackground() instanceof ColorDrawable &&
                ((ColorDrawable) banView.getBackground()).getColor() == banView.getContext().getResources().getColor(R.color.suspend_upload_label);
    }

    private void showSuspendUpload(final View banView, long banResetAt) {
        long now = new Date().getTime();
        if (banResetAt > now) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            CountDownTimer countDownTimer = new CountDownTimer(banResetAt - now, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String banMessage = banView.getContext().getString(R.string.upload_suspend) + " " + simpleDateFormat.format(millisUntilFinished);
                    TextView banTextView = (TextView) banView.findViewWithTag("banMessage");

                    if (banTextView != null) {
                        banTextView.setText(banMessage);
                    }
                }

                @Override
                public void onFinish() {
                    banView.setVisibility(View.GONE);
                }
            };

            hideViewByTags(banView, "contactUsButton", "banEmptyView", "banSeparatorLine");

            banView.setBackgroundColor(banView.getContext().getResources().getColor(R.color.suspend_upload_label));
            banView.setVisibility(View.VISIBLE);
            countDownTimer.start();
        }
    }

    private void hideViewByTags(View parentView, String... tags) {
        if (tags != null) {
            for (int i = 0; i < tags.length; i++) {
                View view = parentView.findViewWithTag(tags[i]);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
}
