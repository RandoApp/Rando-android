package com.github.randoapp.util;

import android.app.Activity;
import android.view.View;

import com.github.randoapp.R;

public class RandoUtil {

    public static void initMenuButton(View view, final Activity activity) {
        view.findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openOptionsMenu();
            }
        });
    }
}
