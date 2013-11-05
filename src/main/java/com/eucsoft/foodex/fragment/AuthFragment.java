package com.eucsoft.foodex.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.api.API;

public class AuthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.auth, container, false);

        TextView textViewSkipLink = (TextView) rootView.findViewById(R.id.textViewSkipLink);
        textViewSkipLink.setOnClickListener(new StubListener(rootView, this));

        Button facebookButton = (Button) rootView.findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(new StubListener(rootView, this));

        Button signupButton = (Button) rootView.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new StubListener(rootView, this));
        return rootView;
    }

    class StubListener implements View.OnClickListener {
        private View rootView;
        private AuthFragment authFragment;

        public StubListener(View rootView, AuthFragment authFragment) {
            this.rootView = rootView;
            this.authFragment = authFragment;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(authFragment)
                    .add(R.id.main_screen, new HomeWallFragment())
                    .commit();
        }
    }

}
