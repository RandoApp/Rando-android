package com.github.randoapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.log.Log;
import com.github.randoapp.upload.UploadService;
import com.github.randoapp.util.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.d(NetworkChangeReceiver.class, "Network status: " + status);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                if (RandoDAO.countAllRandosToUpload() > 0) {
                    context.startService(new Intent(context, UploadService.class));
                }
            } else {
                Log.d(NetworkChangeReceiver.class, "Not Connected");
            }

        }
    }
}