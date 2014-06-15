package com.github.randoapp.task;

import com.github.randoapp.util.LocationHelper;

public class LocationUpdateTask extends BaseTask {

    private LocationHelper locationHelper;

    public LocationUpdateTask(LocationHelper locationHelper) {
        this.locationHelper = locationHelper;
    }

    @Override
    public Integer run() {
        while (locationHelper.gotLocation() == false) {
            //do nothing, just wait
        }
        return OK;
    }

}
