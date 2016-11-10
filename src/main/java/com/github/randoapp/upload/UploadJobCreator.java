package com.github.randoapp.upload;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;


public class UploadJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case UploadJob.TAG:
                return new UploadJob();
            default:
                return null;
        }
    }
}
