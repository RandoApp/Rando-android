package com.github.randoapp.task;

import android.location.Location;
import android.text.TextUtils;

import com.github.randoapp.api.API;
import com.github.randoapp.api.exception.ForbiddenException;
import com.github.randoapp.api.exception.RequestTooLongException;
import com.github.randoapp.db.model.RandoUpload;
import com.github.randoapp.log.Log;

import java.io.File;
import java.io.FileNotFoundException;

import static com.github.randoapp.Constants.FILE_NOT_FOUND_ERROR;
import static com.github.randoapp.Constants.FORBIDDEN_ERROR;
import static com.github.randoapp.Constants.INCORRECT_ARGS_ERROR;
import static com.github.randoapp.Constants.REQUEST_TOO_LONG_ERROR;

public class UploadTask extends BaseTask {

    private String fileToUpload;
    private Location location;

    public UploadTask(RandoUpload randoUpload) {
        this.fileToUpload = randoUpload.file;
        this.location = getLocation(randoUpload);
    }

    private Location getLocation(RandoUpload randoUpload) {
        Location location = new Location("Rando4Me.UploadService");
        location.setLatitude(Double.parseDouble(randoUpload.latitude));
        location.setLongitude(Double.parseDouble(randoUpload.longitude));
        return location;
    }

    @Override
    public Integer run() {
        Log.d(UploadTask.class, "run");

        if (fileToUpload == null || TextUtils.isEmpty(fileToUpload) || location == null) {
            data.put("error", INCORRECT_ARGS_ERROR);
            return ERROR;
        }

        try {
            API.uploadImage(new File(fileToUpload), location);
            return OK;
        } catch (FileNotFoundException e) {
            data.put("error", FILE_NOT_FOUND_ERROR);
        } catch (RequestTooLongException e) {
            data.put("error", REQUEST_TOO_LONG_ERROR);
        } catch (ForbiddenException e) {
            data.put("error", FORBIDDEN_ERROR);
        } catch (Exception e) {
            data.put("error", e.getMessage());
        }
        return ERROR;
    }
}