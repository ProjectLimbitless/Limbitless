package com.example.limbitlesssummerproject19;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    Button button_capture; //Button on camera preview to capture image
    TextureView textureView; //The camera preview itself

    // used for orientation correction of photos
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId; //unique ID used to access the camera
    CameraDevice cameraDevice; //representation of a single camera connected to the device
    CameraCaptureSession cameraCaptureSession; //configured capture session; used for capturing images from the camera
    CaptureRequest captureRequest; //package of settings/outputs needed to capture a single image
    CaptureRequest.Builder captureRequestBuilder; //how to create the capture request

    private Size imageDimension; //describe width & height in pixels
    private ImageReader imageReader; //allows app to access image data rendered into a Surface
    private File file; //file where photo will be stored
    Handler mBackgroundHandler; //to schedule actions to be executed at some point in the future
    HandlerThread mBackgroundThread; //the thread associated with the handler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = (TextureView) findViewById(R.id.texture);
        button_capture = (Button) findViewById(R.id.button_capture);


        textureView.setSurfaceTextureListener(textureListener);

        //set a listener to respond when the camera capture button is clicked
        button_capture.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    takePicture(); //helper function to take a picture when button is clicked
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //this interface is the contract for receiving the results for permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "Sorry, camera permission is required.", Toast.LENGTH_LONG).show();
            }
        }

    }

    //TextureView is the view which renders captured camera image data
    //This callback gives us a notification when we are read to prepare for the camera device initialization
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override //SurfaceTexture captures frames from an image stream
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openCamera(); //helper function to open the camera (prepares the camera to take a picture)
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    //callback objects for receiving updates about the state of a camera device
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera; //sets the cameraDevice to this camera
            try {
                createCameraPreview(); //helper function to create the camera preview
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override //closes the camera when camera device is no longer available to use
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override //closes the camera when camera device encounters a serious error
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    /* helper function to create the camera preview;
     * called in CameraDevice.StateCallback() -> onOpened()
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createCameraPreview() throws CameraAccessException {
        //surface textures capture frames from an image stream
        SurfaceTexture texture = textureView.getSurfaceTexture();

        //sets the default size of image buffers
        texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

        //a surface created from a SurfaceTexture can be used as an output destination for the camera
        Surface surface = new Surface(texture);

        //create a capture request for new capture requests, initialized with template for target use case
        captureRequestBuilder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_PREVIEW);
        //adds surface to the list of targets for this request
        captureRequestBuilder.addTarget(surface);

        //creates a new camera capture session by providing the target output set of Surfaces to the camera device
        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override //called when camera device has finished configuring itself, session can now start processing capture requests
            public void onConfigured(CameraCaptureSession session) {
                if(cameraDevice == null) {
                    return;
                }
                cameraCaptureSession = session; //set the cameraCaptureSession
                try {
                    updatePreview(); //helper function to update the camera preview
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override //called if the session cannot be configured as requested
            public void onConfigureFailed(CameraCaptureSession session) {
                Toast.makeText(getApplicationContext(), "Configuration Changed", Toast.LENGTH_LONG).show();
            }
        }, null);
    }

    /* helper function to update the preview
     * called in createCameraPreview() -> cameraDevice.createCaptureSession(___ , StateCallBack() -> onConfigured, ____ );
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updatePreview() throws CameraAccessException {
        //returns if cameraDevice is null
        if(cameraDevice == null) {
            return;
        }

        //sets a capture request field to a value;
        //sets control_mode field to control_mode_auto (auto-exposure, auto-white-balance, auto-focus)
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //request endlessly repeating capture of images by this capture session
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
    }

    /* helper function to open up the camera;
     * called in TextureView.SurfaceTextureListener() -> onSurfaceTextureAvailable;
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() throws CameraAccessException {

        //how to obtain the camera ID of the back camera
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraId = manager.getCameraIdList()[0];

        //with cameraID, now obtain the characteristics of that camera (what it can do)
        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        //class to store the available stream to prepare for a capture session
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        //gets the image dimensions from the streamConfigurationMap
        imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

        //checks that the user has permissions to access the camera and write data
        //and requests permissions otherwise
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return;
        }
        manager.openCamera(cameraId, stateCallBack, null);
    }


    /*helper function to take a picture when button is clicked;
      called in onCreate -> button_capture.setOnClickListener()
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePicture() throws CameraAccessException {
        //automatically returns if camera is null
        if(cameraDevice == null) {
            return;
        }

        //getSystemService() = returns the handle to a system-level service by class
        //CameraManager is a system service manager for detecting, characterizing, and connecting to CameraDevice
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //getCameraCharacteristics() = queries the capabilities of a camera device
        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

        Size[] jpegSizez = null;

        //get() = get a camera characteristics field value
        /* SCALAR_STREAM_CONFIGURATION_MAP = the available stream configurations that this camera device supports;
         * also includes the minimum frame durations and the stall durations for each format/size combination
         */
        //getOutputSizes = retrieves a list of sizes compatible with "JPEG" to use as an output
        jpegSizez = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        //default height and width
        int width = 640;
        int height = 480;

        //if list of JPEG sizes is not null, then it will grab a width/height from that list
        if(jpegSizez != null && jpegSizez.length > 0) {
            width = jpegSizez[0].getWidth();
            height = jpegSizez[0].getHeight();
        }

        //ImageReader class allows direct application access to image data rendered into a Surface
        //newInstance() = creates a new reader for images of the desired size and format
        ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

        //List of surfaces -> Surfaces are handles onto raw buffers that are managed by the screen compositor
        List<Surface> outputSurfaces = new ArrayList<>(2);

        //reader.getSurface = returns a surface as a drawing target, used to produce Image for the ImageReader
        outputSurfaces.add(reader.getSurface());

        //adds a new Surface from the existing SurfaceTexture
        outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

        //TEMPLATE_STILL_CAPTURE is used to prioritize image quality over frame rate
        //CaptureRequest defines the parameters for camera device (e.g. exposition, resolution)
        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

        //adds a surface to list of targets for this request
        captureBuilder.addTarget(reader.getSurface());

        //initializes the capture request (the way the photo will be taken)
        //CONTROL_MODE = overall mode of 3A(auto-exposure, auto-white-balance, auto-focus)
        //CONTROL_MODE_AUTO = use settings for each individual 3A routine (manual control of capture parameters is disabled)
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //used for orientation correction of the photo
        int rotation = getWindowManager().getDefaultDisplay().getRotation(); //gets current orientation of photo
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation)); //corrects orientation

        //used to make the photo name a time stamp of when the photo was taken
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        //initializes a file to write the photo to with the jpg extension and the name from above
        file = new File(Environment.getExternalStorageDirectory() + "/" + ts + ".jpg");

        //calback interface for being notified that a new image is available
        ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;
                image = reader.acquireLatestImage(); //acquires latest image from the ImageReader's queue

                //creates a byte buffer from the image (how the image is stored in memory)
                //getPlanes() = returns the array of pixel planes for the image,
                //getBuffer() = returns byte buffer
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()]; //initializes array of bytes in order to write to file

                //writes the byte buffer into the array of bytes
                buffer.get(bytes);
                try {
                    save(bytes); //helper function to save bytes
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(image != null) {
                        image.close(); //always closes the image afterwards
                    }
                }
            }
        };

        //registers a listener to be invoked when a new image becomes available from the ImageReader
        //invokes the listener on the Backrgound Handler
        reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

        //callback for tracking the progress of a captureRequest submitted to the camera device
        final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                try {
                    createCameraPreview(); //creates new cameraPreview if capture is successful
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };

        //creates another capture session afterwards (to the user, the camera just stays open and the cycle is repeated
        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                try {
                    session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {

            }
        }, mBackgroundHandler);
    }

    /* helper function to save bytes
     * called in takePicture() -> ImageReader.onImageAvailableListener -> onImageAvailable();
     */
    private void save(byte[] bytes) throws IOException {
        OutputStream outputStream = null;

        outputStream = new FileOutputStream(file);

        outputStream.write(bytes);
    }

    //this function is called when this activity is resumed after coming from another activity
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread(); //helper function to start a thread to have concurrent execution

        //check if surfaceTexture associated with this textureView is available for rendering
        if(textureView.isAvailable()) {
            try {
                openCamera(); //helper function to open the camera
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        //else it will set the surfaceTextureListener (which also calls openCamera())
        else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    /* helper function to start a thread to have concurrent execution;
     * called in onResume()
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    //this function is called when the current activity (this) is paused to go into another activity
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onPause() {
        super.onPause();

        try {
            stopBackgroundThread(); //helper function to end any threads
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* helper function to end any threads;
     * called in onPause()
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void stopBackgroundThread() throws InterruptedException {
        mBackgroundThread.quitSafely();
        mBackgroundThread.join();
        mBackgroundThread = null;
        mBackgroundHandler = null;
    }
}
