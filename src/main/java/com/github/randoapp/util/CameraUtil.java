package com.github.randoapp.util;

import android.hardware.Camera;

import com.commonsware.cwac.camera.DeviceProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.github.randoapp.Constants.CAMERA_MIN_SIZE;
import static com.github.randoapp.Constants.PICTURE_DESIRED_ASPECT_RATIO;

public class CameraUtil {

public static Camera.Size getBestPictureSize(List<Camera.Size> cameraSizes) {
        return findMaxCameraSize(cameraSizes);
    }

    public static Camera.Size getBestPictureSizeForOldDevices(List<Camera.Size> cameraSizes, DeviceProfile deviceProfile) {

        List<Camera.Size> filteredList = new ArrayList<Camera.Size>();
        for (Camera.Size size : cameraSizes){
            double ratio=(double)size.height / size.width;
            if (Math.abs(ratio - PICTURE_DESIRED_ASPECT_RATIO) < 0.05
                    && size.height > deviceProfile.getMinPictureHeight()
                    && size.height < deviceProfile.getMaxPictureHeight()){
                filteredList.add(size);
            }
        }

        if (filteredList.size() == 0){
            filteredList = cameraSizes;
        }

        Collections.sort(filteredList, new Comparator<Camera.Size>() {
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
        return filteredList.get(0);
    }

    public static Camera.Size getBestPreviewSize(List<Camera.Size> cameraSizes, final int screenWidth, final int screenHeight) {
        Camera.Size optimalSize = findBestSizeByRatio(cameraSizes, screenWidth, screenHeight);

        if (optimalSize == null) {
            optimalSize = findClosestSize(cameraSizes, screenWidth, screenHeight);
        }

        return optimalSize;
    }

    private static Camera.Size findBestSizeByRatio(List<Camera.Size> cameraSizes, final int screenWidth, final int screenHeight) {
        double screenRatio = (double) screenWidth / screenHeight;
        Camera.Size optimalSize = null;
        for (Camera.Size cameraSize : cameraSizes) {
            double ration = (double) cameraSize.width / cameraSize.height;
            if (Math.abs( screenRatio - ration ) < 0.05) {
                if (optimalSize != null) {
                    if (optimalSize.height > cameraSize.height && optimalSize.width > cameraSize.width) {
                        optimalSize = cameraSize;
                    }
                } else {
                    optimalSize = cameraSize;
                }
            }
        }
        return optimalSize;
    }


    private static Camera.Size findClosestSize(List<Camera.Size> cameraSizes, final int width, final int height) {
        Camera.Size optimalSize = cameraSizes.get(0);

        for (Camera.Size cameraSize : cameraSizes) {
            if (cameraSize.height >= height && optimalSize.height >= cameraSize.height
                    && cameraSize.width >= width && optimalSize.width >= cameraSize.width) {
                optimalSize = cameraSize;
            }
        }

        return optimalSize;
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
