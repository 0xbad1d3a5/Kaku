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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;
import ca.fuwafuwa.kaku.Windows.Window;

import static androidx.core.app.NotificationCompat.FLAG_FOREGROUND_SERVICE;
import static androidx.core.app.NotificationCompat.FLAG_ONGOING_EVENT;

/**
 * Created by 0xbad1d3a5 on 4/9/2016.
 */
public class MainService extends Service implements Stoppable {

    private static final String TAG = MainService.class.getName();

    public static class CloseMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "GOT CLOSE");
            context.stopService(new Intent(context, MainService.class));
        }
    }

    public static class ToggleImagePreviewMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SharedPreferences prefs = context.getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            boolean imagePreview = prefs.getBoolean(Constants.KAKU_PREF_SHOW_PREVIEW_IMAGE, true);
            prefs.edit().putBoolean(Constants.KAKU_PREF_SHOW_PREVIEW_IMAGE, !imagePreview).apply();

            Intent i = new Intent(context, MainService.class).putExtra(Constants.EXTRA_TOGGLE_IMAGE_PREVIEW, 0);
            KakuTools.startKakuService(context, i);
        }
    }

    public static class TogglePageModeMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SharedPreferences prefs = context.getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            boolean pageMode = prefs.getBoolean(Constants.KAKU_PREF_HORIZONTAL_TEXT, true);
            prefs.edit().putBoolean(Constants.KAKU_PREF_HORIZONTAL_TEXT, !pageMode).apply();

            Intent i = new Intent(context, MainService.class).putExtra(Constants.EXTRA_TOGGLE_PAGE_MODE, 0);
            KakuTools.startKakuService(context, i);
        }
    }

    public static class ToggleInstantModeMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SharedPreferences prefs = context.getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            boolean pageMode = prefs.getBoolean(Constants.KAKU_PREF_INSTANT_MODE, true);
            prefs.edit().putBoolean(Constants.KAKU_PREF_INSTANT_MODE, !pageMode).apply();

            Intent i = new Intent(context, MainService.class).putExtra(Constants.EXTRA_TOGGLE_INSTANT_MODE, 0);
            KakuTools.startKakuService(context, i);
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
    private static final int NOTIFICATION_ID = 1;

    private Intent mProjectionResultIntent;
    private int mProjectionResultCode;
    private boolean mShowPreviewImage;
    private boolean mHorizontalText;
    private boolean mInstantMode;

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
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (intent.getExtras().containsKey(Constants.EXTRA_PROJECTION_RESULT_CODE) &&
            intent.getExtras().containsKey(Constants.EXTRA_PROJECTION_RESULT_INTENT))
        {
            mProjectionResultIntent = (Intent) intent.getExtras().get(Constants.EXTRA_PROJECTION_RESULT_INTENT);
            mProjectionResultCode = intent.getExtras().getInt(Constants.EXTRA_PROJECTION_RESULT_CODE);
        }

        if (mCaptureWindow == null){
            mCaptureWindow = new CaptureWindow(this, mShowPreviewImage, mHorizontalText, mInstantMode);
        }
        else {
            Log.d(TAG, "onStartCommand - Reinitializing CaptureWindow");

            // Note: must call getNotificaton() before reinit-ing the CaptureWindow, since it sets some global variables... probably should refactor this to be more clean
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, getNotification());

            mCaptureWindow.reInit();
            mCaptureWindow.reInitOcr(mShowPreviewImage, mHorizontalText, mInstantMode);
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
        startForeground(NOTIFICATION_ID, getNotification());
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
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mProjectionResultCode, mProjectionResultIntent);
            mMediaProjectionStopCallback = new MediaProjectionStopCallback();
            mMediaProjection.registerCallback(mMediaProjectionStopCallback, mHandler);
        }
        createVirtualDisplay();
    }

    public Handler getHandler(){
        return mHandler;
    }

    public Image getScreenshot() throws InterruptedException {
        long startTime = System.nanoTime();
        Image image = mImageReader.acquireLatestImage();
        while (image == null && System.nanoTime() < startTime + 2000000000){
            Thread.sleep(20);
            image = mImageReader.acquireLatestImage();
        }
        return image;
    }

    private Notification getNotification()
    {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channelId = createNotificationChannel();
        }
        else {
            channelId = "";
        }

        PendingIntent toggleImagePreview = PendingIntent.getBroadcast(this, Constants.REQUEST_SERVICE_TOGGLE_IMAGE_PREVIEW, new Intent(this, ToggleImagePreviewMainService.class), 0);
        PendingIntent togglePageMode = PendingIntent.getBroadcast(this, Constants.REQUEST_SERVICE_TOGGLE_PAGE_MODE, new Intent(this, TogglePageModeMainService.class), 0);
        PendingIntent toggleInstantMode = PendingIntent.getBroadcast(this, Constants.REQUEST_SERVICE_TOGGLE_INSTANT_MODE, new Intent(this, ToggleInstantModeMainService.class), 0);
        PendingIntent closeMainService = PendingIntent.getBroadcast(this, Constants.REQUEST_SERVICE_SHUTDOWN, new Intent(this, CloseMainService.class), 0);

        SharedPreferences prefs = getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
        mShowPreviewImage = prefs.getBoolean(Constants.KAKU_PREF_SHOW_PREVIEW_IMAGE, true);
        mHorizontalText = prefs.getBoolean(Constants.KAKU_PREF_HORIZONTAL_TEXT, true);
        mInstantMode = prefs.getBoolean(Constants.KAKU_PREF_INSTANT_MODE, true);

        Notification n = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.kaku_notification_icon)
                .setContentTitle(String.format("Kaku is reading text %s", mHorizontalText ? "horizontally" : "vertically"))
                .setContentText(String.format("Black and white filter %s, instant mode %s", mShowPreviewImage ? "on" : "off", mInstantMode ? "on" : "off"))
                .setContentIntent(closeMainService)
                .addAction(0, "Text Direction", togglePageMode)
                .addAction(0, "Filter Image", toggleImagePreview)
                .addAction(0, "Instant Mode", toggleInstantMode)
                .build();
        n.flags = FLAG_ONGOING_EVENT | FLAG_FOREGROUND_SERVICE;

        return n;
    }

    private void createVirtualDisplay()
    {
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
