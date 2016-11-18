package com.github.randoapp.upload;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.github.randoapp.Constants;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;

import java.util.concurrent.TimeUnit;

public class UploadJobScheduler {


    public static void scheduleUpload(RandoUpload randoUpload, Context context) {
        Log.d(UploadJobScheduler.class, "Schedule Job to upload", randoUpload.toString());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.startService(new Intent(context, UploadServiceLegacy.class));
        } else {
            PersistableBundleCompat extras = new PersistableBundleCompat();
            extras.putLong(Constants.TO_UPLOAD_RANDO_ID, randoUpload.id);

            int jobId = new JobRequest.Builder(UploadJob.TAG)
                    .setExecutionWindow(1_000L, TimeUnit.DAYS.toMillis(10))
                    .setBackoffCriteria(TimeUnit.MINUTES.toMillis(2), JobRequest.BackoffPolicy.EXPONENTIAL)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setExtras(extras)
                    .setRequirementsEnforced(true)
                    .setPersisted(true)
                    .setUpdateCurrent(false)
                    .build()
                    .schedule();
        }
    }
}
