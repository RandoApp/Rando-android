package com.github.randoapp.camera;

import android.hardware.Camera;

public class CameraSizes {
    public Camera.Size previewSize;
    public Camera.Size pictureSize;

    @Override
    public String toString() {
        return "CameraSizes{" +
                "previewSize=" + (previewSize!=null ? (previewSize.width +"x"+previewSize.height) : null) +
                ", pictureSize=" + ((pictureSize!=null)? (pictureSize.width +"x"+pictureSize.height) : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CameraSizes that = (CameraSizes) o;

        if (pictureSize != null ? !pictureSize.equals(that.pictureSize) : that.pictureSize != null)
            return false;
        if (previewSize != null ? !previewSize.equals(that.previewSize) : that.previewSize != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = previewSize != null ? previewSize.hashCode() : 0;
        result = 31 * result + (pictureSize != null ? pictureSize.hashCode() : 0);
        return result;
    }
}

