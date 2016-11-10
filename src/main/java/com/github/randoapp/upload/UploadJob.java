package com.github.randoapp.upload;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.github.randoapp.log.Log;

public class UploadJob extends Job {

    public static final String TAG = "job_upload_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(UploadJob.class, "Run Job!!!");
        return Result.SUCCESS;
    }
}
