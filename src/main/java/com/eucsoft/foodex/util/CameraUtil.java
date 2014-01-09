package com.eucsoft.foodex.util;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.eucsoft.foodex.Constants.CAMERA_MIN_SIZE;

public class CameraUtil {

    //this epsilon being so large is intended, as often there will not be an adequate resolution with
    //the correct aspect ratio available
    //so we trade the correct aspect ratio for faster rendering
    private final static double epsilon = 0.17;

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

    /**
     * Get the optimal preview size for the given screen size.
     *
     * @param sizes
     * @param previewWidth
     * @param previewHeight
     * @return
     */
    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int previewWidth, int previewHeight) {
        double aspectRatio = ((double) previewWidth) / previewHeight;
        Camera.Size optimalSize = null;
        for (Iterator<Camera.Size> iterator = sizes.iterator(); iterator.hasNext(); ) {
            Camera.Size currSize = iterator.next();
            double curAspectRatio = ((double) currSize.width) / currSize.height;
            //do the aspect ratios equal?
            if (Math.abs(aspectRatio - curAspectRatio) < epsilon) {
                //they do
                if (optimalSize != null) {
                    //is the current size smaller than the one before
                    if (optimalSize.height > currSize.height && optimalSize.width > currSize.width) {
                        optimalSize = currSize;
                    }
                } else {
                    optimalSize = currSize;
                }
            }
        }
        if (optimalSize == null) {
            //did not find a size with the correct aspect ratio.. let's choose the smallest instead
            for (Iterator<Camera.Size> iterator = sizes.iterator(); iterator.hasNext(); ) {
                Camera.Size currSize = iterator.next();
                if (optimalSize != null) {
                    //is the current size smaller than the one before
                    if (optimalSize.height > currSize.height && optimalSize.width > currSize.width) {
                        optimalSize = currSize;
                    } else {
                        optimalSize = currSize;
                    }
                } else {
                    optimalSize = currSize;
                }

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
