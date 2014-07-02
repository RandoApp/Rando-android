package com.github.randoapp.test.util;

import android.hardware.Camera;
import android.test.AndroidTestCase;

import static com.github.randoapp.Constants.*;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.DeviceProfile;
import com.github.randoapp.Constants;
import com.github.randoapp.camera.CameraSizes;
import com.github.randoapp.util.CameraUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CameraUtilTest extends AndroidTestCase {

    public void testSelectMaxSizeButNearCameraMinSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        8000, 7000,
                        9000, 8000,
                        1900, DESIRED_PICTURE_SIZE,
                        640, 480,
                        400, 400),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(640, 480)));
    }

    public void testGetCameraMinSizeRectangle() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        DESIRED_PICTURE_SIZE, 500,
                        DESIRED_PICTURE_SIZE, DESIRED_PICTURE_SIZE,
                        3000, DESIRED_PICTURE_SIZE,
                        640, 480,
                        400, 400),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(640, 480)));
    }

    public void testGetMinSizeIfAllSizesAreHuge() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        9000, 9000,
                        8000, 7000,
                        6000, 5000,
                        3000, 2000),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetMinSizeIfAllSizesAreHugeIgnoreOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        3000, 2000,
                        6000, 5000,
                        8000, 7000,
                        9000, 9000),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetMaxSizeIfAllSizesAreLow() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        800, 700,
                        900, 200,
                        400, 500,
                        300, 300),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(800, 700)));
    }

    public void testGetMaxSizeIfAllSizesAreLowIgnoreOrder() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        300, 300,
                        400, 500,
                        900, 200,
                        800, 700),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(800, 700)));
    }

    public void testGetMaxSizeWithMinResolution() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        3000, 7000,
                        3000, 4000,
                        3000, 2000,
                        3000, 1000),
                DeviceProfile.getInstance(getContext())
        );

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(3000, 2000)));
    }

    public void testGetRectangleResolutionIfItIsMaxSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPictureSize(createSizes(
                        3000, 7000,
                        3000, 4000,
                        3000, 3000,
                        3000, 1000,
                        1000, 1000),
                DeviceProfile.getInstance(getContext())
        );

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
                400, 1000,
                400, 900,
                400, 100), 400, 800);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(2000, 4000)));
    }

    public void testGetBestPreivewSizeSameSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                4000, 5000,
                400, 800,
                4000, 8000,
                400, 1000,
                400, 900,
                400, 100), 400, 800);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(400, 800)));
    }

    public void testGetBestPreivewSizeBigTarget() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                4000, 5000,
                400, 800,
                4000, 8000,
                400, 1000,
                400, 900,
                4200, 5000,
                400, 100), 70000, 80000);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(4200, 5000)));
    }

    public void testGetBestPreivewSizeSmallTarget() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                4000, 5000,
                400, 800,
                4000, 8000,
                400, 1000,
                600, 700,
                400, 900,
                400, 100), 100, 80);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(400, 100)));
    }

    public void testGetBestPreivewSizeWithAllSizesWithEqualRation() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                2000, 2000,
                1000, 1000,
                400, 400,
                100, 100), 800, 1200);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(2000, 2000)));
    }

    public void testGetBestPreivewSizeIgnoreOrderList() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                3000, 3000,
                1000, 1000,
                400, 400,
                100, 100,
                2000, 2000), 800, 1200);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(2000, 2000)));
    }

    public void testGetBestPreivewSizeOneSize() throws Exception {
        Camera.Size actual = CameraUtil.getBestPreviewSize(createSizes(
                100, 100), 800, 1200);

        assertThat(actual.width + ":" + actual.height, actual, is(createSize(100, 100)));
    }


    public void testCameraSizesNexus4DefaultProfile() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2592, 1944,
                        2048, 1536,
                        1920, 1080,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        352, 288,
                        320, 240,
                        176, 144
                ), createSizes(
                        1280, 720,
                        800, 480,
                        768, 432,
                        720, 480,
                        640, 480,
                        576, 432,
                        480, 320,
                        384, 288,
                        352, 288,
                        320, 240,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDefaultDeviceProfile()), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(640,480);
        expectedCameraSizes.pictureSize = createSize(1600,1200);

        assertThat("Preview size " + actualCameraSizes.previewSize.width + ":" + actualCameraSizes.previewSize.height + "But Expected:"+expectedCameraSizes.previewSize.width + ":" + expectedCameraSizes.previewSize.height, actualCameraSizes.previewSize, is(expectedCameraSizes.previewSize));
        assertThat("Picture size " + actualCameraSizes.pictureSize.width + ":" + actualCameraSizes.pictureSize.height+expectedCameraSizes.pictureSize.width + ":" + expectedCameraSizes.pictureSize.height, actualCameraSizes.pictureSize, is(expectedCameraSizes.pictureSize));
    }

    public void testCameraSizesNexus4EnforceProfileRatio1_3() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2592, 1944,
                        2048, 1536,
                        1920, 1080,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        352, 288,
                        320, 240,
                        176, 144
                ), createSizes(
                        1280, 720,
                        800, 480,
                        768, 432,
                        720, 480,
                        640, 480,
                        576, 432,
                        480, 320,
                        384, 288,
                        352, 288,
                        320, 240,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDeviceProfile(1536, 1536)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(640,480);
        expectedCameraSizes.pictureSize = createSize(1600, 1200);

        assertThat("Preview size " + actualCameraSizes.previewSize.width + ":" + actualCameraSizes.previewSize.height + "But Expected:"+expectedCameraSizes.previewSize.width + ":" + expectedCameraSizes.previewSize.height, actualCameraSizes.previewSize, is(expectedCameraSizes.previewSize));
        assertThat("Picture size " + actualCameraSizes.pictureSize.width + ":" + actualCameraSizes.pictureSize.height+expectedCameraSizes.pictureSize.width + ":" + expectedCameraSizes.pictureSize.height, actualCameraSizes.pictureSize, is(expectedCameraSizes.pictureSize));
    }

    public void testCameraSizesNexus4EnforceProfileRatio1_7() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2592, 1944,
                        2048, 1536,
                        1920, 1080,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        352, 288,
                        320, 240,
                        176, 144
                ), createSizes(
                        1280, 720,
                        800, 480,
                        768, 432,
                        720, 480,
                        640, 480,
                        576, 432,
                        480, 320,
                        384, 288,
                        352, 288,
                        320, 240,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDeviceProfile(1836,1836)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(640,480);
        expectedCameraSizes.pictureSize = createSize(1600, 1200);

        assertThat("Preview size " + actualCameraSizes.previewSize.width + ":" + actualCameraSizes.previewSize.height + "But Expected:"+expectedCameraSizes.previewSize.width + ":" + expectedCameraSizes.previewSize.height, actualCameraSizes.previewSize, is(expectedCameraSizes.previewSize));
        assertThat("Picture size " + actualCameraSizes.pictureSize.width + ":" + actualCameraSizes.pictureSize.height+expectedCameraSizes.pictureSize.width + ":" + expectedCameraSizes.pictureSize.height, actualCameraSizes.pictureSize, is(expectedCameraSizes.pictureSize));
    }

    public void ignoretestCameraSizesNexus4DefaultProfileNoPreviewWithTheSameRatio() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2592, 1944,
                        2048, 1536,
                        1920, 1080,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        352, 288,
                        320, 240,
                        176, 144
                ), createSizes(
                        1280, 720,
                        800, 480,
                        768, 432,
                        720, 480,
                        480, 320,
                        352, 288,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDefaultDeviceProfile()), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(768,432);
        expectedCameraSizes.pictureSize = createSize(1600,1200);

        assertThat("Preview size " + actualCameraSizes.previewSize.width + ":" + actualCameraSizes.previewSize.height + "But Expected:"+expectedCameraSizes.previewSize.width + ":" + expectedCameraSizes.previewSize.height, actualCameraSizes.previewSize, is(expectedCameraSizes.previewSize));
        assertThat("Picture size " + actualCameraSizes.pictureSize.width + ":" + actualCameraSizes.pictureSize.height, actualCameraSizes.pictureSize, is(expectedCameraSizes.pictureSize));
    }


    private List<Camera.Size> createSizes(int... sizes) {
        List<Camera.Size> cameraSizes = new ArrayList<Camera.Size>();

        if (sizes.length % 2 != 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < sizes.length; i += 2) {
            cameraSizes.add(createSize(sizes[i], sizes[i + 1]));
        }
        return cameraSizes;
    }

    private Camera.Size createSize(int width, int height) {
        return mock(Camera.class).new Size(width, height);
    }

    private Camera.Parameters mockCameraParameters(List<Camera.Size> pictureSizes, List<Camera.Size> previewSizes) throws IOException {
        Camera.Parameters parametersMock = mock(Camera.Parameters.class);
        when(parametersMock.getSupportedPictureSizes()).thenReturn(pictureSizes);
        when(parametersMock.getSupportedPreviewSizes()).thenReturn(previewSizes);
        return parametersMock;
    }

    private DeviceProfile mockDefaultDeviceProfile() throws IOException {
        return mockDeviceProfile(0, Integer.MAX_VALUE);
    }

    private DeviceProfile mockDeviceProfile(int minHeight, int maxHeight) throws IOException {
        DeviceProfile deviceProfileMock = mock(DeviceProfile.class);
        when(deviceProfileMock.getMaxPictureHeight()).thenReturn(maxHeight);
        when(deviceProfileMock.getMinPictureHeight()).thenReturn(minHeight);

        return deviceProfileMock;
    }

    private CameraHost mockCameraHost(DeviceProfile deviceProfile) throws IOException {
        CameraHost cameraHost = mock(CameraHost.class);
        when(cameraHost.getDeviceProfile()).thenReturn(deviceProfile);

        return cameraHost;
    }

}
