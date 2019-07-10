package com.example.limbitlesssummerproject19;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.text.SimpleDateFormat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import static android.content.ContentValues.TAG;


/**
 * CAMERA ACTIVITY IS SCRAPPED FROM THE PROJECT, GO INTO BURST MODE ACTIVITY
 */


public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    private static final String KEY_IS_CAPTURING = "is_capturing";
    private static final int FOCUS_AREA_SIZE = 300;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;


    private byte[] mCameraData;
    private boolean mIsCapturing;


    //Calls for captureImage function
    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    //Calls for a new picture and disregards the previous picture
    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };

    // Saves image to the internal storage inside the device
    private OnClickListener mSaveImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            saveToInternalStorage();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



            mCameraImage = findViewById(R.id.camera_image_view);
            mCameraImage.setVisibility(View.INVISIBLE);

            mCameraPreview = findViewById(R.id.preview_view);
            final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            mCaptureImageButton = findViewById(R.id.mTake);
            mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);


            /**
             * ALLOWS CAMERA TO FOCUS, WITHOUT IT THE CAMERA WOULD NOT FOCUS AT ALL.
             */
            ImageView mCameraImageView = findViewById(R.id.camera_image_view);
            mCameraImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        focusOnTouch(event);
                    }
                    return true;
                }
            });

            Button mSaveImageButton = (Button) findViewById(R.id.save_image_button);
            mSaveImageButton.setOnClickListener(mSaveImageButtonClickListener);
            mSaveImageButton.setEnabled(true);

            mIsCapturing = true;


    }

    //Checks if the permission to access the camera is granted or denied
    private boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }

    //Request to access the camera hardware
    private void requestPermission(){

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }




    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING,
                mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }


    //* this code is called upon to focus the camera */
    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i(TAG,"fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    //Calculates focus area inside the phone
    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mCameraPreview.getWidth()) * 2000 - 1000).intValue());
        int top = clamp(Float.valueOf((y / mCameraPreview.getHeight()) * 2000 - 1000).intValue());

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    // I am not sure what clamp does
    private int clamp(int touchCoordinateInCameraReper) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+ CameraActivity.FOCUS_AREA_SIZE /2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - CameraActivity.FOCUS_AREA_SIZE /2;
            } else {
                result = -1000 + CameraActivity.FOCUS_AREA_SIZE /2;
            }
        } else{
            result = touchCoordinateInCameraReper - CameraActivity.FOCUS_AREA_SIZE /2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };


    //CREATES A NEW FILE AND A NEW FOLDER CALLED LIMBITLESS INSIDE THE PHONE
    private File openFileForImage() {

        File imageDirectory;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Limbitless");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }

    /**
     * DURING THE ACTIVE SESSION OF THE CAMERA, THE ORIENTATION OF THE IMAGE GETS DISPLAYED IN
     * A VERTICAL MODE AS OPPOSED TO HORIZONTAL MODE (DEFAULT METHOD)
     */

    private int determineDisplayOrientation() {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);
        WindowManager wm = (WindowManager)
                getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        assert wm != null;
        int rotation = wm.getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * SAVES THE BITMAP DATA OF THE PICTURE INTO A FILE USING THE PATH FROM openFileForImage
     * METHOD. SAVES THE IMAGE INTO THE PHONE.
     */
    private void saveToInternalStorage() {

        File mypath = openFileForImage();
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(mypath);

            Bitmap bmp = BitmapFactory.decodeByteArray(mCameraData, 0,
                    mCameraData.length);
            Matrix matrix = new Matrix();
            if (android.os.Build.VERSION.SDK_INT < 9) {
                matrix.postRotate(90);
            } else {
                int orientation = determineDisplayOrientation();
                matrix.postRotate(orientation);
            }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(CameraActivity.this, "Image was saved!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                // Print out exception to verify later
                Log.e("CameraActivity", "exception", e);
                Toast.makeText(CameraActivity.this,
                        "Unable to open camera.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     *  FUNCTION TAKES THE DATA OF THE PICTURE AND SETS IT TO mCameraData METHOD
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();

    }
    /**
     * SURFACE HOLDER ALLOW TO DO SOMETHING RELATED TO THE CAMERA. I AM NOT COMPLETELY SURE WHAT IT
     * DOES, BUT IT HAS TO DO WITH SOMETHING INITIALIZING THE CAMERA BEFORE CALLING
     * setImageCapture()
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        //This should give me the right orientation
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(params);

        //Creates a size array and places all of the parameters inside the camera
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size mSize = null;

        for (Camera.Size size : sizes) {
            mSize = size;
        }

        // Checks the orientation of the camera to make is a portrait
        if (this.getResources().getConfiguration().orientation !=
                Configuration.ORIENTATION_LANDSCAPE) {

            // Sets to portrait
            params.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
            params.setRotation(90);

        } else {

            //Sets to landscape
            params.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
            params.setRotation(0);
        }
        assert mSize != null;
        params.setPictureSize(mSize.width, mSize.height);


        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * captureImage TAKES THE PICTURE ACTION INTENT AND SETS THE CONDITIONS TO STORE THE DATA OF
     * THE PICTURE ACQUIRED
     */

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    /**
     * OPENS THE CAMERA AND SETS ALL THE BUTTONS TO TAKE, BURST MODE, AND SAVE
     * WAITS FOR USER INPUT
     */
    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mCamera.startPreview();
        mCaptureImageButton.setText(R.string.capture_image);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    /**
     * UPON CAMERA CAPTURE, AND PREVIEW OF THE IMAGE IS DISPLAYED. THIS ALLOWS THE USER TO VERIFY
     * WHETHER OR NOT THE PICTURE IS SAVE BY THE USER OR A NEW PICTURE HAS TO BE TAKEN
     */
    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        mCameraImage.setImageBitmap(bitmap);
        mCamera.stopPreview();
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCameraImage.setVisibility(View.VISIBLE);
        mCaptureImageButton.setText(R.string.recapture_image);
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);

    }


}