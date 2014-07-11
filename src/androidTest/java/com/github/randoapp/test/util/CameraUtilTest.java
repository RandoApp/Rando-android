package com.github.randoapp.test.util;

import android.hardware.Camera;
import android.test.AndroidTestCase;

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
        expectedCameraSizes.previewSize = createSize(640, 480);
        expectedCameraSizes.pictureSize = createSize(1600, 1200);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
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
        ), mockCameraHost(mockDeviceProfile(1536, Integer.MAX_VALUE)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(640, 480);
        expectedCameraSizes.pictureSize = createSize(2048, 1536);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
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
        ), mockCameraHost(mockDeviceProfile(1836, Integer.MAX_VALUE)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(768, 432);
        expectedCameraSizes.pictureSize = createSize(3264, 1836);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
    }


    public void testCameraSizesS3d2ucd2attEnforceProfile() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2048, 1536,
                        2048, 1152,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        960, 720,
                        640, 480
                ), createSizes(
                        1920, 1080,
                        1280, 720,
                        960, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        576, 432,
                        480, 320,
                        352, 288,
                        320, 240,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDeviceProfile(1836, 1836)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(720, 480);
        expectedCameraSizes.pictureSize = createSize(3264, 1836);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
    }

    public void testCameraSizesNexus4DefaultProfileNoPreviewWithTheSameRatio() throws Exception {
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
        expectedCameraSizes.previewSize = createSize(768, 432);
        expectedCameraSizes.pictureSize = createSize(3264, 1836);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
    }

    public void testCameraSizesSamsungd2vmuEnforceProfile() throws Exception {
        CameraSizes actualCameraSizes = CameraUtil.getCameraSizes(mockCameraParameters(
                createSizes(
                        3264, 2448,
                        3264, 2176,
                        3264, 1836,
                        2048, 1536,
                        2048, 1152,
                        1600, 1200,
                        1280, 960,
                        1280, 720,
                        960, 720,
                        640, 480
                ), createSizes(
                        1920, 1080,
                        1280, 720,
                        960, 720,
                        800, 480,
                        720, 480,
                        640, 480,
                        576, 432,
                        480, 320,
                        352, 288,
                        320, 240,
                        240, 160,
                        176, 144)
        ), mockCameraHost(mockDeviceProfile(2448, Integer.MAX_VALUE)), 768, 1184, Constants.DESIRED_PICTURE_SIZE, true);

        CameraSizes expectedCameraSizes = new CameraSizes();
        expectedCameraSizes.previewSize = createSize(640, 480);
        expectedCameraSizes.pictureSize = createSize(3264, 2448);

        assertThat("Picture sizes are not equal", actualCameraSizes, is(expectedCameraSizes));
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

    private String size2String(Camera.Size size) {
        return size.width + "x" + size.height;
    }

}
