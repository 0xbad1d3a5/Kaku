package ca.fuwafuwa.kaku;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;

import java.util.concurrent.TimeoutException;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;

/**
 * Created by Xyresic on 4/9/2016.
 */
public class MainService extends Service implements Stoppable {

    private  static final String TAG = MainService.class.getName();

    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";
    public static final String EXTRA_RESULT_INTENT = "EXTRA_RESULT_INTENT";

    private WindowManager mWindowManager;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private MainServiceHandler mHandler;

    private int mRotation;
    private Point realDisplaySize = new Point();

    private MediaProjectionStopCallback mMediaProjectionStopCallback;
    private OrientationChangeCallback mOrientationChangeCallback;
    private CaptureWindow mCaptureWindow;


    public static class CloseMainService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "GOT CLOSE");
            context.stopService(new Intent(context, MainService.class));
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        public OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            synchronized (this){
                final int rotation = mDisplay.getRotation();
                if (rotation != mRotation){

                    mRotation = rotation;
                    Log.d(TAG, "Orientation changed");

                    createVirtualDisplay();

                    mCaptureWindow.reInit();
                }
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
                if (MediaProjectionStopCallback.this == mMediaProjectionStopCallback){
                    if (mVirtualDisplay != null){
                        mVirtualDisplay.release();
                    }
                    if (mOrientationChangeCallback != null){
                        mOrientationChangeCallback.disable();
                    }
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
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

        Log.d(TAG, "New onStartCommand");

        Intent mIntent = (Intent) intent.getExtras().get(EXTRA_RESULT_INTENT);
        int resultCode = intent.getExtras().getInt(EXTRA_RESULT_CODE);

        mHandler = new MainServiceHandler(this);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (mMediaProjection == null){
            Log.d(TAG, "mMediaProjection is null");
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, mIntent);
            mMediaProjectionStopCallback = new MediaProjectionStopCallback();
            mMediaProjection.registerCallback(mMediaProjectionStopCallback, mHandler);
        }

        mOrientationChangeCallback = new OrientationChangeCallback(this);
        if (mOrientationChangeCallback.canDetectOrientation()){
            mOrientationChangeCallback.enable();
        }

        createVirtualDisplay();

        if (mCaptureWindow == null){
            Log.d(TAG, "CaptureWindow is null");
            mCaptureWindow = new CaptureWindow(this);
        }
        else {
            mCaptureWindow.reInit();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CREATING MAINSERVICE: " + System.identityHashCode(this));
        Notification n = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Kaku is running")
                .setContentText("Tap here to close Kaku")
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(this, CloseMainService.class), 0))
                .build();

        startForeground(1, n);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DESTORYING MAINSERVICE: " + System.identityHashCode(this));
        mCaptureWindow.stop();
        stop();
        super.onDestroy();
    }

    @Override
    public void stop() {
        if (mMediaProjection != null){
            mMediaProjection.stop();
        }
    }

    public Handler getHandler(){
        return mHandler;
    }

    public Image getScreenshot() throws TimeoutException {
        long startTime = System.currentTimeMillis();
        Image image = mImageReader.acquireLatestImage();
        while (image == null && startTime + 1000 > System.currentTimeMillis()){
            image = mImageReader.acquireLatestImage();
        }
        return image;
    }

    public Point getRealDisplaySize(){
        return realDisplaySize;
    }

    private void createVirtualDisplay(){

        // display metrics
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int mDensity = metrics.densityDpi;
        mDisplay = mWindowManager.getDefaultDisplay();

        // get width and height
        mDisplay.getRealSize(realDisplaySize);

        // start capture reader
        Log.e(TAG, "Starting Projection");
        if (mVirtualDisplay != null){
            mVirtualDisplay.release();
        }
        mImageReader = ImageReader.newInstance(realDisplaySize.x, realDisplaySize.y, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(getClass().getName(), realDisplaySize.x, realDisplaySize.y, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
    }
}
