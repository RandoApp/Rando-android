package com.eucsoft.foodex.util;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.eucsoft.foodex.Constants.CAMERA_MIN_SIZE;

public class CameraUtil {

public static Camera.Size getBestPictureSize(List<Camera.Size> cameraSizes) {
        return findMaxCameraSize(cameraSizes);
    }

    public static Camera.Size getBestPictureSizeForOldDevices(List<Camera.Size> cameraSizes) {
        Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                int cameraMinSize1 = Math.min(cameraSize1.height, cameraSize1.width);
                int cameraMinSize2 = Math.min(cameraSize2.height, cameraSize2.width);

                if (cameraMinSize1 >= CAMERA_MIN_SIZE && cameraMinSize2 >= CAMERA_MIN_SIZE) {
                    return compareSizeIncludeResolution(cameraSize1, cameraSize2);
                }
                return compareSizeIncludeResolution(cameraSize2, cameraSize1);
            }
        });
        return cameraSizes.get(0);
    }

    public static Camera.Size getBestPreviewSize(List<Camera.Size> cameraSizes, final int width, final int height) {
        return findClosestSize(cameraSizes, width, height);
    }

    private static Camera.Size findClosestSize(List<Camera.Size> cameraSizes, final int width, final int height) {
        Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                double targetRatio = (double) height / width;
                double ratio1 = (double) cameraSize1.height / cameraSize1.width;
                double ratio2 = (double) cameraSize2.height / cameraSize2.width;

                if (cameraSize1.height >= height & cameraSize2.height >= height
                        & cameraSize1.width >= width & cameraSize2.width >= width) {
                    if (Math.abs(ratio1 - targetRatio) >= (Math.abs(ratio2 - targetRatio))) {
                        return -1;
                    }
                }

                return 1;
            }
        });
        return cameraSizes.get(0);
    }

    private static Camera.Size findMaxCameraSize(List<Camera.Size> cameraSizes) {
        Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                return Math.min(cameraSize2.height, cameraSize2.width) - Math.min(cameraSize1.width, cameraSize1.height);
            }
        });
        return cameraSizes.get(0);
    }

    private static int compareSizeIncludeResolution(Camera.Size cameraSize1, Camera.Size cameraSize2) {
        int cameraMinSize1 = Math.min(cameraSize1.height, cameraSize1.width);
        int cameraMinSize2 = Math.min(cameraSize2.height, cameraSize2.width);

        if (cameraMinSize1 == cameraMinSize2) {
            return cameraSize1.height * cameraSize1.width - cameraSize2.height * cameraSize2.width;
        }
        return cameraMinSize1 - cameraMinSize2;
    }

}
