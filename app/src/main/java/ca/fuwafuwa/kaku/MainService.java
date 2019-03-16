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
import android.widget.Toast;

import ca.fuwafuwa.kaku.Interfaces.Stoppable;
import ca.fuwafuwa.kaku.Windows.Window;
import ca.fuwafuwa.kaku.Windows.WindowCoordinator;

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

    public static class ToggleShowHideMainService extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            SharedPreferences prefs = context.getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            boolean shown = prefs.getBoolean(Constants.KAKU_PREF_SHOW_HIDE, true);
            prefs.edit().putBoolean(Constants.KAKU_PREF_SHOW_HIDE, !shown).apply();

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
            Log.d(TAG, "Stopping projection");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (MediaProjectionStopCallback.this == mMediaProjectionStopCallback){
                        if (mVirtualDisplay != null){
                            mVirtualDisplay.release();
                        }
                        mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                        mMediaProjection = null;
                        mImageReader.close();
                    }
                }
            });
        }
    }

    private static boolean isKakuRunning = false;

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
    private WindowCoordinator mWindowCoordinator = new WindowCoordinator(this);

    @Override
    public IBinder onBind(Intent intent)
    {
        // Not used
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        if (!isKakuRunning)
        {
            SharedPreferences prefs = getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(Constants.KAKU_PREF_SHOW_HIDE, true).apply();
        }

        Log.d(TAG, "CREATING MAINSERVICE: " + System.identityHashCode(this));
        Toast.makeText(this, "Starting capture window...", Toast.LENGTH_LONG).show();

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mHandler = new MainServiceHandler(this, mWindowCoordinator);

        // Set preferences for ratings
        SharedPreferences prefs = getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
        int timesLaunched = prefs.getInt(Constants.KAKU_PREF_TIMES_LAUNCHED, 1);
        prefs.edit().putInt(Constants.KAKU_PREF_TIMES_LAUNCHED, timesLaunched + 1).apply();

        startForeground(NOTIFICATION_ID, getNotification());
        isKakuRunning = true;
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

        // Determine if we need to start/stop the capture service
        SharedPreferences prefs = getSharedPreferences(Constants.KAKU_PREF_FILE, Context.MODE_PRIVATE);
        Boolean shown = prefs.getBoolean(Constants.KAKU_PREF_SHOW_HIDE, true);
        if (shown)
        {
            // Re-init CaptureWindow as well as prefs may have changed (BroadcastReceiver go to onStartCommand())
            mWindowCoordinator.getWindow(Constants.WINDOW_CAPTURE).reInit(new Window.ReinitOptions());
        }
        else
        {
            mWindowCoordinator.stopAllWindows();
            stop();
        }

        // Set notification text
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, getNotification());

        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (mWindowCoordinator.hasWindow(Constants.WINDOW_CAPTURE))
        {
            final int rotation = mDisplay.getRotation();

            if (rotation != mRotation)
            {
                Log.d(TAG, "Orientation changed");
                mRotation = rotation;
                createVirtualDisplay();
                mWindowCoordinator.reinitAllWindows();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        stopForeground(true);
        Log.d(TAG, "DESTORYING MAINSERVICE: " + System.identityHashCode(this));

        stop();
        mWindowCoordinator.stopAllWindows();
        mWindowCoordinator = null;
        isKakuRunning = false;

        Log.d(TAG, String.format("MAINSERVICE: %s DESTROYED", System.identityHashCode(this)));
        super.onDestroy();
    }

    @Override
    public void stop()
    {
        if (mMediaProjection != null)
        {
            mMediaProjection.stop();
        }
    }

    public static boolean IsRunning()
    {
        return isKakuRunning;
    }

    /**
     * This function is here as a bug fix against {@link #onConfigurationChanged(Configuration)} not
     * triggering when the app is first started and immediately switches to another orientation. In
     * such a case onConfigurationChanged will not trigger and {@link Window#reInit(ca.fuwafuwa.kaku.Windows.Window.ReinitOptions)} will not
     * update the LayoutParams.
     */
    public void onCaptureWindowFinishedInitializing()
    {
        if (mMediaProjection == null){
            Log.d(TAG, "mMediaProjection is null");
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mProjectionResultCode, mProjectionResultIntent);
            mMediaProjectionStopCallback = new MediaProjectionStopCallback();
            mMediaProjection.registerCallback(mMediaProjectionStopCallback, mHandler);
        }
        createVirtualDisplay();
    }

    public Handler getHandler()
    {
        return mHandler;
    }

    public Image getScreenshot() throws InterruptedException
    {
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

        PendingIntent toggleShowHide = PendingIntent.getBroadcast(this, Constants.REQUEST_SERVICE_TOGGLE_SHOW_HIDE, new Intent(this, ToggleShowHideMainService.class), 0);
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

        Notification n;
        if (prefs.getShowHideSetting())
        {
            n = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.kaku_notification_icon)
                    .setContentTitle(contentTitle)
                    .setContentText(String.format("Instant mode %s, black and white filter %s", prefs.getInstantModeSetting() ? "on" : "off", prefs.getImageFilterSetting() ? "on" : "off"))
                    .setContentIntent(toggleShowHide)
                    .addAction(0, "Instant Mode", toggleInstantMode)
                    .addAction(0, "Image Filter", toggleImagePreview)
                    .addAction(0, "Shutdown", closeMainService)
                    .build();
        }
        else {
            n = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.kaku_notification_icon)
                    .setContentTitle("Kaku is hidden and in power-saving mode")
                    .setContentIntent(toggleShowHide)
                    .build();
        }

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
        Log.d(TAG, String.format("Starting Projection: %dx%d", mRealDisplaySize.x, mRealDisplaySize.y));
        if (mVirtualDisplay != null){
            mVirtualDisplay.release();
        }
        mImageReader = ImageReader.newInstance(mRealDisplaySize.x, mRealDisplaySize.y, PixelFormat.RGBA_8888, 2); // TODO: Something causing a NRE here
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
