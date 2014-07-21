package com.github.randoapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.randoapp.CameraActivity;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.adapter.RandoPairsAdapter;
import com.github.randoapp.log.Log;
import com.github.randoapp.notification.Notification;
import com.github.randoapp.util.RandoUtil;
import com.jess.ui.TwoWayGridView;

import static com.github.randoapp.Constants.REPORT_BROADCAST;
import static com.github.randoapp.Constants.SYNC_SERVICE_BROADCAST_EVENT;

public class HomeWallFragment extends Fragment {

    private RandoPairsAdapter randoPairsAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved Update request");
            if (REPORT_BROADCAST.equals(intent.getAction())) {
                toggleReportMode();
            } else if (SYNC_SERVICE_BROADCAST_EVENT.equals(intent.getAction())) {
                showNotification();
            }
        }

        private void toggleReportMode() {
            randoPairsAdapter.notifyDataSetChanged();
        }

        private void showNotification() {
            randoPairsAdapter.notifyDataSetChanged();
            Notification.show("Rando", "Your rando got updated");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home, container, false);
        TwoWayGridView gridView = (TwoWayGridView) rootView.findViewById(R.id.main_grid);

        randoPairsAdapter = new RandoPairsAdapter(container.getContext());
        gridView.setAdapter(randoPairsAdapter);

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setPadding(getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_left),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_top),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_right),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_bottom));
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(1);
            gridView.setPadding(getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_top),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_right),
                    getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_bottom));
        }

        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), CameraActivity.class);
                startActivityForResult(intent, Constants.CAMERA_ACTIVITY_UPLOAD_PRESSED_REQUEST_CODE);
            }
        });

        RandoUtil.initMenuButton(rootView, getActivity());

        TextView topPanelText = (TextView) rootView.findViewById(R.id.top_panel_text);
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
        if (version != null && version.contains("b")) {
            topPanelText.setText(getText(R.string.app_name_official) + " (" + version + ")");
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        randoPairsAdapter.notifyDataSetChanged();
        getActivity().registerReceiver(receiver, new IntentFilter(SYNC_SERVICE_BROADCAST_EVENT));
        getActivity().registerReceiver(receiver, new IntentFilter(REPORT_BROADCAST));
    }

}
