package com.github.randoapp.service;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;

import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.preferences.Preferences;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class TutorialService {

    private static final int delay = 3000;

    public void showStep1OpenCamera(final Activity activity) {

        activity.findViewById(R.id.colums_pager);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                      new FancyShowCaseView.Builder(activity)
                              .focusOn(activity.findViewById(R.id.camera_button))
                              .title("Tap to rando button for taking your first rando")
                              .focusCircleRadiusFactor(1.5)
                              .backgroundColor(Color.parseColor("#ccf87b00"))
                              .build()
                              .show();
                  }
              },
                        delay);
            }
        });

    }

}
