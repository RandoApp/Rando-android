package com.github.randoapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.adapter.RandoListAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.ErrorResponseListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.upload.UploadJobScheduler;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.GooglePlayServicesUtil;
import com.github.randoapp.util.NetworkUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import static com.github.randoapp.Constants.PUSH_NOTIFICATION_BROADCAST_EVENT;
import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.STATISTICS_PARAM;
import static com.github.randoapp.Constants.SYNC_BROADCAST_EVENT;
import static com.github.randoapp.Constants.SYNC_STATISTICS_EVENT;
import static com.github.randoapp.Constants.UPLOAD_SERVICE_BROADCAST_EVENT;

public class HomeListFragment extends Fragment {

    private RandoListAdapter randoPairsAdapter;
    private boolean isStranger;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Rando scrollToRando;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(BroadcastReceiver.class, "Recieved Update request");
            if (SYNC_BROADCAST_EVENT.equals(intent.getAction())) {
                randoPairsAdapter.changeCursor(RandoDAO.getCursor(context, isStranger));
                randoPairsAdapter.notifyDataSetChanged();
            } else if (UPLOAD_SERVICE_BROADCAST_EVENT.equals(intent.getAction())) {
                randoPairsAdapter.changeCursor(RandoDAO.getCursor(context, isStranger));
                randoPairsAdapter.notifyDataSetChanged();
            } else if (SYNC_STATISTICS_EVENT.equals(intent.getAction())) {
                randoPairsAdapter.notifyItemChanged(0);
            } else if (PUSH_NOTIFICATION_BROADCAST_EVENT.equals(intent.getAction())) {
                String randoId = intent.getStringExtra(RANDO_ID_PARAM);
                if (randoId != null && !randoPairsAdapter.isStranger()) {
                    randoPairsAdapter.changeCursor(RandoDAO.getCursor(context, isStranger));
                    randoPairsAdapter.notifyDataSetChanged();
                    if (intent.getBooleanExtra(STATISTICS_PARAM, false)) {
                        API.statistics(getContext(), null);
                    }
                } else {
                    randoPairsAdapter.changeCursor(RandoDAO.getCursor(context, isStranger));
                    randoPairsAdapter.notifyDataSetChanged();

                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            isStranger = bundle.getInt(Constants.PAGE) == 1;
            scrollToRando = (Rando) bundle.getSerializable(Constants.RANDO_PARAM);
        }

        final View rootView;
        rootView = inflater.inflate(R.layout.home_list, container, false);

        ImageView icHome = (ImageView) rootView.findViewById(R.id.ic_home);
        if (isStranger) {
            icHome.setVisibility(View.GONE);
        } else {
            icHome.setVisibility(View.VISIBLE);
        }

        RecyclerView listView = rootView.findViewById(R.id.listView);
        listView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listView.setLayoutManager(llm);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        randoPairsAdapter = new RandoListAdapter(getContext(), isStranger, mFirebaseAnalytics);
        randoPairsAdapter.setHasStableIds(true);

        listView.setAdapter(randoPairsAdapter);
        API.statistics(getContext(), null);

        //ToDo: fix position of rando
        if (scrollToRando != null) {
            listView.getLayoutManager().scrollToPosition(randoPairsAdapter.findElementById(scrollToRando.randoId));
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Analytics.logForceSync(mFirebaseAnalytics);
                if (NetworkUtil.isOnline(getContext())) {
                    API.syncUserAsync(getContext(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, new ErrorResponseListener(getContext()) {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    if (RandoDAO.getNextRandoToUpload(getContext()) != null) {
                        UploadJobScheduler.scheduleUpload(getContext());
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), R.string.error_no_network, Toast.LENGTH_LONG).show();
                }
            }
        });
        showForceSyncButtonIfNecessary(rootView);
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
        randoPairsAdapter.changeCursor(RandoDAO.getCursor(getContext(), isStranger));
        randoPairsAdapter.notifyDataSetChanged();
        getActivity().registerReceiver(receiver, new IntentFilter(SYNC_BROADCAST_EVENT));
        if (!isStranger) {
            getActivity().registerReceiver(receiver, new IntentFilter(UPLOAD_SERVICE_BROADCAST_EVENT));
            getActivity().registerReceiver(receiver, new IntentFilter(SYNC_STATISTICS_EVENT));
        }
        getActivity().registerReceiver(receiver, new IntentFilter(PUSH_NOTIFICATION_BROADCAST_EVENT));
    }

    private void showForceSyncButtonIfNecessary(final View rootView) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getContext());
        Button forceSyncButton = rootView.findViewById(R.id.forceSyncButton);
        if (status != ConnectionResult.SUCCESS
                && (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && GooglePlayServicesUtil.isGPSVersionLowerThanRequired(getActivity().getPackageManager()))) {
            forceSyncButton.setVisibility(View.VISIBLE);
            forceSyncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeRefreshLayout.setRefreshing(true);
                    if (NetworkUtil.isOnline(getContext())) {
                        API.syncUserAsync(getContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, new ErrorResponseListener(getContext()) {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.error_no_network, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            forceSyncButton.setVisibility(View.GONE);
        }
    }
}
