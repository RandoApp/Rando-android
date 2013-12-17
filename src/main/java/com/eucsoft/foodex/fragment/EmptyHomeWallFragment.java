package com.eucsoft.foodex.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.log.Log;
import com.eucsoft.foodex.service.SyncService;

public class EmptyHomeWallFragment extends Fragment {

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(android.content.BroadcastReceiver.class, "Recieved Update request.");
            FoodDAO foodDAO = new FoodDAO(App.context);
            if (foodDAO.getFoodPairsNumber() > 0) {
                FragmentManager fragmentManager = ((ActionBarActivity) getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, new HomeWallFragment()).commit();
            }
            foodDAO.close();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.empty_home, container, false);
        ImageButton takePictureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);

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
