package com.eucsoft.foodex.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.adapter.FoodPairsAdapter;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.SyncService;
import com.eucsoft.foodex.twowaygrid.TwoWayGridView;


public class HomeWallFragment extends Fragment {


    private FoodPairsAdapter foodPairsAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        int calls = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Service Call!! " + calls,
                    Toast.LENGTH_LONG).show();
            Log.i(BroadcastReceiver.class, "Recieved Update request.");
            foodPairsAdapter.notifyDataSetChanged();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(HomeWallFragment.this.getView().getContext())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Foodex")
                            .setContentText("Your Foodex pictures got updated.")
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSmallIcon(R.drawable.bonappetit2);

            Intent notificationIntent = new Intent(HomeWallFragment.this.getView().getContext(), MainActivity.class);

            PendingIntent contentIntent = PendingIntent.getActivity(HomeWallFragment.this.getView().getContext(), 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(contentIntent);
            builder.setAutoCancel(true);
            builder.setLights(Color.BLUE, 500, 500);
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
            builder.setVibrate(pattern);
            builder.setStyle(new NotificationCompat.InboxStyle());

            NotificationManager manager = (NotificationManager) HomeWallFragment.this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
            calls++;
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
            gridView.setPadding(Constants.FOOD_PADDING_LANDSCAPE_COLUMN_LEFT, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_TOP, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_RIGHT, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_BOTTOM);
            delta = takePictureButtonHeight;
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(1);
            gridView.setPadding(Constants.FOOD_PADDING_PORTRAIT_COLUMN_LEFT, Constants.FOOD_PADDING_PORTRAIT_COLUMN_TOP, Constants.FOOD_PADDING_PORTRAIT_COLUMN_RIGHT, Constants.FOOD_PADDING_PORTRAIT_COLUMN_BOTTOM);
            delta = takePictureButtonHeight - Constants.BON_APPETIT_BUTTON_SIZE;
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
        getActivity().registerReceiver(receiver, new IntentFilter(SyncService.NOTIFICATION));
    }
}
