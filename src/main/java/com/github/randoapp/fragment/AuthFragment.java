package com.github.randoapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.auth.EmailAndPasswordAuth;
import com.github.randoapp.auth.GoogleAuth;
import com.github.randoapp.auth.SkipAuth;
import com.github.randoapp.util.AccountUtil;

public class AuthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auth, container, false);

        TextView textViewSkipLink = (TextView) rootView.findViewById(R.id.textViewSkipLink);
        textViewSkipLink.setOnClickListener(new SkipAuth(this));

        Button signupButton = (Button) rootView.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new EmailAndPasswordAuth(rootView, this));

        createGoogleAuthButton(rootView);

        AccountUtil.updateTopPanel(this);

        return rootView;
    }

    private void createGoogleAuthButton(View rootView) {
        Button googleButton = (Button) rootView.findViewById(R.id.googleAuthButton);
        googleButton.setBackgroundDrawable(getResources().getDrawable(com.google.android.gms.R.drawable.common_signin_btn_text_normal_dark));
        googleButton.setText(getResources().getString(com.google.android.gms.R.string.common_signin_button_text_long));

        GoogleAuth googleAuthListener = new GoogleAuth(this, googleButton);
        googleButton.setOnTouchListener(googleAuthListener);
        googleButton.setOnClickListener(googleAuthListener);
    }

}
