//package com.example.android.camera2basic;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.ImageFormat;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.CameraMetadata;
//import android.hardware.camera2.CaptureRequest;
//import android.hardware.camera2.params.MeteringRectangle;
//import android.hardware.camera2.params.StreamConfigurationMap;
//import android.media.ImageReader;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.util.Size;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Switch;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//
///**
// * Created by Emilio on 2/4/17.
// */
//
//public class CameraFragment extends Fragment {
//
//    private static final int RESULT_LOAD_IMAGE = 1;
//
//    private String mCameraId;
//
//    private AutoFitTextureView mTextureView;
//
//    private Switch mDemoModeSwitch;
//
//    private CameraCharacteristics mCharacteristics;
//
//    private ImageReader mImageReader;
//
//    private CameraDevice mCameraDevice;
//
//    private CameraCaptureSession mCaptureSession;
//
//
//
//    public static CameraFragment newInstance() {
//        return new CameraFragment();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
//
//        for (String cameraId : cameraManager.getCameraIdList()) {
//
//            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
//
//            if (characteristics.get(CameraCharacteristics.LENS_FACING) != null
//                    && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
//
//                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//
//                Size largest = Collections.max(
//                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
//                        new Camera2BasicFragment.CompareSizesByArea()
//                );
//
//
//            }
//        }
//    }
//
//    /**
//     * Sets up member variables related to camera.
//     *
//     * @param width  The width of available size for camera preview
//     * @param height The height of available size for camera preview
//     */
//    private void setUpCameraOutputs(int width, int height) {
//        Activity activity = getActivity();
//        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            for (String cameraId : manager.getCameraIdList()) {
//                mCharacteristics
//                        = manager.getCameraCharacteristics(cameraId);
//
//                // We don't use a front facing camera in this sample.
//                Integer facing = mCharacteristics.get(CameraCharacteristics.LENS_FACING);
//                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
//                    continue;
//                }
//
//                StreamConfigurationMap map = mCharacteristics.get(
//                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//                if (map == null) {
//                    continue;
//                }
//
//                // For still image captures, we use the largest available size.
//                Size largest = Collections.max(
//                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
//                        new Camera2BasicFragment.CompareSizesByArea());
//                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
//                        ImageFormat.JPEG, /*maxImages*/2);
//                mImageReader.setOnImageAvailableListener(
//                        new ImageReader.OnImageAvailableListener() {
//                            @Override
//                            public void onImageAvailable(ImageReader imageReader) {
//                                System.out.println("EMILIO: image available");
//                                mBackgroundHandler.post(new Camera2BasicFragment.ImageSaver(reader.acquireNextImage(), mFile));
//                            }
//                        },
//                        mBackgroundHandler);
//                System.out.println("EMILIO: image reader set");
//
//                // Find out if we need to swap dimension to get the preview size relative to sensor
//                // coordinate.
//                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//                //noinspection ConstantConditions
//                mSensorOrientation = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//                boolean swappedDimensions = false;
//                switch (displayRotation) {
//                    case Surface.ROTATION_0:
//                    case Surface.ROTATION_180:
//                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    case Surface.ROTATION_90:
//                    case Surface.ROTATION_270:
//                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    default:
//                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
//                }
//
//                Point displaySize = new Point();
//                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
//                int rotatedPreviewWidth = width;
//                int rotatedPreviewHeight = height;
//                int maxPreviewWidth = displaySize.x;
//                int maxPreviewHeight = displaySize.y;
//
//                if (swappedDimensions) {
//                    rotatedPreviewWidth = height;
//                    rotatedPreviewHeight = width;
//                    maxPreviewWidth = displaySize.y;
//                    maxPreviewHeight = displaySize.x;
//                }
//
//                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
//                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
//                }
//
//                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
//                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
//                }
//
//                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
//                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
//                // garbage capture data.
//                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
//                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
//                        maxPreviewHeight, largest);
//
//                // We fit the aspect ratio of TextureView to the size of preview we picked.
//                int orientation = getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
//                } else {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
//                }
//
//                // Check if the flash is supported.
//                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
//                mFlashSupported = available == null ? false : available;
//
//                mCameraId = cameraId;
//                return;
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            // Currently an NPE is thrown when the Camera2API is used but not supported on the
//            // device this code runs.
//            Camera2BasicFragment.ErrorDialog.newInstance(getString(R.string.camera_error))
//                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_camera2_basic, container, false);
//
//        view.findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takePicture();
//            }
//        });
//
//        view.findViewById(R.id.button_choose).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                );
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//            }
//        });
//
//        mTextureView = (TextureView) view.findViewById(R.id.texture);
//        mTextureView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        try {
//                            focusCamera(motionEvent);
//                        } catch (CameraAccessException e) {
//                            e.printStackTrace();
//                        }
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void focusCamera(MotionEvent event) throws CameraAccessException {
//        CaptureRequest.Builder requestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//        Rect rect;
//        Size size;
//        if (CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE != null) {
//            rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
//        } else {
//            rect = new Rect(0, 0, 3000, 4096);
//        }
//        Log.i("onAreaTouchEvent", "SENSOR_INFO_ACTIVE_ARRAY_SIZE,,,,,,,,rect.left--->" + rect.left + ",,,rect.top--->" + rect.top + ",,,,rect.right--->" + rect.right + ",,,,rect.bottom---->" + rect.bottom);
//        if (CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE != null) {
//            size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
//        } else {
//            size = new Size(3000, 4096);
//        }
//        Log.i("onAreaTouchEvent", "mCameraCharacteristics,,,,size.getWidth()--->" + size.getWidth() + ",,,size.getHeight()--->" + size.getHeight());
//        int areaSize = 64;
//        //Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
//        int right = rect.right;
//        int bottom = rect.bottom;
//        int viewWidth = mTextureView.getWidth();
//        int viewHeight = mTextureView.getHeight();
//        int ll, rr;
//        Rect newRect;
//        int centerX = (int) event.getX();
//        int centerY = (int) event.getY();
//        ll = ((centerX * right) - areaSize) / viewWidth;
//        rr = ((centerY * bottom) - areaSize) / viewHeight;
//        int focusLeft = clamp(ll, 0, right);
//        int focusBottom = clamp(rr, 0, bottom);
//        Log.i("focus_position", "focusLeft--->" + focusLeft + ",,,focusTop--->" + focusBottom + ",,,focusRight--->" + (focusLeft + areaSize) + ",,,focusBottom--->" + (focusBottom + areaSize));
//        newRect = new Rect(focusLeft, focusBottom, focusLeft + areaSize, focusBottom + areaSize);
//        MeteringRectangle meteringRectangle = new MeteringRectangle(newRect, 100);
//        MeteringRectangle[] meteringRectangleArr = {meteringRectangle};
//
//        requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
//        requestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, meteringRectangleArr);
//        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//        requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
//        mCaptureSession.capture(requestBuilder.build(), null, null);
//    }
//
//    private int clamp(int val, int min, int max) {
//        if (val > max) {
//            return max;
//        }
//        if (val < min) {
//            return min;
//        }
//        return val;
//    }
//}
