package com.github.randoapp.util;

import android.hardware.Camera;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.DeviceProfile;
import com.github.randoapp.camera.CameraSizes;
import com.github.randoapp.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.github.randoapp.Constants.DESIRED_PICTURE_SIZE;
import static com.github.randoapp.Constants.PICTURE_DESIRED_ASPECT_RATIO;

public class CameraUtil {

    /**
     * Returns CameraSizes pair picture size and preview size with following conditions:
     * 1. Preview and Picture sizes must be in the same ratio
     * 2. Height of preview size should be less than screen height
     *
     * @param parameters         Camera.Parameters
     * @param screenWidth        available preview screen Width
     * @param screenHeight       available preview screen Height
     * @param desiredPictureSize
     * @return
     */

    public static CameraSizes getCameraSizes(Camera.Parameters parameters, final int screenWidth, final int screenHeight, final int desiredPictureSize) {
        CameraSizes cameraSizes = new CameraSizes();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

        Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                int cameraMinSize1 = Math.min(cameraSize1.height, cameraSize1.width);
                int cameraMinSize2 = Math.min(cameraSize2.height, cameraSize2.width);

                if (cameraMinSize1 >= desiredPictureSize && cameraMinSize2 >= desiredPictureSize) {
                    return compareSizeIncludeResolution(cameraSize1, cameraSize2);
                }
                return compareSizeIncludeResolution(cameraSize2, cameraSize1);
            }
        });

        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> filteredPreviewSizes = new ArrayList<Camera.Size>();

        for (Camera.Size size : previewSizes) {
            if (size.width <= screenWidth && size.height <= screenHeight) {
                filteredPreviewSizes.add(size);
            }
        }

        for (Camera.Size size : pictureSizes) {
            if (cameraSizes.pictureSize == null) {
                cameraSizes.pictureSize = size;
            }
            Camera.Size foundPreviewSize = findBiggestSizeByRatio(filteredPreviewSizes, size.width, size.height);
            if (foundPreviewSize != null) {
                cameraSizes.previewSize = foundPreviewSize;
                cameraSizes.pictureSize = size;
                break;
            }
        }
        if (cameraSizes.previewSize == null) {
            cameraSizes.previewSize = filteredPreviewSizes.get(0);
        }
        return cameraSizes;
    }


    private static Camera.Size findBiggestSizeByRatio(List<Camera.Size> cameraSizes, final int screenWidth, final int screenHeight) {
        double screenRatio = (double) screenWidth / screenHeight;
        Camera.Size optimalSize = null;
        for (Camera.Size cameraSize : cameraSizes) {
            double ratio = (double) cameraSize.width / cameraSize.height;
            if (Math.abs(screenRatio - ratio) < 0.05) {
                if (optimalSize != null) {
                    if (optimalSize.height < cameraSize.height && optimalSize.width < cameraSize.width) {
                        optimalSize = cameraSize;
                    }
                } else {
                    optimalSize = cameraSize;
                }
            }
        }
        return optimalSize;
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
