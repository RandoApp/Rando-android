package com.github.randoapp.view;

import android.content.Context;
import android.widget.RelativeLayout;

import com.github.randoapp.R;

public class RandoActionsView extends RelativeLayout {
    public RandoActionsView(Context context) {
        super(context);
        inflate(context, R.layout.rando_actions_layer, this);
    }
}
