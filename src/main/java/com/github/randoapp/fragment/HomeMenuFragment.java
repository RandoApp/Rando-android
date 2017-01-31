package com.github.randoapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.task.LogoutTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.view.Progress;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

import static com.github.randoapp.Constants.SYNC_BROADCAST_EVENT;

public class HomeMenuFragment extends Fragment {

    private TextView accountName;
    private FirebaseAnalytics mFirebaseAnalytics;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved Update request");
            if (SYNC_BROADCAST_EVENT.equals(intent.getAction())) {
                initAccountName();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.home_left_menu, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        rootView.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logLogout(mFirebaseAnalytics);
                Progress.show(getActivity().getResources().getString(R.string.logout_progress));
                new LogoutTask()
                        .onDone(new OnDone() {
                            @Override
                            public void onDone(Map<String, Object> data) {
                                Intent intent = new Intent(Constants.LOGOUT_BROADCAST_EVENT);
                                getContext().sendBroadcast(intent);
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

        rootView.findViewById(R.id.contactUsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("randoapp.me@gmail.com") +
                        "?subject=" + Uri.encode("Contact support. Account: " + Preferences.getAccount());
                Uri uri = Uri.parse(uriText);

                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Support Email"));
            }
        });

        accountName = (TextView) rootView.findViewById(R.id.accountName);
        initVersion(rootView);
        initHelp(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initAccountName();
        getActivity().registerReceiver(receiver, new IntentFilter(SYNC_BROADCAST_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    private void initVersion(View rootView) {
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

    private void initAccountName() {
        accountName.setText(getActivity().getString(R.string.account) + " " + Preferences.getAccount());
    }

    private void initHelp(View rootView) {
        //init Take section
        View view = rootView.findViewById(R.id.help_layout_take);
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_rando));
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_take_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_take_description));

        view = rootView.findViewById(R.id.help_layout_location);
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_globe));
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_location_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_location_description));

        view = rootView.findViewById(R.id.help_layout_delete);
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_trash_rando));
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_delete_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_delete_description));

        view = rootView.findViewById(R.id.help_layout_share);
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.ic_share_rando));
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_share_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_share_description));
    }
}