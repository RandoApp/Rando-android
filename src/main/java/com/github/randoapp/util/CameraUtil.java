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

    public static Camera.Size getBestPictureSize(List<Camera.Size> cameraSizes) {
        return findMaxCameraSize(cameraSizes);
    }

    public static Camera.Size getBestPictureSize(List<Camera.Size> cameraSizes, DeviceProfile deviceProfile) {

        List<Camera.Size> filteredList = new ArrayList<Camera.Size>();
        for (Camera.Size size : cameraSizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - PICTURE_DESIRED_ASPECT_RATIO) < 0.05
                    && size.height >= deviceProfile.getMinPictureHeight()
                    && size.height <= deviceProfile.getMaxPictureHeight()) {
                filteredList.add(size);
            }
        }

        if (filteredList.size() == 0) {
            filteredList = cameraSizes;
        }

        Collections.sort(filteredList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                int cameraMinSize1 = Math.min(cameraSize1.height, cameraSize1.width);
                int cameraMinSize2 = Math.min(cameraSize2.height, cameraSize2.width);

                if (cameraMinSize1 >= DESIRED_PICTURE_SIZE && cameraMinSize2 >= DESIRED_PICTURE_SIZE) {
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

    public static Camera.Size getBestPictureSizeForOldDevices(List<Camera.Size> cameraSizes) {
        Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size cameraSize1, Camera.Size cameraSize2) {
                int cameraMinSize1 = Math.min(cameraSize1.height, cameraSize1.width);
                int cameraMinSize2 = Math.min(cameraSize2.height, cameraSize2.width);

                if (cameraMinSize1 >= DESIRED_PICTURE_SIZE && cameraMinSize2 >= DESIRED_PICTURE_SIZE) {
                    return compareSizeIncludeResolution(cameraSize1, cameraSize2);
                }
                return compareSizeIncludeResolution(cameraSize2, cameraSize1);
            }
        });
        return cameraSizes.get(0);
    }




    /**
     * Returns CameraSizes pair picture size and preview size with following conditions:
     * 1. Preview and Picture sizes must be in the same ratio
     * 2. Height of preview size should be less than screen height
     *
     * @param parameters         Camera.Parameters
     * @param host               CameraHost used
     * @param screenWidth        available preview screen Width
     * @param screenHeight       available preview screen Height
     * @param desiredPictureSize
     * @param enforceProfile
     * @return
     */

    public static CameraSizes getCameraSizes(Camera.Parameters parameters, CameraHost host,
                                             final int screenWidth, final int screenHeight, final int desiredPictureSize, boolean enforceProfile) {
        CameraSizes cameraSizes = new CameraSizes();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

        DeviceProfile deviceProfile = host.getDeviceProfile();

        List<Camera.Size> filteredPictureSizes = new ArrayList<Camera.Size>();

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

        if (enforceProfile && (deviceProfile.getMinPictureHeight() != 0 || deviceProfile.getMaxPictureHeight() != Integer.MAX_VALUE)) {
            filteredPictureSizes.add(getBestPictureSizeForOldDevices(parameters.getSupportedPictureSizes()));
            if (filteredPictureSizes.size() == 0) {
                return getCameraSizes(parameters, host, screenWidth, screenHeight, desiredPictureSize, false);
            }
        } else {
            for (Camera.Size size : pictureSizes) {
                if ((!enforceProfile)
                        ||
                        (size.height <= deviceProfile.getMaxPictureHeight()
                                && size.height >= deviceProfile.getMinPictureHeight())) {
                    filteredPictureSizes.add(size);
                }
            }
        }

        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> filteredPreviewSizes = new ArrayList<Camera.Size>();

        for (Camera.Size size : previewSizes) {
            if (size.width <= screenWidth && size.height <= screenHeight) {
                filteredPreviewSizes.add(size);
            }
        }

        for (Camera.Size size : filteredPictureSizes) {
            Camera.Size foundPreviewSize = findBiggestSizeByRatio(filteredPreviewSizes, size.width, size.height);
            if (foundPreviewSize != null) {
                cameraSizes.previewSize = foundPreviewSize;
                cameraSizes.pictureSize = size;
                break;
            }
        }
        if ((cameraSizes.pictureSize == null || cameraSizes.previewSize == null) && enforceProfile){
            return getCameraSizes(parameters, host, screenWidth, screenHeight, desiredPictureSize, false);
        }
        return cameraSizes;
    }


    private static Camera.Size findBestSizeByRatio(List<Camera.Size> cameraSizes, final int screenWidth, final int screenHeight) {
        double screenRatio = (double) screenWidth / screenHeight;
        Camera.Size optimalSize = null;
        for (Camera.Size cameraSize : cameraSizes) {
            double ratio = (double) cameraSize.width / cameraSize.height;
            if (Math.abs(screenRatio - ratio) < 0.05) {
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
