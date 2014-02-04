package com.eucsoft.foodex.test.util;

import android.hardware.Camera;
import android.test.AndroidTestCase;

import static com.eucsoft.foodex.Constants.*;
import com.eucsoft.foodex.util.CameraUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class CameraUtilTest extends AndroidTestCase {

    public void testSelectMaxSizeButNearCameraMinSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                8000, 7000,
                9000, 8000,
                1900, CAMERA_MIN_SIZE,
                640, 480,
                400, 400));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(1900, CAMERA_MIN_SIZE)));
    }

    public void testGetCameraMinSizeRectangle() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                CAMERA_MIN_SIZE, 500,
                CAMERA_MIN_SIZE, CAMERA_MIN_SIZE,
                3000, CAMERA_MIN_SIZE,
                640, 480,
                400, 400));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(CAMERA_MIN_SIZE, CAMERA_MIN_SIZE)));
    }

    public void testGetMinSizeIfAllSizesAreHuge() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                9000, 9000,
                8000, 7000,
                6000, 5000,
                3000, 2000));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetMinSizeIfAllSizesAreHugeIgnoreOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                3000, 2000,
                6000, 5000,
                8000, 7000,
                9000, 9000));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetMaxSizeIfAllSizesAreLow() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                800, 700,
                900, 200,
                400, 500,
                300, 300));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(800, 700)));
    }

    public void testGetMaxSizeIfAllSizesAreLowIgnoreOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                300, 300,
                400, 500,
                900, 200,
                800, 700));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(800, 700)));
    }

    public void testGetMaxSizeWithMinResolution() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                3000, 7000,
                3000, 4000,
                3000, 2000,
                3000, 1000));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetRectangleResolutionIfItIsMaxSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSizeForOldDevices(createSizes(
                3000, 7000,
                3000, 4000,
                3000, 3000,
                3000, 1000,
                1000, 1000));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 3000)));
    }

    public void testGetMaxSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                7000, 5000,
                4000, 2000,
                3000, 3000,
                500, 400,
                400, 100));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(7000, 5000)));
    }

    public void testGetMaxSizeIgnoreSizeListOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                400, 100,
                500, 400,
                3000, 3000,
                4000, 2000,
                7000, 5000));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(7000, 5000)));
    }

    public void testGetMaxSizeIgnoreWidthAndHeightOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                4000, 5000,
                5000, 4000,
                3000, 3000,
                500, 400,
                400, 100));

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(4000, 5000)));
    }

    public void testGetBestPreivewSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                4000, 5000,
                2000, 4000,
                3000, 3000,
                400, 900,
                400, 100), 400, 800);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(2000, 4000)));
    }

    public void testGetBestPreivewSizeSameSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                4000, 5000,
                400, 800,
                4000, 8000,
                400, 900,
                400, 100), 400, 800);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(400, 800)));
    }

    private List<Camera.Size> createSizes(int... sizes) {
        List<Camera.Size> cameraSizes = new ArrayList<Camera.Size>();

        for (int i = 0; i < sizes.length; i+=2) {
            cameraSizes.add(createSize(sizes[i], sizes[i+1]));
        }
        return cameraSizes;
    }

    private Camera.Size createSize(int width, int height) {
        return mock(Camera.class).new Size(width, height);
    }

}
