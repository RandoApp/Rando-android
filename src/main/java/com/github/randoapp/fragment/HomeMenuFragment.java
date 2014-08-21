package com.github.randoapp.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.task.LogoutTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.view.Progress;

import java.util.Map;

public class HomeMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.home_left_menu, container, false);

        rootView.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Progress.show(App.context.getResources().getString(R.string.logout_progress));
                new LogoutTask()
                        .onDone(new OnDone() {
                            @Override
                            public void onDone(Map<String, Object> data) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                                Progress.hide();
                            }
                        })
                        .execute();
            }
        });

        rootView.findViewById(R.id.aboutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.SERVER_URL));
                startActivity(i);
            }
        });

        initVersion(rootView);
        initAccountName(rootView);
        initHelp(rootView);

        return rootView;
    }


    private void  initVersion(View rootView){
        TextView versionText = (TextView) rootView.findViewById(R.id.app_version);
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ex) {

        }
        String version = "";
        if (info != null) {
            version = info.versionName;
        }
        versionText.setText(versionText.getText() + " " + version);
    }

    private void initAccountName(View rootView){
        TextView accountName = (TextView) rootView.findViewById(R.id.accountName);
        accountName.setText(accountName.getText() + " " + Preferences.getAccount());
    }

    private void initHelp(View rootView){
        //init Take section
        View view = rootView.findViewById(R.id.help_layout_take);
        ((ImageView)view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_launcher));
        ((TextView)view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_take_title));
        ((TextView)view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_take_description));

        view = rootView.findViewById(R.id.help_layout_location);
       ((ImageView)view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_globe));
        ((TextView)view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_location_title));
        ((TextView)view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_location_description));
    }
}