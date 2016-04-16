package ca.fuwafuwa.kaku;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
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
import android.view.OrientationEventListener;
import android.view.WindowManager;

import java.nio.ByteBuffer;

/**
 * Created by Xyresic on 4/9/2016.
 */
public class MainService extends Service {

    public static final String TAG = MainService.class.getName();

    private WindowManager windowManager;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;

    private int mWidth;
    private int mHeight;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    private OrientationChangeCallback mOrientationChangeCallback;

    MainServiceHandler mHandler;

    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";
    public static final String EXTRA_RESULT_INTENT = "EXTRA_RESULT_INTENT";

    private class OrientationChangeCallback extends OrientationEventListener {

        public OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            synchronized (this){
                Log.d(TAG, "Orientation changed");
                if (mVirtualDisplay != null){
                    mVirtualDisplay.release();
                }
                createVirtualDisplay();
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback{
        @Override
        public void onStop(){
            Log.e(TAG, "Stopping projection");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null){
                        mVirtualDisplay.release();
                    }
                    if (mOrientationChangeCallback != null){
                        mOrientationChangeCallback.disable();
                    }
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Intent mIntent = (Intent) intent.getExtras().get(EXTRA_RESULT_INTENT);
        int resultCode = intent.getExtras().getInt(EXTRA_RESULT_CODE);

        mHandler = new MainServiceHandler(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, mIntent);
        mOrientationChangeCallback = new OrientationChangeCallback(this);
        mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);

        createVirtualDisplay();

        new CaptureWindow(this);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public Handler getHandler(){
        return mHandler;
    }

    public Bitmap getScreenshot(){

        Bitmap bitmap = null;
        Image image = mImageReader.acquireLatestImage();

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
        }

        return bitmap;
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
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(getClass().getName(), mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
    }
}
