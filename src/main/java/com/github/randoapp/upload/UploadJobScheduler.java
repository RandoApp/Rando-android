package com.github.randoapp.upload;


import android.content.Context;

import com.evernote.android.job.JobRequest;
import com.github.randoapp.log.Log;

import java.util.concurrent.TimeUnit;

public class UploadJobScheduler {


    public static void scheduleUpload(Context context) {
        Log.d(UploadJobScheduler.class, "Schedule Job to upload");
        new JobRequest.Builder(UploadJob.TAG)
                .setExecutionWindow(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10))
                .setBackoffCriteria(TimeUnit.MINUTES.toMillis(2), JobRequest.BackoffPolicy.LINEAR)
                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .startNow()
                .build()
                .schedule();
    }
}
