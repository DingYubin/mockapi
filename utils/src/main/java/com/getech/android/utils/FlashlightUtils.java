package com.getech.android.utils;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

public final class FlashlightUtils {

    private static final String TAG = "FlashlightUtils";

    private Camera mCamera;

    private FlashlightUtils() {
    }

    /**
     * Return the single {@link FlashlightUtils} instance.
     *
     * @return the single {@link FlashlightUtils} instance
     */
    public static FlashlightUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Return whether the device supports flashlight.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFlashlightEnable() {
        return Utils.getApp()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * Register the utils of flashlight.
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean register() {
        try {
            mCamera = Camera.open(0);
        } catch (Throwable t) {
            Log.e(TAG, "register: ", t);
            return false;
        }
        if (mCamera == null) {
            Log.e(TAG, "register: open camera failed!");
            return false;
        }
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Unregister the utils of flashlight.
     */
    public void unregister() {
        if (mCamera == null) return;
        mCamera.stopPreview();
        mCamera.release();
    }

    /**
     * Turn on the flashlight.
     */
    public void setFlashlightOn() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (!FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * Turn off the flashlight.
     */
    public void setFlashlightOff() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * Return whether the flashlight is working.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isFlashlightOn() {
        if (mCamera == null) {
            Log.e(TAG, "setFlashlightOn: the utils of flashlight register failed!");
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        return FLASH_MODE_TORCH.equals(parameters.getFlashMode());
    }

    private static final class LazyHolder {
        private static final FlashlightUtils INSTANCE = new FlashlightUtils();
    }
}
