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

import java.util.HashMap;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.CaptureWindow;
import ca.fuwafuwa.kaku.Windows.Window;
import ca.fuwafuwa.kaku.Windows.WindowCoordinator;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

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
            boolean imagePreview = prefs.getBoolean(Constants.KAKU_PREF_IMAGE_FILTER, true);
            prefs.edit().putBoolean(Constants.KAKU_PREF_IMAGE_FILTER, !imagePreview).apply();

            KakuTools.startKakuService(context, new Intent(context, MainService.class));
        }
    }

    public static class TogglePageModeMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SharedPreferences prefs = context.getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            TextDirection textDirection = TextDirection.valueOf(prefs.getString(Constants.KAKU_PREF_TEXT_DIRECTION, TextDirection.AUTO.toString()));
            textDirection = TextDirection.Companion.getByValue((textDirection.ordinal() + 1) % 3);
            prefs.edit().putString(Constants.KAKU_PREF_TEXT_DIRECTION, textDirection.toString()).apply();

            KakuTools.startKakuService(context, new Intent(context, MainService.class));
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

            KakuTools.startKakuService(context, new Intent(context, MainService.class));
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
    private WindowCoordinator mWindowCoordinator;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "CREATING MAINSERVICE: " + System.identityHashCode(this));

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mWindowCoordinator = new WindowCoordinator(this);
        mHandler = new MainServiceHandler(this, mWindowCoordinator);

        startForeground(NOTIFICATION_ID, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");

        if (intent.getExtras() != null &&
            intent.getExtras().containsKey(Constants.EXTRA_PROJECTION_RESULT_CODE) &&
            intent.getExtras().containsKey(Constants.EXTRA_PROJECTION_RESULT_INTENT))
        {
            mProjectionResultIntent = (Intent) intent.getExtras().get(Constants.EXTRA_PROJECTION_RESULT_INTENT);
            mProjectionResultCode = intent.getExtras().getInt(Constants.EXTRA_PROJECTION_RESULT_CODE);
        }

        // re-init CaptureWindow as prefs may have changed
        mWindowCoordinator.getWindow(Constants.WINDOW_CAPTURE).reInit(new Window.ReinitOptions());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, getNotification());

        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        final int rotation = mDisplay.getRotation();

        if (rotation != mRotation)
        {
            Log.d(TAG, "Orientation changed");
            mRotation = rotation;
            createVirtualDisplay();
            mWindowCoordinator.reinitAllWindows();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DESTORYING MAINSERVICE: " + System.identityHashCode(this));
        mWindowCoordinator.stopAllWindows();
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
     * such a case onConfigurationChanged will not trigger and {@link Window#reInit(ca.fuwafuwa.kaku.Windows.Window.ReinitOptions)} will not
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

        Prefs prefs = KakuTools.getPrefs(this);

        String contentTitle = "Kaku";
        switch (prefs.getTextDirectionSetting())
        {
            case AUTO:
                contentTitle = "Kaku is determining text direction automatically";
                break;
            case VERTICAL:
                contentTitle = "Kaku is reading text vertically";
                break;
            case HORIZONTAL:
                contentTitle = "Kaku is reading text horizontally";
                break;
        }

        Notification n = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.kaku_notification_icon)
                .setContentTitle(contentTitle)
                .setContentText(String.format("Black and white filter %s, instant mode %s", prefs.getImageFilterSetting() ? "on" : "off", prefs.getInstantModeSetting() ? "on" : "off"))
                //.setContentText(String.format("Black and white filter %s", prefs.getImageFilterSetting() ? "on" : "off"))
                .setContentIntent(closeMainService)
                .addAction(0, "Text Direction", togglePageMode)
                .addAction(0, "Image Filter", toggleImagePreview)
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
    private String createNotificationChannel()
    {
        String channelId = Constants.KAKU_CHANNEL_ID;
        String channelName = Constants.KAKU_CHANNEL_NAME;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(channel);

        return channelId;
    }
}
