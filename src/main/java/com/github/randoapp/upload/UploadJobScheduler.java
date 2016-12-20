package com.github.randoapp.upload;


import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.evernote.android.job.JobRequest;
import com.github.randoapp.log.Log;

import java.util.concurrent.TimeUnit;

public class UploadJobScheduler {


    public static void scheduleUpload(Context context) {
        Log.d(UploadJobScheduler.class, "Schedule Job to upload");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.startService(new Intent(context, UploadServiceLegacy.class));
        } else {
            new JobRequest.Builder(UploadJob.TAG)
                    .setExecutionWindow(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10))
                    .setBackoffCriteria(TimeUnit.MINUTES.toMillis(2), JobRequest.BackoffPolicy.LINEAR)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setRequirementsEnforced(true)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule();
        }
    }
}
