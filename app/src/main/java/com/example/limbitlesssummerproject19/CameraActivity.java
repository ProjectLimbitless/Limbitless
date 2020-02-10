package com.example.limbitlesssummerproject19;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Locale;

/**
 * File: CameraActivity.java
 *
 * This activity controls the camera function of the app. (This is a BIG file!)
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends AppCompatActivity implements SensorEventListener{


   /**Button on camera preview to capture image */
    private ImageButton endsession;
    public AutoFitTextureView textureView; /** The camera preview itself */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(); /** used for orientation correction of photos */

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String mCameraId; /** unique ID used to access the camera */
    public CameraDevice cameraDevice; /** representation of a single camera connected to the device */
    public CameraCaptureSession mCaptureSession; /** configured capture session; used for capturing images from the camera */
    public CaptureRequest.Builder mPreviewRequestBuilder; /** how to create the capture request */

    public Handler mBackgroundHandler; /** to schedule actions to be executed at some point in the future */
    public HandlerThread mBackgroundThread; /** the thread associated with the handler */

    public String directoryName;
    public String sessionName;
    public File session;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private ImageReader mImageReader;

    private Size mPreviewSize;
    private static final String TAG = CameraActivity.class.getSimpleName();

    private boolean mFlashSupported;
    private int numberOfImages = 0;
    private File mFile;
    private CaptureRequest mPreviewRequest;
    private int tenthDigit = 0;
    private int hundredsDigit = 0;
    private int thousandsDigit = 0;
    private int mState = STATE_PREVIEW;

    private static final int MAX_PREVIEW_WIDTH = 1080;
    private static final int MAX_PREVIEW_HEIGHT = 1920;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;



    public Context context;


    private SensorManager sensorManager;

    private HandlerThread mSensorThread;
    private Handler mainHandler = new Handler();
    ImageButton startSession;


    private float[] avgAccelerometerData = new float[3];
    private float[] avgMagnetometerData = new float[3];

    float[] globalOrientation = new float[3];
    float[] currentOrientation = new float[3];
    float[] previousOrientation = new float[3];
    float[] currentAvgOrientation = new float[3];

    // final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    private Display mDisplay;

    boolean SWITCH = true;
    private Toast mToast = null;

    MovingAverage orientationMA = new MovingAverage(20);
    MovingAverage accelerometerMA = new MovingAverage(20);
    MovingAverage magnetometerMA = new MovingAverage(20);

    private static final long START_TIME_IN_MILLIS = 6000;
    private TextView mTextViewCountdown;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    /*------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /** Setting back button to main activity */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        textureView = findViewById(R.id.texture);

        /**
         * Creates the layout of the camera view. In this case its is the opaque windows
         * encapsulating the view of the camera
         */

        View topView = findViewById(R.id.top_view);
        View bottomView = findViewById(R.id.bottom_view);
        View leftView =  findViewById(R.id.left_view);
        View rightView = findViewById(R.id.right_view);

        topView.getBackground().setAlpha(128);
        bottomView.getBackground().setAlpha(128);
        leftView.getBackground().setAlpha(128);
        rightView.getBackground().setAlpha(128);

        /** set up paths to store photos */
        createDirectory();

        textureView.setSurfaceTextureListener(textureListener);


        /**
         *  Here is the starting session of the vectors sensors, on Create. Upon opening the new
         *  activity, the sensors begin to orient themselves to acquire the required data. More
         *  work has to be done, but it works up to now.
         *
         */
        /**-------------------Starts the Sesion-----------------------------*/

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startSession =  findViewById(R.id.startSession);

        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "startSession in progress");
                startSession.setVisibility(View.INVISIBLE);
                startTimer();
            }
        });

        endsession =  findViewById(R.id.end_session);
        endsession.setVisibility(View.INVISIBLE);
        endsession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SWITCH = false;
                customToast("Session Over. Hit the return button and start a new session!");
                endsession.setVisibility(View.INVISIBLE);
            }
        });

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
        /**------------------------------------------------------------*/

        /**Countdown code */
        mTextViewCountdown =  findViewById(R.id.countdown);
    }



    /**
     * Function: startTimer()
     * Purpose: starts a countdown when the user starts a camera capturing session
     * Parameters: none
     * Return: none
     */
    public void startTimer() {
        CountDownTimer mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                endsession.setVisibility(View.VISIBLE);
                sessionLoop();
            }
        }.start();


    }


    /**
     * Function: updateCountDownText()
     * Purpose: display the countdown to the screen (shows numbers decreasing)
     * Parameters: none
     * Return: none
     */
    public void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        if(seconds == 4) {
            setStartingPosition();
            setCheckPoint();
            customToast("Sensors calibrated!");
        }

        if(seconds == 2)
            customToast("Session is starting!");

        String timeLeft = Integer.toString(seconds);
        if (seconds == 0) {
            mTextViewCountdown.setText("");
        } else
            mTextViewCountdown.setText(timeLeft);
    }



    /**
     * Function: customToast()
     * Purpose: display a custom toast message to the screen
     * Parameters: String message = the message that will be printed to the screen
     * Return: none
     */
    public void customToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));

        TextView toastText = layout.findViewById(R.id.toast_text);
        //ImageView toastImage = layout.findViewById(R.id.toast_image); //to dynamically set image of toast

        toastText.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 250);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.show();
    }



    /**
     * Function: takePicture2()
     * Purpose: Take a picture (delegates to lockFocus() function)
     * Parameters: none
     * Return: none
     */
    private void takePicture2(){
        lockFocus();
    }



    // creates a directory per session
    /**
     * Function: createDirectory()
     * Purpose: creates a directory per photo capturing session
     * Parameters: none
     * Return: none
     */
    public void  createDirectory(){
        directoryName = Environment.getExternalStorageDirectory() +
                File.separator + "ProstheticFolder";
        File directory = new File( directoryName );

        if( !directory.exists() ) {
            directory.mkdirs();
        }

        File countFiles = new File( directoryName );
        File[] files = countFiles.listFiles( new FileFilter() {
            @Override
            public boolean accept( File pathname ) {
                return pathname.isDirectory();
            }
        });

        //May be necessary


        if ( null == files){
            sessionName = directoryName + File.separator + "Session 1";
            System.out.println("null under countFiles");
        }
        else {
            sessionName = directoryName + File.separator + "Session " + ( files.length + 1 );
        }
        session = new File( sessionName );
        if( !session.exists() ) {
            session.mkdirs();
        }

        /** "yyyy_mm_dd_hh_mm" */
        /** Get current date */
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_hh_mm",
                Locale.getDefault());
        sessionName = directoryName + File.separator + dateFormat.format(new Date());

        /** Save photos */
        session = new File(sessionName);
        if(!session.exists()) {
            session.mkdirs();
        }
    }



    /**
     * Function: onRequestPermissionsResult()
     * Purpose: this interface is the contract for receiving the results for permission requests
     * Parameters: int requestCode =
     *             String[] permissions =
     *             int[] grantResults =
     * Return: none
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "Sorry, camera permission is required.", Toast.LENGTH_LONG).show();
            }
        }

    }



    /**
     * Function: SurfaceTextureListener() (not really a function, but a listener initialization)
     * Purpose: this callback gives us a notification when we are ready to prepare the camera device initialization
     *          Note: TextureView is the view which renders captured camera image data
     * Parameters: none
     * Return: new TextureView.SurfaceTextureListener
     */
    private final TextureView.SurfaceTextureListener textureListener
            = new TextureView.SurfaceTextureListener() {

        @Override /**SurfaceTexture captures frames from an image stream */
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openCamera(width, height); /** helper function to open the camera (prepares the camera to take a picture) */
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };




    /**
     * Function: StateCallBack() (not really a function but a callback initialization)
     * Purpose: this creates a callback object for receiving updates about the state of a camera device
     * Parameters: none
     * Return: new CameraDevice.StateCallBack()
     */
    private final CameraDevice.StateCallback mStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraOpenCloseLock.release();
            cameraDevice = camera; /** sets the cameraDevice to this camera */
            createCameraPreviewSession(); /** helper function to create the camera preview */
        }

        @Override /** closes the camera when camera device is no longer available to use */
        public void onDisconnected(CameraDevice camera) {
            mCameraOpenCloseLock.release();
            camera.close();
            cameraDevice = null;
        }

        @Override /** closes the camera when camera device encounters a serious error */
        public void onError(CameraDevice camera, int error) {
            mCameraOpenCloseLock.release();
            camera.close();
            cameraDevice.close();
            cameraDevice = null;
        }
    };



    /**
     * Function: createCameraPreviewSession()
     * Purpose: helper function to create the camera preview (called in CameraDevice.StateCallback() -> onOpened()
     * Parameters: none
     * Return: none
     */
    private void createCameraPreviewSession()  {
        try {
            /** surface textures capture frames from an image stream */
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                throw new AssertionError();
            }

            /**sets the default size of image buffers */
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            /** a surface created from a SurfaceTexture can be used as an output destination for the camera */
            Surface surface = new Surface(texture);

            /** create a capture request for new capture requests, initialized with template for target use case */
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_PREVIEW);
            /** adds surface to the list of targets for this request */
            mPreviewRequestBuilder.addTarget(surface);


            /** Here, we create a CameraCaptureSession for camera preview. */
            cameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            /** The camera is already closed */
                            if (null == cameraDevice) {
                                return;
                            }

                            /** When the session is ready, we start displaying the preview. */
                            mCaptureSession = cameraCaptureSession;
                            try {
                                /** Auto focus should be continuous for camera preview. */
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                /** Flash is automatically enabled when necessary. */
                                setAutoFlash(mPreviewRequestBuilder);

                                /** Finally, we start displaying the camera preview. */
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                           showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    /**
     * Function: setAutoFlash()
     * Purpose: sets up auto flash when capturing photos
     * Parameters: CaptureRequest.Builder requestBuilder =
     * Return: none
     */
    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }




    /**
     * Function: CaptureCallback (not really a function but a callback initialization)
     * Purpose: create a callback to handle events related to JPEG capture
     * Parameters: none
     * Return: CameraCaptureSession.CaptureCallback
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    /** We have nothing to do when the camera preview is working normally. */
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {

                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        /** CONTROL_AE_STATE can be null on some devices */
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();

                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    /** CONTROL_AE_STATE can be null on some devices */
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    /** CONTROL_AE_STATE can be null on some devices */
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();

                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };



    /**
     * Function: runPrecaptureSequence()
     * Purpose:
     * Parameters: none
     * Return: none
     */
    private void runPrecaptureSequence() {
        try {
            /** This is how to tell the camera to trigger. */
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            /** Tell #mCaptureCallback to wait for the pre-capture sequence to be set. */
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }




    /**
     * Function: captureStillPicture()
     * Purpose: capture a still picture. this method should be called when we get a response in {@link #mCaptureCallback}
     * Parameters: none
     * Return: none
     */
    private void captureStillPicture() {
        try {

            List<CaptureRequest> captureList = new ArrayList<>();
            mPreviewRequestBuilder.addTarget(mImageReader.getSurface());
            for ( int requestImage = 0 ; requestImage < 1 ; requestImage++ ) {
                captureList.add(mPreviewRequestBuilder.build());
            }

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.captureBurst(captureList, cameraCaptureCallback, null);
            mPreviewRequestBuilder.removeTarget(mImageReader.getSurface());

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    /**
     * Function: CaptureCallBack() (not really a function, but a callback intialization
     * Purpose: crete a callback object for ____________
     * Parameters: none
     * Return: none
     */
    CameraCaptureSession.CaptureCallback cameraCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {

            if(numberOfImages == 0) {

                showToast("Image Saved!");
                unlockFocus();
                numberOfImages++;

            }else if (numberOfImages < 2) {

                // Log.d(TAG, mFile.toString());
                // Log.d(TAG, session.toString());
                unlockFocus();
                numberOfImages++;

            } else {

                showToast("Image Saved!");
                //showToast((numberOfImages + 1) + " images saved!");
                numberOfImages = 0;

            }

        }
    };



    /**
     * Function: lockFocus()
     * Purpose: lock the focus??
     * Parameters: none
     * Return: none
     */
    private void lockFocus() {
        try {
            /** This is how to tell the camera to lock focus. */
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            /** Tell #mCaptureCallback to wait for the lock. */
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    /**
     * Function: unlockFocus()
     * Purpose: to unlock the focus??
     * Parameters: none
     * Return: none
     */
    private void unlockFocus() {
        try {
            /** Reset the auto-focus trigger */
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            /** After this, the camera will go back to the normal state of preview. */
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    /**
     * Function: updatePreview()
     * Purpose:  never used?
     * Parameters: none
     * Return: none
     */
    private void updatePreview() throws CameraAccessException {
        //returns if cameraDevice is null
        if(cameraDevice == null) {
            return;
        }

        //sets a capture request field to a value;
        //sets control_mode field to control_mode_auto (auto-exposure, auto-white-balance, auto-focus)
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //request endlessly repeating capture of images by this capture session
        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
    }




    /**
     * Function: openCamera()
     * Purpose: helper function to open up the camera ; called in TextureView.SurfaceTextureListener() -> onSurfaceTextureAvailable
     * Parameters: int width =
     *             int height =
     * Return: none
     */
    private void openCamera(int width, int height) throws CameraAccessException {
        /**
         * checks that the user has permissions to access the camera and write data
         * and requests permissions otherwise
         */
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
            return;
        }
        setUpCameraOutputs(width,height);
        configureTransform(width,height);

        /** how to obtain the camera ID of the back camera */
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
        manager.openCamera(mCameraId, mStateCallBack,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }



    /**
     * Function: setUpCameraOutputs()
     * Purpose: set up the camera outputs?
     * Parameters: int width =
     *             int height =
     * Return: none
     */
    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                /** We don't use a front facing camera in this sample. */
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                /** For still image captures, we use the largest available size. */
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/3);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                /** Find out if we need to swap dimension to get the preview size relative to sensor coordinate */
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                Integer mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    textureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    /**
     * Function: chooseOptimalSize()
     * Purpose: idk??
     * Parameters: Size[] choices =
     *             int textureViewWidth =
     *             int textureViewHeight =
     *             int maxWidth =
     *             int maxHeight =
     *             Size aspectRatio =
     * Return: Size??
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }



    /**
     * Function:
     * Purpose:
     * Parameters:
     * Return:
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    /**
     * Function: OnImageAvailableListener()
     * Purpose: (not really a function, but a callback initialization)
     * Parameters: none
     * Return: none
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            newFile();
            image = reader.acquireNextImage();
            mBackgroundHandler.post(new ImageSaver(image, mFile));

        }

    };



    /**
     * Function: newFile()
     * Purpose: to generate a new file in the Android Phone Files
     * Parameters: none
     * Return: none
     */
    public void newFile(){
        thousandsDigit++;

        if( thousandsDigit > 9){
            hundredsDigit++;
            thousandsDigit = 0;
        }

        if(hundredsDigit > 9){
            tenthDigit++;
            hundredsDigit = 0;
        }

        int onesDigit = 0;
        mFile = new File(sessionName + "/Image " + onesDigit +
                tenthDigit + hundredsDigit + thousandsDigit + ".jpg");

    }



    /**
     * Function:
     * Purpose:
     * Parameters:
     * Return:
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         *
         * */
        private final File mFile;

        ImageSaver(Image image, File file) {

            mImage = image;
            mFile = file;

        }


        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);

                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }



    /**
     * Function: showToast()
     * Purpose: show a custom toast to the screen
     * Parameters: final String text = the message to be displayed to the screen
     * Return: none
     */
    private void showToast(final String text) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 300);

            }
        });

    }




    /**
     * Function:
     * Purpose: Configures the necessary {@link android.graphics.Matrix} transformation to 'textureView',
     *          This method should be called after the camera preview size is determined in setUpCameraOutputs and
     *          also the size of 'mTextureView' is fixed.
     * Parameters: int viewWidth =
     *             int viewHeight =
     * Return: none
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize ) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }



    /**
     * Function: onResume()
     * Purpose: this function is called when this activity is resumed after coming from another activity
     * Parameters: none
     * Return: none
     */
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread(); //helper function to start a thread to have concurrent execution

        //check if surfaceTexture associated with this textureView is available for rendering
        if(textureView.isAvailable()) {
            try {
                openCamera(textureView.getWidth(),textureView.getHeight()); //helper function to open the camera
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        //else it will set the surfaceTextureListener (which also calls openCamera())
        else {
            textureView.setSurfaceTextureListener(textureListener);
        }
        /*------------------------------------------------------------*/
        mSensorThread = new HandlerThread("Sensor Thread", Thread.MAX_PRIORITY);
        mSensorThread.start();
        Handler mSensorHandler = new Handler(mSensorThread.getLooper());
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, 25000, mSensorHandler);
        }

        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, 25000, mSensorHandler);
        }
        /*------------------------------------------------------------*/
    }




    /**
     * Function: startBackgroundThread()
     * Purpose: helper function to start a thread to have concurrent execution (called in onResume() )
     * Parameters: none
     * Return: none
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }



    /**
     * Function: onPause()
     * Purpose: this function is called when the current activity (this) is paused to go into another activity
     * Parameters: none
     * Return: none
     */
    @Override
    protected void onPause() {
        super.onPause();

        try {
            stopBackgroundThread(); //helper function to end any threads
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*---------------------------------------------------------------*/
        sensorManager.unregisterListener(this);
        mSensorThread.quitSafely();
        /*---------------------------------------------------------------*/
    }



    /* helper function to end any threads;
     * called in onPause()
     */
    /**
     * Function: stopBackgroundThread()
     * Purpose: helper function to end any threads (called in onPause() )
     * Parameters: none
     * Return: none
     */
    protected void stopBackgroundThread() throws InterruptedException {
        mBackgroundThread.quitSafely();
        mBackgroundThread.join();
        mBackgroundThread = null;
        mBackgroundHandler = null;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    /*----------------------------------------------------------------*/


    /**
     * Function: onAccuracyChanged()
     * Purpose: meh
     * Parameters: meh
     * Return: meh
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




    /**
     *
     * Modifying the accelerometer and magnetometer using the sensors. Checks if the accelerometer
     * and temp values are not close to each other by 0.01
     *
     */
    /**
     * Function: onSensorChanged()
     * Purpose: CONSTANTLY update the accelerometer and magnetometer readings (differences > 0.01) //
     *          initialize/set the MovingAverage object values
     * Parameters: SensorEvent event =
     * Return: none
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] mAccelerometerData = event.values.clone();
            accelerometerMA.addData(mAccelerometerData);
            float[] temp = accelerometerMA.getAverage();
            if(!withinRange(avgAccelerometerData[0], temp[0], (float) 0.01)) {
                avgAccelerometerData[0] = temp[0];
            }
            if(!withinRange(avgAccelerometerData[1], temp[1], (float) 0.01)) {
                avgAccelerometerData[1] = temp[1];
            }
            if(!withinRange(avgAccelerometerData[2], temp[2], (float) 0.01)) {
                avgAccelerometerData[2] = temp[2];
            }

        }  else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] mMagnetometerData = event.values.clone();
            magnetometerMA.addData(mMagnetometerData);
            float[] temp = magnetometerMA.getAverage();
            if(!withinRange(avgMagnetometerData[0], temp[0], (float) 0.01)) {
                avgMagnetometerData[0] = temp[0];
            }
            if(!withinRange(avgMagnetometerData[1], temp[1], (float) 0.01)) {
                avgMagnetometerData[1] = temp[1];
            }
            if(!withinRange(avgMagnetometerData[2], temp[2], (float) 0.01)) {
                avgMagnetometerData[2] = temp[2];
            }

        }

        setOrientation();
    }



    /**
     * Function: setOrientation()
     * Purpose: set the correct [azimuth, pitch, roll] values // helper function to set the correct orientation after
     *          extracting raw data from onSensorChanged
     * Parameters: none
     * Return: none
     */
    private void setOrientation() {
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,null, avgAccelerometerData, avgMagnetometerData);

        float[] rotationMatrixAdjusted = new float[9];
        switch(mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone();
                break;

            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted);
                break;

            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted);
                break;

            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted);
                break;
        }

        float[] orientationValues = new float[3];
        if(rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
        }

        currentOrientation = orientationValues.clone();
        orientationMA.addData(currentOrientation);
        float[] temp = orientationMA.getAverage();

        if(!withinRange(currentAvgOrientation[0], temp[0], (float) 0.02)) {
            currentAvgOrientation[0] = temp[0];
        }
        if(!withinRange(currentAvgOrientation[1], temp[1], (float) 0.02)) {
            currentAvgOrientation[1] = temp[1];
        }
        if(!withinRange(currentAvgOrientation[2], temp[2], (float) 0.02)) {
            currentAvgOrientation[2] = temp[2];
        }
    }



    /**
     * Function: makeToast()
     * Purpose: makes toasts to the screen (this one is meant for feedback after image capture
     * Parameters: Context context =
     *             String message =
     *             int messageLength =
     * Return: Nothing, just prints a toast message
     */
    private void makeToast(Context context, String message, int messageLength) {
        //short = 0, long = 1
        if(mToast!= null) mToast.cancel();
        mToast = Toast.makeText(context, message, messageLength);
        mToast.show();
    }




    /**
     * Function: withinRange()
     * Purpose: test if compared val is +/- 0.1 from constant
     * Parameters: float constant = constant value to compare to
     *             float comparedVal = current value you want to check
     *             float range = the maximum difference between the two values
     * Return: true if within range
     *         false otherwise
     */
    private boolean withinRange(float constant, float comparedVal, float range) {
        if(Math.abs(constant - comparedVal) > range)
            return false;
        return true;
    }



    /**
     * Function: setStartingPosition()
     * Purpose: initialize the current sensor position of the phone (starts the initial checkpoint in rotation)
     * Parameters: none
     * Return: none
     */
    private void setStartingPosition() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while(flag) {
                    if(Math.abs(currentAvgOrientation[1]) > 1.2) {
                        makeToast(getApplicationContext(), "Angle your phone down a bit more.", Toast.LENGTH_SHORT);
                    } else {
                        flag = false;
                    }
                }
            }
        });

        globalOrientation[0] = currentAvgOrientation[0];
        globalOrientation[1] = currentAvgOrientation[1];
        globalOrientation[2] = currentAvgOrientation[2];
    }



    /**
     * Function: setCheckPoint()
     * Purpose: set a new checkpoint in the rotation
     * Parameters: none
     * Return: none
     */
    private void setCheckPoint() {
        previousOrientation[0] = currentAvgOrientation[0];
        previousOrientation[1] = currentAvgOrientation[1];
        previousOrientation[2] = currentAvgOrientation[2];
    }




    /**
     * Function: sessionLoop()
     * Purpose: creates a loop such that when the user moves around an object in a circle, this function
     *          will capture photos automatically at every 10 degree change in position
     *          Note: sessionLoop is important since it allows the camera to capture images using withinPitchRange
     *                method and some other checkpoint within MATH.ABS. Here is where we need to update the
     *                progress bar. This is interesting since the images captured are used in a runnable method.
     *                This means that capturing images and gathering data from the accelerometer and magnetometer
     *                happen at the same time asynchronously.
     * Parameters: none
     * Return: none
     */
    private void sessionLoop() {
        takePicture2();
        setCheckPoint();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (SWITCH) {
                    //only moves forward to capture a photo if within pitch range

                    if (Math.abs(Math.abs(currentAvgOrientation[2]) - Math.abs(previousOrientation[2])) >= 0.1396263402) {
                        takePicture2(); //helper function to take a picture when button is clicked
                        setCheckPoint();
                    }
                }
            }
        }).start();

    }
}
