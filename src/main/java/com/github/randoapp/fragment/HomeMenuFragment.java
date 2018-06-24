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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.randoapp.AuthActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.BanService;
import com.github.randoapp.service.ContactUsService;

import static com.github.randoapp.Constants.SYNC_BROADCAST_EVENT;

public class HomeMenuFragment extends Fragment {

    private TextView accountName;

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

        rootView.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.LOGOUT_ACTIVITY, true);
                startActivity(intent);
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

        rootView.findViewById(R.id.policyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.PRIVACY_POLICY_URL));
                startActivity(i);
            }
        });

        rootView.findViewById(R.id.contactUsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactUsService().openContactUsActivity(getActivity());
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
        new BanService().showBanMessageIfNeeded(getActivity().findViewById(R.id.banLabel));
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
        accountName.setText(getActivity().getString(R.string.account) + " " + Preferences.getAccount(getContext()));
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
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageResource(R.drawable.ic_delete_black_24dp);
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_delete_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_delete_description));

        view = rootView.findViewById(R.id.help_layout_share);
        ((ImageView) view.findViewById(R.id.help_section_imageview_icon)).setImageResource(R.drawable.ic_share_black_24dp);
        ((TextView) view.findViewById(R.id.help_section_textview_title)).setText(rootView.getContext().getResources().getString(R.string.help_share_title));
        ((TextView) view.findViewById(R.id.help_section_textview_description)).setText(rootView.getContext().getResources().getString(R.string.help_share_description));
    }
}
