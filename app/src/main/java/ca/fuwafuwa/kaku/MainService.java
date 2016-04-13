package ca.fuwafuwa.kaku;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by 0x1bad1d3a on 4/9/2016.
 */
public class MainService extends Service implements MoveCallback {

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private Handler mHandler;
    private WindowManager windowManager;
    private int mWidth;
    private int mHeight;

    private View captureBox;
    private WindowManager.LayoutParams params;

    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;

    public static final String TAG = MainService.class.getName();
    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";
    public static final String EXTRA_RESULT_INTENT = "EXTRA_RESULT_INTENT";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Intent mIntent = (Intent) intent.getExtras().get(EXTRA_RESULT_INTENT);
        int resultCode = intent.getExtras().getInt(EXTRA_RESULT_CODE);

        windowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, mIntent);

        if (mMediaProjection != null){
            File externalFilesDir = getExternalFilesDir(null);
            if (externalFilesDir != null){
                STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
                File storeDirectory = new File(STORE_DIRECTORY);
                if (!storeDirectory.exists()){
                    boolean success = storeDirectory.mkdirs();
                    if (!success){
                        Log.e(TAG, "Failed to create final storage directory");
                        return START_STICKY;
                    }
                }
            }
            else {
                Log.e(TAG, "Failed to create file storage directory, getExternalFilesDir is null");
                return START_STICKY;
            }

            createVirtualDisplay();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
        initUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void saveImage(){
        Image image = null;
        FileOutputStream fos = null;
        Bitmap bitmap = null;

        try {
            image = mImageReader.acquireLatestImage();
            if (image != null){
                Log.e(TAG, String.format("Image Dimensions: %dx%d", image.getWidth(), image.getHeight()));

                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                Log.e(TAG, String.format("pixelStride: %s | rowStride: %s | rowPadding %s", pixelStride, rowStride, rowPadding));

                // create bitmap
                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight);

                Log.e(TAG, "pixelStride: " + pixelStride);
                Log.e(TAG, "rowStride: " + rowStride);
                Log.e(TAG, "rowPadding: " + rowPadding);
                Log.e(TAG, "mWidth: " + mWidth);
                Log.e(TAG, "mHeight: " + mHeight);

                // write bitmap to file
                fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen " + IMAGES_PRODUCED + ".png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                IMAGES_PRODUCED++;
                Log.e(TAG, "Captured image: " + IMAGES_PRODUCED);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (fos != null){
                try {
                    fos.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (bitmap != null){
                bitmap.recycle();
            }
            if (image != null){
                image.close();
            }
        }
    }

    private void createVirtualDisplay(){

        // display metrics
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int mDensity = metrics.densityDpi;
        Display mDisplay = windowManager.getDefaultDisplay();

        // get width and height
        Point size = new Point();
        mDisplay.getRealSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screencap", mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
    }

    private void initUI(){
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);
        captureBox = vi.inflate(R.layout.capture_box, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0,
                100,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        captureBox.getLayoutParams();

        windowManager.addView(captureBox, params);

        ((CaptureBox) captureBox.findViewById(R.id.capture_box)).registerMoveCallback(this);
        ((CaptureBox) captureBox.findViewById(R.id.capture_box)).registerMoveCallback(this);
    }

    public void moveCallback(float x, float y) {
        params.x = (int) x;
        params.y = (int) y;
        windowManager.updateViewLayout(captureBox, params);
        Log.e(TAG, String.format("%f %f", x, y));
    }

    public void screenshotCallback(){
        saveImage();
    }
}