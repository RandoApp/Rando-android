package com.github.randoapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.adapter.RandoPairsAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.log.Log;
import com.github.randoapp.notification.Notification;

import org.json.JSONObject;

import static com.github.randoapp.Constants.REPORT_BROADCAST;
import static com.github.randoapp.Constants.SYNC_BROADCAST_EVENT;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_BROADCAST_EVENT;

public class HomeListFragment extends Fragment {

    private RandoPairsAdapter randoPairsAdapter;

    private boolean isStranger;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved Update request");
            if (REPORT_BROADCAST.equals(intent.getAction())) {
                toggleReportMode();
            } else if (SYNC_BROADCAST_EVENT.equals(intent.getAction())) {
                showNotification();
            } else if (UPLOAD_SERVICE_BROADCAST_EVENT.equals(intent.getAction())){
                randoPairsAdapter.notifyDataSetChanged();
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
                             final Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            isStranger = bundle.getInt(Constants.PAGE) == 1;
        }

        final View rootView;
        rootView = inflater.inflate(R.layout.home_list, container, false);

        ImageView icHome = (ImageView) rootView.findViewById(R.id.ic_home);
        if (isStranger){
            icHome.setVisibility(View.GONE);
        } else {
            icHome.setVisibility(View.VISIBLE);
        }

        final ListView listView = (ListView) rootView.findViewById(R.id.listView);
        randoPairsAdapter = new RandoPairsAdapter(isStranger);
        listView.setAdapter(randoPairsAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                API.syncUserAsync(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
        randoPairsAdapter.notifyDataSetChanged();
        getActivity().registerReceiver(receiver, new IntentFilter(SYNC_BROADCAST_EVENT));
        getActivity().registerReceiver(receiver, new IntentFilter(REPORT_BROADCAST));
        getActivity().registerReceiver(receiver, new IntentFilter(UPLOAD_SERVICE_BROADCAST_EVENT));
    }

}
