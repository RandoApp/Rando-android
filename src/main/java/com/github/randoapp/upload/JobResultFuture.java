package com.github.randoapp.upload;

import com.evernote.android.job.Job;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class JobResultFuture implements Future<Job.Result> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private Job.Result value;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public Job.Result get() throws InterruptedException {
        latch.await();
        return value;
    }

    @Override
    public Job.Result get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return value;
        } else {
            throw new TimeoutException();
        }
    }

    // calling this more than once doesn't make sense, and won't work properly in this implementation. so: don't.
    void put(Job.Result result) {
        value = result;
        latch.countDown();
    }
}