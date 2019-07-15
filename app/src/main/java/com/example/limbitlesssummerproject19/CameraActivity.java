package com.example.limbitlesssummerproject19;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;


public class CameraActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private int cameraFacing;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Size previewSize;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback stateCallback;
    private String cameraId;
    private TextureView textureView;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;


    public static final int CAMERA_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /*
         * Requesting permission to access camera
         */
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);

        /*
         * Requesting information inside the camera
         */
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        /*
         * Defining camera facing - back lens in this case
         */
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;



        /*
         * This is a listener and is used to know when the surface texture associated with the
         * TextureView is available
         */

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {

            /*
             * Camera is setup in here
             */
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                setUpCamera();
                openCamera();
            }

            /*
             * Camera does all the work for us
             */
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            /*
             * Camera stuff gets destroyed here
             */
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }
            /*
             * Update the view here
             */
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };

        /*
         * stateCallback is an object that receives updates about the state of the camera capture
         * session. There three state of the camera are open and everything is okay, simply
         * disconnected, or closed due to an error.
         */

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {

                CameraActivity.this.cameraDevice = cameraDevice;
                createPreviewSession();

            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {

                cameraDevice.close();
                CameraActivity.this.cameraDevice = null;

            }

            @Override
            public void onError(CameraDevice camera, int error) {

                cameraDevice.close();
                CameraActivity.this.cameraDevice = null;


            }

        };



    }

    /*
     * Camera is set before it opens. It is important to access the preview size from the
     * back camera to scale the TextureView written in the activity_camera.xml. Method returns an
     * array of Size objects prepared for SurfaceTexture.
     *
     * Note: minSdkVersion in build.gradle was changed from 15 to 'android-L' to set up the camera.
     */

    private void setUpCamera(){
        try{

            /*
             * Iterating through getCameraIdList until it find the correct camera facing
             */

            for (String cameraId: cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics =
                            cameraManager.getCameraCharacteristics(cameraId);

                if(cameraCharacteristics.get(cameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {

                    /*
                     * Accessing required metadata from the back camera lens
                     */

                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                         CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    /*
                     * Retrieving preview size information from the back camera lens
                     * to scale the TextureView
                     */

                     previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                     this.cameraId = cameraId;

                }

            }

        } catch (CameraAccessException e){
            e.printStackTrace();
        }

    }


    /*
     *  Checking permission to access the camera, and if granted, the camera gets opened. This
     *  line of code happens after retrieving information from the camera hardware to adjust the
     *  TextureView.
     */
    private void openCamera(){
        try{

            /*
             * Checking if camera allows the user enter.
             */

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED){

                /*
                 * Camera opens.
                 */

                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }

        } catch (CameraAccessException e) {

            e.printStackTrace();

        }
    }


    /*
     * A thread allows programs to run concurrently, separated from each other.
     * A looper is a thread that loops infinitely and listens for messages or runnables.
     * A HandlerThread runs outside of the activity's lifecycle. They are meant to handle multiple
     * jobs on the background thread. It has a thread and a looper.
     */
    private void openBackgroundThread(){

        backgroundThread = new HandlerThread("camera_background_thread");

        backgroundThread.start();

        backgroundHandler = new Handler(backgroundThread.getLooper());

    }

    /*
     * Resumes camera's operation by checking whether or not the textureView is available to use.
     */
    @Override
    protected void onResume() {

        super.onResume();

        openBackgroundThread();

        if (textureView.isAvailable()) {

            setUpCamera();
            openCamera();

        } else {

            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    /*
     * Upon finishing capturing a picture/session, the camera closes and the backgroung thread
     * stops working. We need to implement this since the camera and the background thread are
     * potential memory leaks.
     */


    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }


    /**
     * WORKING ON CREATE PREVIEW SESSION
     */
    private void createPreviewSession(){

        try {

            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

        } catch (CameraAccessException e){

            e.printStackTrace();
        }
    }
}
