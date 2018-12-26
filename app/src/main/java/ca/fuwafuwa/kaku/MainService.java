package ca.fuwafuwa.kaku;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.concurrent.TimeoutException;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;
import ca.fuwafuwa.kaku.Windows.Window;

/**
 * Created by 0xbad1d3a5 on 4/9/2016.
 */
public class MainService extends Service implements Stoppable {

    private static final String TAG = MainService.class.getName();

    public static class CloseMainService extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "GOT CLOSE");
            context.stopService(new Intent(context, MainService.class));
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
                        mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                    }
                }
            });
        }
    }

    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    public static final String EXTRA_RESULT_INTENT = "EXTRA_RESULT_INTENT";
    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    public static final String KAKU_PREF_FILE = "ca.fuwafuwa.kaku";

    public static final String KAKU_TOGGLE_IMAGE_PREVIEW = "KAKU_TOGGLE_IMAGE_PREVIEW";
    public static final String KAKU_TOGGLE_PAGE_MODE = "KAKU_TOGGLE_PAGE_MODE";

    public static final String KAKU_PREF_SHOW_PREVIEW_IMAGE = "ShowPreviewImage";
    public static final String KAKU_PREF_HORIZONTAL_TEXT = "HorizontalText";

    private static final int RESTART_SERVICE_FOR_IMAGE_PREVIEW = 300;
    private static final int RESTART_SERVICE_FOR_PAGE_MODE = 400;


    private Intent mIntent;
    private int mResultCode;
    private boolean mShowPreviewImage;
    private boolean mHorizontalText;

    private WindowManager mWindowManager;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private MainServiceHandler mHandler;

    private int mRotation;
    private Point mRealDisplaySize = new Point();

    private MediaProjectionStopCallback mMediaProjectionStopCallback;
    private CaptureWindow mCaptureWindow;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d(TAG, "onStartCommand");

        mHandler = new MainServiceHandler(this);
        mIntent = (Intent) intent.getExtras().get(EXTRA_RESULT_INTENT);
        mResultCode = intent.getExtras().getInt(EXTRA_RESULT_CODE);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (mCaptureWindow == null){
            mCaptureWindow = new CaptureWindow(this, mShowPreviewImage, mHorizontalText);
        }
        else {
            mCaptureWindow.reInit();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        final int rotation = mDisplay.getRotation();

        if (rotation != mRotation){
            Log.d(TAG, "Orientation changed");
            mRotation = rotation;
            createVirtualDisplay();
            mCaptureWindow.reInit();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CREATING MAINSERVICE: " + System.identityHashCode(this));

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channelId = createNotificationChannel();
        }
        else {
            channelId = "";
        }

        SharedPreferences prefs = getSharedPreferences(KAKU_PREF_FILE, Context.MODE_PRIVATE);

        mShowPreviewImage = prefs.getBoolean(KAKU_PREF_SHOW_PREVIEW_IMAGE, true);
        Intent toggleImagePreviewIntent = new Intent(this, MainActivity.class).putExtra(KAKU_TOGGLE_IMAGE_PREVIEW, 0);

        mHorizontalText = prefs.getBoolean(KAKU_PREF_HORIZONTAL_TEXT, true);
        Intent togglePageMode = new Intent(this, MainActivity.class).putExtra(KAKU_TOGGLE_PAGE_MODE, 0);

        Notification n = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Kaku is Running")
                .setContentText(String.format("Tap Here to Close - Currently in %s", mHorizontalText ? "Horizontal Mode" : "Vertical Mode"))
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(this, CloseMainService.class), 0))
                .addAction(0, mShowPreviewImage ? "Threshold Off" : "Threshold On", PendingIntent.getActivity(this, RESTART_SERVICE_FOR_IMAGE_PREVIEW, toggleImagePreviewIntent, 0))
                .addAction(0, mHorizontalText ? "Vertical Mode" : "Horizontal Mode", PendingIntent.getActivity(this, RESTART_SERVICE_FOR_PAGE_MODE, togglePageMode, 0))
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

    /**
     * This function is here as a bug fix against {@link #onConfigurationChanged(Configuration)} not
     * triggering when the app is first started and immediately switches to another orientation. In
     * such a case onConfigurationChanged will not trigger and {@link Window#reInit()} will not
     * update the LayoutParams.
     */
    public void onCaptureWindowFinishedInitializing(){
        if (mMediaProjection == null){
            Log.d(TAG, "mMediaProjection is null");
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mIntent);
            mMediaProjectionStopCallback = new MediaProjectionStopCallback();
            mMediaProjection.registerCallback(mMediaProjectionStopCallback, mHandler);
        }
        createVirtualDisplay();
    }

    public Handler getHandler(){
        return mHandler;
    }

    public Image getScreenshot() throws TimeoutException, InterruptedException {
        long startTime = System.nanoTime();
        Image image = mImageReader.acquireLatestImage();
        while (image == null && System.nanoTime() < startTime + 2000000000){
            Thread.sleep(20);
            image = mImageReader.acquireLatestImage();
        }
        return image;
    }

    public Point getRealDisplaySize(){
        return mRealDisplaySize;
    }

    private void createVirtualDisplay(){

        // display metrics
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int mDensity = metrics.densityDpi;
        mDisplay = mWindowManager.getDefaultDisplay();

        // get width and height
        mDisplay.getRealSize(mRealDisplaySize);

        // start capture reader
        Log.e(TAG, String.format("Starting Projection: %dx%d", mRealDisplaySize.x, mRealDisplaySize.y));
        if (mVirtualDisplay != null){
            mVirtualDisplay.release();
        }
        mImageReader = ImageReader.newInstance(mRealDisplaySize.x, mRealDisplaySize.y, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(getClass().getName(), mRealDisplaySize.x, mRealDisplaySize.y, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "kaku_service";
        String channelName = "Kaku Background Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = getSystemService(Context.NOTIFICATION_SERVICE) instanceof NotificationManager ? (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) : null;
        service.createNotificationChannel(channel);
        return channelId;
    }
}
