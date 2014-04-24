package com.github.randoapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.randoapp.R;
import com.github.randoapp.TakePictureActivity;
import com.github.randoapp.adapter.RandoPairsAdapter;
import com.github.randoapp.log.Log;
import com.github.randoapp.notification.Notification;
import com.jess.ui.TwoWayGridView;

import static com.github.randoapp.Constants.BON_APPETIT_BUTTON_SIZE;
import static com.github.randoapp.Constants.RANDO_PADDING_LANDSCAPE_COLUMN_BOTTOM;
import static com.github.randoapp.Constants.RANDO_PADDING_LANDSCAPE_COLUMN_LEFT;
import static com.github.randoapp.Constants.RANDO_PADDING_LANDSCAPE_COLUMN_RIGHT;
import static com.github.randoapp.Constants.RANDO_PADDING_LANDSCAPE_COLUMN_TOP;
import static com.github.randoapp.Constants.RANDO_PADDING_PORTRAIT_COLUMN_BOTTOM;
import static com.github.randoapp.Constants.RANDO_PADDING_PORTRAIT_COLUMN_LEFT;
import static com.github.randoapp.Constants.RANDO_PADDING_PORTRAIT_COLUMN_RIGHT;
import static com.github.randoapp.Constants.RANDO_PADDING_PORTRAIT_COLUMN_TOP;
import static com.github.randoapp.Constants.REPORT_BROADCAST;
import static com.github.randoapp.Constants.SYNC_SERVICE_BROADCAST;

public class HomeWallFragment extends Fragment {

    private RandoPairsAdapter randoPairsAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved Update request");
            if (REPORT_BROADCAST.equals(intent.getAction())) {
                toggleReportMode();
            } else if (SYNC_SERVICE_BROADCAST.equals(intent.getAction())) {
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

        int delta;
        ImageView takePictureButton = (ImageView) rootView.findViewById(R.id.camera_button);
        int takePictureButtonHeight = takePictureButton.getHeight();

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setPadding(RANDO_PADDING_LANDSCAPE_COLUMN_LEFT, RANDO_PADDING_LANDSCAPE_COLUMN_TOP, RANDO_PADDING_LANDSCAPE_COLUMN_RIGHT, RANDO_PADDING_LANDSCAPE_COLUMN_BOTTOM);
            delta = takePictureButtonHeight;
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(1);
            gridView.setPadding(RANDO_PADDING_PORTRAIT_COLUMN_LEFT, RANDO_PADDING_PORTRAIT_COLUMN_TOP, RANDO_PADDING_PORTRAIT_COLUMN_RIGHT, RANDO_PADDING_PORTRAIT_COLUMN_BOTTOM);
            delta = takePictureButtonHeight - BON_APPETIT_BUTTON_SIZE;
        }
        //TODO: delta is a height of space which should be added to the end of dataGrid to allow all buttons to be visible.
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), TakePictureActivity.class);
                startActivityForResult(intent, 100);
            }
        });
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
        getActivity().registerReceiver(receiver, new IntentFilter(SYNC_SERVICE_BROADCAST));
        getActivity().registerReceiver(receiver, new IntentFilter(REPORT_BROADCAST));
    }

}
