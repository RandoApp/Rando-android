package com.eucsoft.foodex.fragment;

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
import android.widget.ImageButton;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.adapter.FoodPairsAdapter;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.notification.Notification;
import com.eucsoft.foodex.twowaygrid.TwoWayGridView;

import static com.eucsoft.foodex.Constants.*;

public class HomeWallFragment extends Fragment {

    private  FoodPairsAdapter foodPairsAdapter;

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
            foodPairsAdapter.notifyDataSetChanged();
        }

        private void showNotification() {
            foodPairsAdapter.notifyDataSetChanged();
            Notification.show("Foodex", "Your Foodex pictures got updated");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home, container, false);

        TwoWayGridView gridView = (TwoWayGridView) rootView.findViewById(R.id.main_grid);

        foodPairsAdapter = new FoodPairsAdapter(container.getContext());
        gridView.setAdapter(foodPairsAdapter);

        int delta;
        ImageButton takePictureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);
        int takePictureButtonHeight = takePictureButton.getHeight();

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setPadding(FOOD_PADDING_LANDSCAPE_COLUMN_LEFT, FOOD_PADDING_LANDSCAPE_COLUMN_TOP, FOOD_PADDING_LANDSCAPE_COLUMN_RIGHT, FOOD_PADDING_LANDSCAPE_COLUMN_BOTTOM);
            delta = takePictureButtonHeight;
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(1);
            gridView.setPadding(FOOD_PADDING_PORTRAIT_COLUMN_LEFT, FOOD_PADDING_PORTRAIT_COLUMN_TOP, FOOD_PADDING_PORTRAIT_COLUMN_RIGHT, FOOD_PADDING_PORTRAIT_COLUMN_BOTTOM);
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
